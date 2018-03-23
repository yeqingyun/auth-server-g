/*
 * Copyright 2004-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.executor;

import com.gionee.cas.util.RequestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.ExternalContextHolder;
import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.registry.FlowDefinitionLocator;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.execution.FlowExecutionFactory;
import org.springframework.webflow.execution.FlowExecutionKey;
import org.springframework.webflow.execution.repository.FlowExecutionLock;
import org.springframework.webflow.execution.repository.FlowExecutionRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of the central facade for <i>driving</i> the execution of flows within an application.
 * <p>
 * This object is responsible for creating and launching new flow executions as requested by clients, as well as
 * resuming existing, paused executions (that were waiting to be resumed in response to a user event).
 * <p>
 * This object is a facade or entry point into the Spring Web Flow execution system and makes the overall system easier
 * to use. The name <i>executor</i> was chosen as <i>executors drive executions</i>.
 * <p>
 * <b>Commonly used configurable properties</b><br>
 * <table border="1">
 * <tr>
 * <td><b>name</b></td>
 * <td><b>description</b></td>
 * <td><b>default</b></td>
 * </tr>
 * <tr>
 * <td>definitionLocator</td>
 * <td>The service locator responsible for loading flow definitions to execute.</td>
 * <td>None</td>
 * </tr>
 * <tr>
 * <td>executionFactory</td>
 * <td>The factory responsible for creating new flow executions.</td>
 * <td>None</td>
 * </tr>
 * <tr>
 * <td>executionRepository</td>
 * <td>The repository responsible for managing flow execution persistence.</td>
 * <td>None</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Keith Donald
 * @author Erwin Vervaet
 * @author Colin Sampaleanu
 * @see FlowDefinitionLocator
 * @see FlowExecutionFactory
 * @see FlowExecutionRepository
 */
public class FlowExecutorImpl implements FlowExecutor {

    private static final Log logger = LogFactory.getLog(FlowExecutorImpl.class);

    /**
     * The locator to access flow definitions registered in a central registry.
     */
    private FlowDefinitionLocator definitionLocator;

    /**
     * The abstract factory for creating a new execution of a flow definition.
     */
    private FlowExecutionFactory executionFactory;

    /**
     * The repository used to save, update, and load existing flow executions to/from a persistent store.
     */
    private FlowExecutionRepository executionRepository;

    private FlowExecutionKeyRedisTemplate flowExecutionKeyCache;


    private String compatibilityAppVersion;

    private CompatibilityAppVersionCompareTool compatibilityAppVersionCompareTool = new CompatibilityAppVersionCompareTool();


    /**
     * Create a new flow executor.
     *
     * @param definitionLocator   the locator for accessing flow definitions to execute
     * @param executionFactory    the factory for creating executions of flow definitions
     * @param executionRepository the repository for persisting paused flow executions
     */
    public FlowExecutorImpl(FlowDefinitionLocator definitionLocator, FlowExecutionFactory executionFactory,
                            FlowExecutionRepository executionRepository) {
        Assert.notNull(definitionLocator, "The locator for accessing flow definitions is required");
        Assert.notNull(executionFactory, "The execution factory for creating new flow executions is required");
        Assert.notNull(executionRepository, "The repository for persisting flow executions is required");
        this.definitionLocator = definitionLocator;
        this.executionFactory = executionFactory;
        this.executionRepository = executionRepository;

    }

    public void setCompatibilityAppVersion(final String compatibilityAppVersion) {
        this.compatibilityAppVersion = compatibilityAppVersion;
        this.compatibilityAppVersionCompareTool.setVersionString(compatibilityAppVersion);
    }

    public FlowExecutionKeyRedisTemplate getFlowExecutionKeyCache() {
        return flowExecutionKeyCache;
    }

    public void setFlowExecutionKeyCache(FlowExecutionKeyRedisTemplate flowExecutionKeyCache) {
        this.flowExecutionKeyCache = flowExecutionKeyCache;
    }

    /**
     * Returns the locator to load flow definitions to execute.
     */
    public FlowDefinitionLocator getDefinitionLocator() {
        return definitionLocator;
    }

    /**
     * Returns the abstract factory used to create new executions of a flow.
     */
    public FlowExecutionFactory getExecutionFactory() {
        return executionFactory;
    }

    /**
     * Returns the repository used to save, update, and load existing flow executions to/from a persistent store.
     */
    public FlowExecutionRepository getExecutionRepository() {
        return executionRepository;
    }

    public FlowExecutionResult launchExecution(String flowId, MutableAttributeMap<?> input, ExternalContext context)
            throws FlowException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Launching new execution of flow '" + flowId + "' with input " + input);
            }
            ExternalContextHolder.setExternalContext(context);

            FlowDefinition flowDefinition = definitionLocator.getFlowDefinition(flowId);
            FlowExecution flowExecution = executionFactory.createFlowExecution(flowDefinition);
            flowExecution.start(input, context);
            if (!flowExecution.hasEnded()) {
                if (isOldAppLogin(context)) {
                    flowExecutionKeyCache.boundValueOps(getKey(context)).set(flowExecution.getKey(), 200, TimeUnit.SECONDS);
                }
                FlowExecutionLock lock = executionRepository.getLock(flowExecution.getKey());
                lock.lock();
                try {
                    executionRepository.putFlowExecution(flowExecution);
                } finally {
                    lock.unlock();
                }
                return createPausedResult(flowExecution);
            } else {
                return createEndResult(flowExecution);
            }
        } finally {
            ExternalContextHolder.setExternalContext(null);
        }
    }

    public FlowExecutionResult resumeExecution(String flowExecutionKey, ExternalContext context) throws FlowException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Resuming flow execution with key '" + flowExecutionKey);
            }
            ExternalContextHolder.setExternalContext(context);

            FlowExecutionKey key = null;
            if (isOldAppLogin(context)) {
                key = flowExecutionKeyCache.boundValueOps(getKey(context)).get();
            } else {
                key = executionRepository.parseFlowExecutionKey(flowExecutionKey);
            }
            FlowExecutionLock lock = executionRepository.getLock(key);
            lock.lock();
            try {
                FlowExecution flowExecution = executionRepository.getFlowExecution(key);
                flowExecution.resume(context);
                if (!flowExecution.hasEnded()) {
                    executionRepository.putFlowExecution(flowExecution);
                    return createPausedResult(flowExecution);
                } else {
                    executionRepository.removeFlowExecution(flowExecution);
                    return createEndResult(flowExecution);
                }
            } finally {
                lock.unlock();
            }
        } finally {
            ExternalContextHolder.setExternalContext(null);
        }
    }

    private boolean isOldAppLogin(ExternalContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getNativeRequest();
        String version = request.getHeader("user-agent");
        if (version != null && !version.startsWith("Mozilla/5.0") && version.indexOf("Android") != -1) {
            version = version.replace("Android", "").trim();
        }
        if(null == compatibilityAppVersionCompareTool){
            return false;
        }
        return compatibilityAppVersionCompareTool.isCompat(version);
    }

    private String getKey(ExternalContext context) {
        return flowExecutionKeyCache.getKeyByUniqueId(RequestUtils.getUniqueId((HttpServletRequest) context.getNativeRequest()));
    }

    private FlowExecutionResult createEndResult(FlowExecution flowExecution) {
        return FlowExecutionResult.createEndedResult(flowExecution.getDefinition().getId(), flowExecution.getOutcome());
    }

    private FlowExecutionResult createPausedResult(FlowExecution flowExecution) {
        return FlowExecutionResult.createPausedResult(flowExecution.getDefinition().getId(), flowExecution.getKey()
                .toString());
    }

    private static class CompatibilityAppVersionCompareTool {

        private String versionString;

        private List<String> compatList;

        public CompatibilityAppVersionCompareTool() {
            this.compatList = new ArrayList<>();
        }

        public CompatibilityAppVersionCompareTool(String versionString) {
            super();
            this.versionString = versionString;
            for (String version : versionString.split(",")) {
                compatList.add(version);
            }
        }

        public void setVersionString(String versionString) {
            this.versionString = versionString;
        }

        public boolean isCompat(String version) {
            if (compatList.contains(version)) {
                return true;
            }

            return false;
        }
    }

}

package com.gionee.cas.webflow;

import com.gionee.cas.util.RequestUtils;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.executor.FlowExecutionKeyRedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Leon.Yu on 2017/4/15.
 */
public class CompatAppRedisCacheCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompatAppRedisCacheCleaner.class);

    private FlowExecutionKeyRedisTemplate flowExecutionKeyCache;

    public void setFlowExecutionKeyCache(FlowExecutionKeyRedisTemplate flowExecutionKeyCache) {
        this.flowExecutionKeyCache = flowExecutionKeyCache;
    }

    public void clean() {
        HttpServletRequest httpServletRequest = WebUtils.getHttpServletRequest();
        String uniqueId = RequestUtils.getUniqueId(httpServletRequest);
        flowExecutionKeyCache.delete(flowExecutionKeyCache.getKeyByUniqueId(uniqueId));
        LOGGER.debug("clean old client FlowExecutionKey cache, from user : " + httpServletRequest.getParameter("username"));
    }
}

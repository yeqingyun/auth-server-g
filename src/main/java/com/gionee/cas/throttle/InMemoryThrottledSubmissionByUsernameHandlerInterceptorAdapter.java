package com.gionee.cas.throttle;

import com.gionee.cas.throttle.api.LoginRecord;
import com.gionee.cas.throttle.util.LoginThrottleInfoHolder;
import org.jasig.cas.web.support.AbstractThrottledSubmissionHandlerInterceptorAdapter;
import org.jasig.cas.web.support.WebUtils;
import org.jasig.inspektr.common.web.ClientInfoHolder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon.Yu on 2017/4/5.
 */

@Component("inMemoryUsernameThrottle")
public class InMemoryThrottledSubmissionByUsernameHandlerInterceptorAdapter extends AbstractThrottledSubmissionHandlerInterceptorAdapter implements Job {

    private static final double SUBMISSION_RATE_DIVIDEND = 1000.0;
    private static final ConcurrentMap<String, LoginRecord> loginRecordCache = new ConcurrentHashMap<>();
    @Value("${cas.throttle.inmemory.cleaner.repeatinterval:5000}")
    private int refreshInterval;
    @Value("${cas.throttle.inmemory.cleaner.startdelay:5000}")
    private int startDelay;
    @Value("${cas.throttle.show.captcha.threshold:2}")
    private int showCaptchaThreshold;
    @Autowired
    @NotNull
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    @Qualifier("scheduler")
    private Scheduler scheduler;

    /**
     * Schedule throttle job.
     */
    @PostConstruct
    public void scheduleThrottleJob() {
        try {
            if (shouldScheduleCleanerJob()) {
                logger.info("Preparing to schedule throttle job");

                final JobDetail job = JobBuilder.newJob(this.getClass())
                        .withIdentity(this.getClass().getSimpleName().concat(UUID.randomUUID().toString()))
                        .build();

                final Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(this.getClass().getSimpleName().concat(UUID.randomUUID().toString()))
                        .startAt(new Date(System.currentTimeMillis() + this.startDelay))
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMilliseconds(this.refreshInterval)
                                .repeatForever()).build();

                logger.debug("Scheduling {} job", this.getClass().getName());
                scheduler.scheduleJob(job, trigger);
                logger.info("{} will clean tickets every {} seconds",
                        this.getClass().getSimpleName(),
                        TimeUnit.MILLISECONDS.toSeconds(this.refreshInterval));
            }
        } catch (final Exception e) {
            logger.warn(e.getMessage(), e);
        }

    }

    @Override
    public void execute(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        try {
            logger.info("Beginning audit cleanup...");
            decrementCounts();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void decrementCounts() {
        final Set<Map.Entry<String, LoginRecord>> loginRecordEntrySet = loginRecordCache.entrySet();
        logger.debug("Decrementing counts for throttler.  Starting key count: {}", loginRecordEntrySet.size());

        final Date now = new Date();
        for (final Iterator<Map.Entry<String, LoginRecord>> iter = loginRecordEntrySet.iterator(); iter.hasNext(); ) {
            final Map.Entry<String, LoginRecord> entry = iter.next();
            long millSecondsAfterLastFailure = now.getTime() - entry.getValue().getDate().getTime();
            if (millSecondsAfterLastFailure > getFailureRangeInSeconds() * SUBMISSION_RATE_DIVIDEND) {
                String key = entry.getKey();
                logger.trace("Removing entry for key {}", key);
                iter.remove();
                LoginThrottleInfoHolder.get().removeThrottled(key);
            }
        }

        logger.debug("Done decrementing count for throttler.");
    }

    private boolean shouldScheduleCleanerJob() {
        if (this.startDelay > 0 && this.applicationContext.getParent() == null && scheduler != null) {
            if (WebUtils.isCasServletInitializing(this.applicationContext)) {
                logger.debug("Found CAS servlet application context");
                final String[] aliases =
                        this.applicationContext.getAutowireCapableBeanFactory().getAliases("authenticationThrottle");
                logger.debug("{} is used as the active authentication throttle", this.getClass().getSimpleName());
                return aliases.length > 0 && aliases[0].equals(getName());
            }
        }

        return false;
    }

    /**
     * Computes the instantaneous rate in between two given dates corresponding to two submissions.
     *
     * @param a First date.
     * @param b Second date.
     * @return Instantaneous submission rate in submissions/sec, e.g. {@code a - b}.
     */
    protected double submissionRate(final Date a, final Date b) {
        return SUBMISSION_RATE_DIVIDEND / (a.getTime() - b.getTime());
    }

    @Override
    protected final boolean exceedsThreshold(final HttpServletRequest request) {
        String ipAddress = constructKey(request);
        if (loginRecordCache.containsKey(ipAddress)) {
            final Date last = loginRecordCache.get(ipAddress).getDate();
            if (last == null) {
                return false;
            }

            return submissionRate(new Date(), last) > getThresholdRate();
        }

        return false;
    }

    @Override
    protected final void recordSubmissionFailure(final HttpServletRequest request) {
        String ipAddress = constructKey(request);
        if (loginRecordCache.containsKey(ipAddress)) {
            LoginRecord loginRecord = loginRecordCache.get(ipAddress);
            LoginRecord latestLoginRecord = new LoginRecord(loginRecord.getFailureTimesInRage() + 1);
            loginRecordCache.put(ipAddress, latestLoginRecord);

            if (latestLoginRecord.getFailureTimesInRage() >= this.showCaptchaThreshold) {
                LoginThrottleInfoHolder.get().setThrottled(ipAddress);
            }
        } else {
            loginRecordCache.put(ipAddress, new LoginRecord(1));
        }
    }

    @Override
    protected String getName() {
        return "inMemoryUsernameThrottle";
    }

    protected String constructKey(HttpServletRequest request) {
        return ClientInfoHolder.getClientInfo().getClientIpAddress();
    }
}

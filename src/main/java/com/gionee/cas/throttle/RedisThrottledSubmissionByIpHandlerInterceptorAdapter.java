package com.gionee.cas.throttle;

import com.gionee.cas.throttle.api.LoginRecord;
import com.gionee.cas.throttle.util.LoginThrottleInfoHolder;
import com.gionee.cas.util.RequestUtils;
import org.jasig.cas.web.support.AbstractThrottledSubmissionHandlerInterceptorAdapter;
import org.jasig.cas.web.support.WebUtils;
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
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon.Yu on 2017/4/11.
 */
@Component("redisIpThrottle")
public class RedisThrottledSubmissionByIpHandlerInterceptorAdapter extends AbstractThrottledSubmissionHandlerInterceptorAdapter implements Job {
    public static final String LOGIN_RECORD_PREFIX = "LOGIN_RECORD.";
    private static final double SUBMISSION_RATE_DIVIDEND = 1000.0;
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

    @Resource(name = "loginRecordRedisTemplate")
    private RedisTemplate<String, LoginRecord> loginRecordRedisTemplate;

    /**
     * Schedule throttle job.
     */
    @PostConstruct
    public void scheduleThrottleJob() {
        try {
            if (shouldScheduleCleanerJob()) {
                logger.debug("Preparing to schedule throttle job");

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
                logger.debug("{} will clean tickets every {} seconds",
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
            logger.debug("Beginning audit cleanup...");
            decrementCounts();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void decrementCounts() {
        Set<String> keys = loginRecordRedisTemplate.keys(LOGIN_RECORD_PREFIX + "*");
        logger.debug("Decrementing counts for throttler.  Starting key count: {}", keys.size());
        final Date now = new Date();
        for (String key : keys) {
            BoundValueOperations<String, LoginRecord> loginRecordBoundValueOperations = loginRecordRedisTemplate.boundValueOps(key);
            LoginRecord loginRecord = loginRecordBoundValueOperations.get();
            if (loginRecord != null) {
                long millSecondsAfterLastFailure = now.getTime() - loginRecord.getDate().getTime();
                if (millSecondsAfterLastFailure > getFailureRangeInSeconds() * SUBMISSION_RATE_DIVIDEND) {
                    int failureTimes = loginRecord.getFailureTimesInRage() - 1;
                    // only failure times equals to 0, is throttle cache can be clear.
                    if (failureTimes <= 0) {
                        loginRecordRedisTemplate.delete(key);
                        LoginThrottleInfoHolder.get().removeThrottled(realKey(key));
                        logger.debug("Removing entry for key {}", key);
                    } else {
                        loginRecord.setFailureTimesInRage(failureTimes);
                        loginRecordBoundValueOperations.set(loginRecord);
                    }
                }
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

    private String realKey(String constructKey) {
        return constructKey.replace(LOGIN_RECORD_PREFIX, "");
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
        String username = constructKey(request);
        if (loginRecordRedisTemplate.hasKey(username)) {
            LoginRecord loginRecord = loginRecordRedisTemplate.boundValueOps(username).get();
            if (loginRecord == null) {
                loginRecordRedisTemplate.delete(username);
                return false;
            } else {
                final Date last = loginRecord.getDate();
                if (last == null) {
                    return false;
                }

                return submissionRate(new Date(), last) > getThresholdRate();
            }
        }

        return false;
    }

    @Override
    protected final void recordSubmissionFailure(final HttpServletRequest request) {
        String usernameKey = constructKey(request);
        if (loginRecordRedisTemplate.hasKey(usernameKey)) {
            BoundValueOperations<String, LoginRecord> loginRecordBoundValueOperations = loginRecordRedisTemplate.boundValueOps(usernameKey);
            LoginRecord loginRecord = loginRecordBoundValueOperations.get();
            LoginRecord latestLoginRecord = new LoginRecord(loginRecord.getFailureTimesInRage() + 1);
            loginRecordBoundValueOperations.set(latestLoginRecord);

            if (latestLoginRecord.getFailureTimesInRage() >= this.showCaptchaThreshold) {
                LoginThrottleInfoHolder.get().setThrottled(realKey(usernameKey));
            }
        } else {
            loginRecordRedisTemplate.boundValueOps(usernameKey).set(new LoginRecord(1));
        }
    }

    @Override
    protected String getName() {
        return "redisIpThrottle";
    }

    protected String constructKey(HttpServletRequest request) {
        return LOGIN_RECORD_PREFIX + RequestUtils.getUniqueId(request);
    }
}

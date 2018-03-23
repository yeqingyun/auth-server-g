package com.gionee.cas.ticket;

import org.jasig.cas.authentication.RememberMeCredential;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.AbstractTicketRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Key-value ticket ticket implementation that stores tickets in redis keyed on the ticket ID.
 *
 * @author serv
 * @since 5.1.0
 */
@Component("redisTicketRegistry")
public class RedisTicketRegistry extends AbstractTicketRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTicketRegistry.class);
    private static final String CAS_TICKET_PREFIX = "CAS_TICKET:";
    private static final String NO_REDIS_CLIENT_IS_DEFINED = "No redis client is defined.";

    @Autowired
    private RedisTemplate<String, Ticket> client;

    @Min(0)
    @Value("${tgt.maxTimeToLiveInSeconds:28800}")
    private int tgtTimeout;

    @Min(0)
    @Value("${tgc.remember.me.maxAge:1209600}")
    private int tgtRememberMeTimeout;

    @Min(0)
    @Value("${st.timeToKillInSeconds:10}")
    private int stTimeout;

    @Value("${redis.cleaner.scan.count:1000}")
    private int scanCount;

    public long deleteAll() {
        final Set<String> redisKeys = this.client.keys(getPatternTicketRedisKey());
        final int size = redisKeys.size();
        this.client.delete(redisKeys);
        return size;
    }

    @Override
    public boolean deleteSingleTicket(final String ticketId) {
        Assert.notNull(this.client, NO_REDIS_CLIENT_IS_DEFINED);
        try {
            final String redisKey = getTicketRedisKey(ticketId);
            this.client.delete(redisKey);
            return true;
        } catch (final Exception e) {
            LOGGER.error("Ticket not found or is already removed. Failed deleting [{}]", ticketId, e);
        }
        return false;
    }


    @Override
    public void addTicket(final Ticket ticket) {
        Assert.notNull(this.client, NO_REDIS_CLIENT_IS_DEFINED);
        try {
            LOGGER.debug("Adding ticket [{}]", ticket);
            final String redisKey = this.getTicketRedisKey(ticket.getId());
            // Encode first, then add
            final Ticket encodeTicket = this.encodeTicket(ticket);
            this.client.boundValueOps(redisKey)
                    .set(encodeTicket, getTimeout(ticket), TimeUnit.SECONDS);
        } catch (final Exception e) {
            LOGGER.error("Failed to add [{}]", ticket);
        }
    }

    @Override
    public Ticket getTicket(final String ticketId) {
        Assert.notNull(this.client, NO_REDIS_CLIENT_IS_DEFINED);
        try {
            final String redisKey = this.getTicketRedisKey(ticketId);
            final Ticket t = this.client.boundValueOps(redisKey).get();
            if (t != null) {
                //Decoding add first
                return decodeTicket(t);
            }
        } catch (final Exception e) {
            LOGGER.error("Failed fetching [{}] ", ticketId, e);
        }
        return null;
    }

    /**
     * do every ticket operation in redis scan, avoid big mount of tickets iteration for causing bad performance.
     *
     * @param template
     * @return
     */
    public Integer doInRedisScan(final RedisTicketOperationTemplate template) {
        Assert.notNull(template, "Redis ticket operation template can't be null.");

        return this.client.execute(new RedisCallback<Integer>() {
            @Override
            public Integer doInRedis(RedisConnection connection) throws DataAccessException {
                ScanOptions scanOptions = ScanOptions.scanOptions().count(scanCount).match(getPatternTicketRedisKey()).build();
                RedisSerializer<?> ticketSerializer = client.getValueSerializer();
                Cursor<byte[]> cursor = connection.scan(scanOptions);
                int count = 0;
                try {
                    while (cursor.hasNext()) {
                        Ticket ticket = (Ticket) ticketSerializer.deserialize(connection.get(cursor.next()));
                        boolean operateResult = template.doInRedisScan(ticket);
                        if (operateResult) {
                            count++;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                return count;
            }
        });

    }


    @Override
    public Collection<Ticket> getTickets() {
        Assert.notNull(this.client, NO_REDIS_CLIENT_IS_DEFINED);

        final Set<Ticket> tickets = new HashSet<>();
        final Set<String> redisKeys = this.client.keys(this.getPatternTicketRedisKey());
        for (String redisKey : redisKeys) {
            final Ticket ticket = this.client.boundValueOps(redisKey).get();
            if (ticket == null) {
                this.client.delete(redisKey);
            } else {
                // Decoding add first
                tickets.add(this.decodeTicket(ticket));
            }
        }
        return tickets;
    }

    public Ticket updateTicket(final Ticket ticket) {
        Assert.notNull(this.client, NO_REDIS_CLIENT_IS_DEFINED);
        try {
            LOGGER.debug("Updating ticket [{}]", ticket);
            final Ticket encodeTicket = this.encodeTicket(ticket);
            final String redisKey = this.getTicketRedisKey(ticket.getId());
            this.client.boundValueOps(redisKey).set(encodeTicket, getTimeout(ticket), TimeUnit.SECONDS);
            return encodeTicket;
        } catch (final Exception e) {
            LOGGER.error("Failed to update [{}]", ticket);
        }
        return null;
    }

    private boolean isRememberMeTicket(final TicketGrantingTicket ticket) {
        final Boolean isRememberMeTicket = (Boolean) ticket.getAuthentication().getAttributes().
                get(RememberMeCredential.AUTHENTICATION_ATTRIBUTE_REMEMBER_ME);
        return isRememberMeTicket != null && isRememberMeTicket.booleanValue();
    }

    /**
     * If not time out value is specified, expire the ticket immediately.
     *
     * @param ticket the ticket
     * @return timeout
     */
    private int getTimeout(final Ticket ticket) {
        if (ticket instanceof TicketGrantingTicket) {
            TicketGrantingTicket ticketGrantingTicket = (TicketGrantingTicket) ticket;
            if (isRememberMeTicket(ticketGrantingTicket)) {
                return tgtRememberMeTimeout;
            } else {
                return tgtTimeout;
            }
        } else if (ticket instanceof ServiceTicket) {
            return stTimeout;
        }
        throw new IllegalArgumentException("Invalid ticket type");
    }

    // Add a prefix as the key of redis
    private String getTicketRedisKey(final String ticketId) {
        return CAS_TICKET_PREFIX + ticketId;
    }

    // pattern all ticket redisKey
    private String getPatternTicketRedisKey() {
        return CAS_TICKET_PREFIX + "*";
    }

    /**
     * Encode ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    protected Ticket encodeTicket(final Ticket ticket) {
        return ticket;
    }

    /**
     * Decode ticket.
     *
     * @param result the result
     * @return the ticket
     */
    protected Ticket decodeTicket(final Ticket result) {
        return result;
    }

}

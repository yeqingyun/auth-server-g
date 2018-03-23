package com.gionee.cas.ticket;

import org.jasig.cas.ticket.Ticket;

/**
 * Created by Leon.Yu on 2017/7/31.
 */
public interface RedisTicketOperationTemplate {

    /**
     * @param ticket
     * @return indicate template method operate successful,for doInRedisScan count total successful event.
     */
    boolean doInRedisScan(final Ticket ticket);
}

package com.gionee.cas.throttle.api;

import org.jasig.inspektr.common.web.ClientInfoHolder;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Leon.Yu on 2017/4/6.
 */
public class LoginRecord implements Serializable{
    private Date date;
    private String ipAddress;
    private String username;
    private Integer failureTimesInRage;

    public LoginRecord() {
        this.ipAddress = ClientInfoHolder.getClientInfo().getClientIpAddress();
        this.date = new Date();
        this.failureTimesInRage = 0;
    }

    public LoginRecord(Integer failureTimesInRage) {
        this.ipAddress = ClientInfoHolder.getClientInfo().getClientIpAddress();
        this.date = new Date();
        this.failureTimesInRage = failureTimesInRage;
    }

    public Integer getFailureTimesInRage() {
        return failureTimesInRage;
    }

    public void setFailureTimesInRage(Integer failureTimesInRage) {
        this.failureTimesInRage = failureTimesInRage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int compareTo(LoginRecord loginRecord) {
        if (ipAddress != null && loginRecord.getIpAddress() != null) {
            if (ipAddress.equals(loginRecord.getIpAddress())) {
                return date.compareTo(loginRecord.getDate());
            }
        }

        return -1;
    }
}

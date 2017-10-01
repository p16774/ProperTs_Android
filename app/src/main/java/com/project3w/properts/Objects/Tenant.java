package com.project3w.properts.Objects;

import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Tenant {

    // class variables
    private String tenantID, userID, tenantName, tenantAddress, tenantEmail;
    private long tenantPhone;
    private Map<String,Boolean> tenantMessageIds;

    private Tenant() {
        // used for firebase
    }

    Tenant(String tenantName, String tenantAddress, String tenantEmail, long tenantPhone) {
        this.tenantName = tenantName;
        this.tenantAddress = tenantAddress;
        this.tenantEmail = tenantEmail;
        this.tenantPhone = tenantPhone;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantAddress() {
        return tenantAddress;
    }

    public void setTenantAddress(String tenantAddress) {
        this.tenantAddress = tenantAddress;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public void setTenantEmail(String tenantEmail) {
        this.tenantEmail = tenantEmail;
    }

    public long getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(long tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public Map<String, Boolean> getTenantMessageIds() {
        return tenantMessageIds;
    }

    public void setTenantMessageIds(Map<String, Boolean> tenantMessageIds) {
        this.tenantMessageIds = tenantMessageIds;
    }
}

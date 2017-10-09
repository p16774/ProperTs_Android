package com.project3w.properts.Objects;

import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Tenant {

    // class variables
    private String tenantID, userID, tenantName, tenantAddress, tenantEmail, tenantPhone, tenantMoveInDate, tenantDeposit, tenantKeys, tenantOccupants;
    private Map<String,Boolean> tenantMessageIds;

    private Tenant() {
        // used for firebase
    }

    public Tenant(String tenantName, String tenantAddress, String tenantEmail, String tenantPhone, String tenantMoveInDate, String tenantDeposit, String tenantKeys, String tenantOccupants) {
        this.tenantName = tenantName;
        this.tenantAddress = tenantAddress;
        this.tenantEmail = tenantEmail;
        this.tenantMoveInDate = tenantMoveInDate;
        this.tenantDeposit = tenantDeposit;
        this.tenantKeys = tenantKeys;
        this.tenantOccupants = tenantOccupants;
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

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getTenantMoveInDate() {
        return tenantMoveInDate;
    }

    public void setTenantMoveInDate(String tenantMoveInDate) {
        this.tenantMoveInDate = tenantMoveInDate;
    }

    public String getTenantDeposit() {
        return tenantDeposit;
    }

    public void setTenantDeposit(String tenantDeposit) {
        this.tenantDeposit = tenantDeposit;
    }

    public String getTenantKeys() {
        return tenantKeys;
    }

    public void setTenantKeys(String tenantKeys) {
        this.tenantKeys = tenantKeys;
    }

    public String getTenantOccupants() {
        return tenantOccupants;
    }

    public void setTenantOccupants(String tenantOccupants) {
        this.tenantOccupants = tenantOccupants;
    }

    public Map<String, Boolean> getTenantMessageIds() {
        return tenantMessageIds;
    }

    public void setTenantMessageIds(Map<String, Boolean> tenantMessageIds) {
        this.tenantMessageIds = tenantMessageIds;
    }
}

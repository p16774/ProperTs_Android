package com.project3w.newproperts.Objects;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Tenant implements Serializable {

    // class variables
    private String tenantID, userID, tenantFirstName, tenantLastName, tenantAddress, tenantEmail, tenantPhone, tenantMoveInDate, tenantDeposit, tenantKeys, tenantOccupants;
    private Boolean tenantStatus;

    public Tenant() {
        // used for firebase
    }

    public Tenant(String tenantFirstName, String tenantLastName, String tenantAddress, String tenantEmail, String tenantPhone, String tenantMoveInDate, String tenantDeposit, String tenantKeys, String tenantOccupants) {
        this.tenantFirstName = tenantFirstName;
        this.tenantLastName = tenantLastName;
        this.tenantAddress = tenantAddress;
        this.tenantEmail = tenantEmail;
        this.tenantMoveInDate = tenantMoveInDate;
        this.tenantDeposit = tenantDeposit;
        this.tenantKeys = tenantKeys;
        this.tenantOccupants = tenantOccupants;
        this.tenantPhone = tenantPhone;
        this.tenantStatus = true;
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

    public String getTenantFirstName() {
        return tenantFirstName;
    }

    public void setTenantFirstName(String tenantFirstName) {
        this.tenantFirstName = tenantFirstName;
    }

    public String getTenantLastName() {
        return tenantLastName;
    }

    public void setTenantLastName(String tenantLastName) {
        this.tenantLastName = tenantLastName;
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

    public Boolean getTenantStatus() {
        return tenantStatus;
    }

    public void setTenantStatus(Boolean tenantStatus) {
        this.tenantStatus = tenantStatus;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "tenantID='" + tenantID + '\'' +
                ", userID='" + userID + '\'' +
                ", tenantFirstName='" + tenantFirstName + '\'' +
                ", tenantLastName='" + tenantLastName + '\'' +
                ", tenantAddress='" + tenantAddress + '\'' +
                ", tenantEmail='" + tenantEmail + '\'' +
                ", tenantPhone='" + tenantPhone + '\'' +
                ", tenantMoveInDate='" + tenantMoveInDate + '\'' +
                ", tenantDeposit='" + tenantDeposit + '\'' +
                ", tenantKeys='" + tenantKeys + '\'' +
                ", tenantOccupants='" + tenantOccupants + '\'' +
                '}';
    }
}

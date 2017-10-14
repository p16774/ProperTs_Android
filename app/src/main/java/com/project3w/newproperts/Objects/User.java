package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/13/17.
 */

public class User {

    // class variables
    String companyCode, propertyCode, tenantID, accessRole;

    public User() {
    }

    public User(String companyCode, String propertyCode, String tenantID, String accessRole) {
        this.companyCode = companyCode;
        this.propertyCode = propertyCode;
        this.tenantID = tenantID;
        this.accessRole = accessRole;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

}

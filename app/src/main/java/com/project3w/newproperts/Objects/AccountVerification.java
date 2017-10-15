package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/1/17.
 */

public class AccountVerification {

    // class variables
    private String tenantLastName, tenantAddress;

    public AccountVerification() {
        // needed for Firebase
    }

    public AccountVerification(String tenantName, String tenantAddress) {
        this.tenantLastName = tenantName;
        this.tenantAddress = tenantAddress;
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
}

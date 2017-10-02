package com.project3w.properts.Objects;

/**
 * Created by Nate on 10/1/17.
 */

public class AccountVerification {

    // class variables
    private String tenantName, tenantAddress;

    public AccountVerification() {
        // needed for Firebase
    }

    public AccountVerification(String tenantName, String tenantAddress) {
        this.tenantName = tenantName;
        this.tenantAddress = tenantAddress;
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
}

package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/1/17.
 */

public class TenantVerification {

    // class variables
    private String tenantLastName, tenantAddress, companyCode;

    public TenantVerification() {
        // needed for Firebase
    }

    public TenantVerification(String tenantName, String tenantAddress, String companyCode) {
        this.tenantLastName = tenantName;
        this.tenantAddress = tenantAddress;
        this.companyCode = companyCode;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}

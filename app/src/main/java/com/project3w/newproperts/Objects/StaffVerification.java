package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/20/17.
 */

public class StaffVerification {

    // class variables
    String staffName, staffKey, companyCode;

    public StaffVerification() {
    }

    public StaffVerification(String staffName, String staffKey, String companyCode) {
        this.staffName = staffName;
        this.staffKey = staffKey;
        this.companyCode = companyCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffKey() {
        return staffKey;
    }

    public void setStaffKey(String staffKey) {
        this.staffKey = staffKey;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}

package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/13/17.
 */

public class Company {

    // class variables
    String companyCode, companyName, companyAddress, companyPhone, companyHours, managerName;

    // firebase constructor
    public Company() {
        // used for Firebase Implementation
    }

    // new company creation constructor
    public Company(String companyCode, String companyName) {
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyAddress = ""; // set these to empty values that user can update later
        this.companyPhone = "";
        this.companyHours = "";
        this.managerName = "";
    }

    public Company(String companyCode, String companyName, String companyAddress, String companyPhone, String companyHours, String managerName) {
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyHours = companyHours;
        this.managerName = managerName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyHours() {
        return companyHours;
    }

    public void setCompanyHours(String companyHours) {
        this.companyHours = companyHours;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}

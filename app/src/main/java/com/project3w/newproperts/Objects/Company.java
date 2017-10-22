package com.project3w.newproperts.Objects;

/**
 * Created by Nate on 10/13/17.
 */

public class Company {

    // class variables
    private String companyCode, companyName, companyAddress, companyPhone, companyEmail, companyHours, companyImagePath, managerName;

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
        this.companyEmail = "";
        this.companyHours = "";
        this.companyImagePath = "";
        this.managerName = "";
    }

    public Company(String companyCode, String companyName, String companyAddress, String companyPhone, String companyEmail, String companyHours, String companyImagePath, String managerName) {
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.companyHours = companyHours;
        this.companyImagePath = companyImagePath;
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

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyHours() {
        return companyHours;
    }

    public void setCompanyHours(String companyHours) {
        this.companyHours = companyHours;
    }

    public String getCompanyImagePath() {
        return companyImagePath;
    }

    public void setCompanyImagePath(String companyImagePath) {
        this.companyImagePath = companyImagePath;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", companyPhone='" + companyPhone + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", companyHours='" + companyHours + '\'' +
                ", companyImagePath='" + companyImagePath + '\'' +
                ", managerName='" + managerName + '\'' +
                '}';
    }
}

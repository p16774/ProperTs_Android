package com.project3w.newproperts.Objects;

import java.io.Serializable;

/**
 * Created by Nate on 9/30/17.
 */

public class Staff implements Serializable {

    // class variables
    String staffID, staffName, staffEmail, staffPhone, staffAccess, userID;

    public Staff() {
    }

    public Staff(String staffName, String staffEmail, String staffPhone) {
        this.staffID = "";
        this.staffName = staffName;
        this.staffEmail = staffEmail;
        this.staffPhone = staffPhone;
        this.staffAccess = "staff";
        this.userID = "";
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffAccess() {
        return staffAccess;
    }

    public void setStaffAccess(String staffAccess) {
        this.staffAccess = staffAccess;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffID='" + staffID + '\'' +
                ", staffName='" + staffName + '\'' +
                ", staffEmail='" + staffEmail + '\'' +
                ", staffPhone='" + staffPhone + '\'' +
                ", staffAccess='" + staffAccess + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}

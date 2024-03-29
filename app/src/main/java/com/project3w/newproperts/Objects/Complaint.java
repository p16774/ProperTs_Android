package com.project3w.newproperts.Objects;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Complaint implements Serializable {

    // class variables
    private String complaintID, complaintTitle, complaintContent, complaintStatus, complaintDate, complaintUser;

    public Complaint() {
    }

    public Complaint(String complaintTitle, String complaintContent, String complaintStatus, String complaintDate, String complaintUser) {
        this.complaintTitle = complaintTitle;
        this.complaintContent = complaintContent;
        this.complaintStatus = complaintStatus;
        this.complaintDate = complaintDate;
        this.complaintUser = complaintUser;
    }

    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }

    public String getComplaintTitle() {
        return complaintTitle;
    }

    public void setComplaintTitle(String complaintTitle) {
        this.complaintTitle = complaintTitle;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(String complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getComplaintUser() {
        return complaintUser;
    }

    public void setComplaintUser(String complaintUser) {
        this.complaintUser = complaintUser;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintID='" + complaintID + '\'' +
                ", complaintTitle='" + complaintTitle + '\'' +
                ", complaintContent='" + complaintContent + '\'' +
                ", complaintStatus='" + complaintStatus + '\'' +
                ", complaintDate='" + complaintDate + '\'' +
                ", complaintUser='" + complaintUser + '\'' +
                '}';
    }
}

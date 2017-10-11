package com.project3w.properts.Objects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Complaint {

    // class variables
    private String complaintID, complaintTitle, complaintContent, complaintStatus, complaintDate;

    public Complaint() {
    }

    public Complaint(String complaintTitle, String complaintContent, String complaintStatus, String complaintDate) {
        this.complaintTitle = complaintTitle;
        this.complaintContent = complaintContent;
        this.complaintStatus = complaintStatus;
        this.complaintDate = complaintDate;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("complaintID", complaintID);
        result.put("complaintTitle", complaintTitle);
        result.put("complaintContent", complaintContent);
        result.put("complaintDate", complaintDate);
        result.put("complaintStatus", complaintStatus);

        return result;
    }
}

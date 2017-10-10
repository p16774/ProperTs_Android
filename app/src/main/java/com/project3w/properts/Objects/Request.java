package com.project3w.properts.Objects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nate on 9/30/17.
 */

public class Request {

    // class variables
    private String requestID, requestTitle, requestContent, requestUrgency, requestDate,
            requestStatus, requestOpenImagePath, requestClosedImagePath;

    public Request() {
    }

    public Request(String requestTitle,
                   String requestContent,
                   String requestUrgency,
                   String requestDate,
                   String requestStatus,
                   String requestImagePath) {
        this.requestTitle = requestTitle;
        this.requestContent = requestContent;
        this.requestUrgency = requestUrgency;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
        this.requestOpenImagePath = requestImagePath;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(String requestContent) {
        this.requestContent = requestContent;
    }

    public String getRequestUrgency() {
        return requestUrgency;
    }

    public void setRequestUrgency(String requestUrgency) {
        this.requestUrgency = requestUrgency;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestOpenImagePath() {
        return requestOpenImagePath;
    }

    public void setRequestOpenImagePath(String requestOpenImagePath) {
        this.requestOpenImagePath = requestOpenImagePath;
    }

    public String getRequestClosedImagePath() {
        return requestClosedImagePath;
    }

    public void setRequestClosedImagePath(String requestClosedImagePath) {
        this.requestClosedImagePath = requestClosedImagePath;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("requestID", requestID);
        result.put("requestTitle", requestTitle);
        result.put("requestContent", requestContent);
        result.put("requestUrgency", requestUrgency);
        result.put("requestDate", requestDate);
        result.put("requestStatus", requestStatus);
        result.put("requestOpenImagePath", requestOpenImagePath);
        result.put("requestClosedImagePath", requestClosedImagePath);

        return result;
    }
}

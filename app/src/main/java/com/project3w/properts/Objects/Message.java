package com.project3w.properts.Objects;

import java.util.Date;

/**
 * Created by Nate on 9/30/17.
 */

public class Message {

    // class variables
    private String messageId;
    private String messageContent;
    private Date messageDate;

    private Message() {
    }

    Message(String messageId, String messageContent, Date messageDate) {

        this.messageId = messageId;
        this.messageContent = messageContent;
        this.messageDate = messageDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

}

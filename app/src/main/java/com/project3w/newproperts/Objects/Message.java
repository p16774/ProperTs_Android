package com.project3w.newproperts.Objects;

import java.util.Date;

/**
 * Created by Nate on 9/30/17.
 */

public class Message {

    // class variables
    private String messageContent, messageSender;
    private long messageDate;

    private Message() {
    }

    public Message(String messageContent, String messageSender, long messageDate) {
        this.messageContent = messageContent;
        this.messageSender = messageSender;
        this.messageDate = messageDate;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public long getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(long messageDate) {
        this.messageDate = messageDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", messageContent='" + messageContent + '\'' +
                ", messageSender='" + messageSender + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }
}

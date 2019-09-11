package ru.fleetcor.mailer;

import java.util.List;

/**
 * Created by Ivan.Zhirnov on 27.07.2018.
 */
public class Message {
    private String id;
    private String subject;
    private String body;
    private List<Addressee> addressees;
    private List<Attachment> attachments;
    private MessageState state;
    private String priority;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<Addressee> getAddressees() { return addressees; }

    public void setAddressees(List<Addressee> addressees) {
        this.addressees = addressees;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public enum MessageState {
        SENT(-1),
        LISTED(0),
        IN_PROGRESS(1),
        ERROR(2);

        private Integer value;

        MessageState(Integer stateCode){
            value = stateCode;
        }

        public Integer getValue(){
            return value;
        }

    }
}

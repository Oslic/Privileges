package ru.fleetcor.mailer;

/**
 * Created by Ivan.Zhirnov on 27.07.2018.
 */
public class Attachment {
    private String name;
    private String type;
    private byte[] body;
    private String cid;

    public Attachment(String name, byte[] body, String type, String cid) {
        this.name = name;
        this.type = type;
        this.body = body;
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}

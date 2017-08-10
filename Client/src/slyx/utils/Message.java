package slyx.utils;

import slyx.libs.JSONObject;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Message {
    private User to;
    private Date sent;
    private String content;

    public Message(User to, Date sent, String content) {
        this.to = to;
        this.sent = sent;
        this.content = content;
    }

    public User getTo() { return to; }
    public void setTo(User to) { this.to = to; }
    public Date getSent() { return sent; }
    public void setSent(Date sent) { this.sent = sent; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Message{" +
                ", to=" + this.to +
                ", sent=" + this.sent +
                ", content='" + this.content + '\'' +
                '}';
    }

    public JSONObject toObject() {
        JSONObject o = new JSONObject();
        o.put("to", this.to.getId());
        o.put("sent", this.sent);
        o.put("content", this.content);
        System.out.println(o);
        return o;
    }
}

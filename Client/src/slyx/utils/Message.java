package slyx.utils;

import slyx.libs.JSONObject;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Message {
    private int id;
    private User from;
    private User to;
    private Date sent;
    private String content;

    public Message(int id, User from, User to, Date sent, String content) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.sent = sent;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }
    public User getTo() { return to; }
    public void setTo(User to) { this.to = to; }
    public Date getSent() { return sent; }
    public void setSent(Date sent) { this.sent = sent; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Message{" +
                ", from=" + this.from.getId() +
                ", to=" + this.to.getId() +
                ", sent=" + this.sent +
                ", content='" + this.content + '\'' +
                '}';
    }

    public JSONObject toObject() {
        JSONObject o = new JSONObject();
        o.put("from", this.from.getId());
        o.put("to", this.to.getId());
        o.put("sent", this.sent.getTime());
        o.put("content", this.content);
        return o;
    }
}

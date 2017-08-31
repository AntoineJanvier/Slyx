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
    private int to;
    private Date sent;
    private String content;
    private String inOrOut;

    public boolean printed = false;

    public Message(int id, User from, int to, Date sent, String content, String inOrOut) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.sent = sent;
        this.content = content;
        this.inOrOut = inOrOut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }
    public int getTo() { return to; }
    public void setTo(int to) { this.to = to; }
    public Date getSent() { return sent; }
    public void setSent(Date sent) { this.sent = sent; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getInOrOut() { return inOrOut; }
    public void setInOrOut(String inOrOut) { this.inOrOut = inOrOut; }

    @Override
    public String toString() {
        return "Message{" +
                ", from=" + this.from.getId() +
                ", to=" + this.to +
                ", sent=" + this.sent +
                ", content='" + this.content + '\'' +
                ", inOrOut='" + this.inOrOut + '\'' +
                '}';
    }

    public JSONObject toObject() {
        JSONObject o = new JSONObject();
        o.put("from", this.from.getId());
        o.put("to", this.to);
        o.put("sent", this.sent.getTime());
        o.put("content", this.content);
        return o;
    }
}

package slyx.utils;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Message {
    private User from;
    private User to;
    private Date sent;
    private String content;

    public Message(User from, User to, String content) {
        this.from = from;
        this.to = to;
        this.sent = new Date();
        this.content = content;
    }

    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }
    public User getTo() { return to; }
    public void setTo(User to) { this.to = to; }
    public Date getSent() { return sent; }
    public void setSent(Date sent) { this.sent = sent; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}

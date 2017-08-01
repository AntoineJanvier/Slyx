package slyx.utils;

import java.util.Date;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class Call {
    private User from;
    private User to;
    private Date begin;
    private Date end;
    private Date duration;

    public Call(User from, User to) {
        this.from = from;
        this.to = to;
    }

    public void start() {
        this.begin = new Date();
        this.end = null;
        this.duration = null;
    }
    public void end() {
        this.end = new Date();
        this.duration = getDuration();
    }

    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }
    public User getTo() { return to; }
    public void setTo(User to) { this.to = to; }
    public Date getBegin() { return begin; }
    public void setBegin(Date begin) { this.begin = begin; }
    public Date getEnd() { return end; }
    public void setEnd(Date end) { this.end = end; }
    public Date getDuration() {
        setDuration(new Date(new Date().getTime() - this.begin.getTime()));
        return duration;
    }
    public void setDuration(Date duration) { this.duration = duration; }
}

package slyx.utils;

/**
 * Created by Antoine Janvier
 * on 01/08/17.
 */
public class History {
    private Message message;
    private Call call;
    private User from;
    private User to;

    public History(User from, User to) {
        this.from = from;
        this.to = to;
    }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
    public Call getCall() { return call; }
    public void setCall(Call call) { this.call = call; }
    public User getFrom() { return from; }
    public void setFrom(User from) { this.from = from; }
    public User getTo() { return to; }
    public void setTo(User to) { this.to = to; }
}

package innovate.ebad.com.instantmessenger.Model;


public class Message {

    private String message, time, userName, type, uri;

    public Message(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public Message(String message, String time, String userName) {
        this.message = message;
        this.time = time;
        this.userName = userName;
    }

    public Message(String message, String time, String userName, String type, String uri) {
        this.message = message;
        this.time = time;
        this.userName = userName;
        this.type = type;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}

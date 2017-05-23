package innovate.ebad.com.instantmessenger.Model;

/**
 * Created by Ebad on 14/5/2017.
 */


public class MessageModel {
    // if boolean is true it means the message is from your friend and false mean its from you

    String message, messageTime, uri, type;
    boolean check;

    public MessageModel(String message, String messageTime, boolean check) {
        this.message = message;
        this.messageTime = messageTime;
        this.uri = uri;
        this.check = check;
    }

    public String getType() {
        return type;
    }

    public MessageModel(String message, String messageTime, String type, boolean check,String uri) {
        this.message = message;
        this.messageTime = messageTime;
        this.type = type;
        this.check = check;
        this.uri = uri;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getUri() {
        return uri;
    }

    public boolean isCheck() {
        return check;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}

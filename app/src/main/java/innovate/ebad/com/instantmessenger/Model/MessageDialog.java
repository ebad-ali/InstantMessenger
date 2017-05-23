package innovate.ebad.com.instantmessenger.Model;


public class MessageDialog {

    String fullName, messageTime, lastMessage, picId, userName;
    int unreadMessages;

    public MessageDialog(String fullName, String messageTime, String lastMessage, String picId, int unreadMessages, String userName) {
        this.fullName = fullName;
        this.messageTime = messageTime;
        this.lastMessage = lastMessage;
        this.picId = picId;
        this.unreadMessages = unreadMessages;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public MessageDialog(String fullName, String lastMessage, String messageTime) {
        this.fullName = fullName;
        this.lastMessage = lastMessage;
        this.messageTime = messageTime;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }


    public String getFullName() {
        return fullName;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getPicId() {
        return picId;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }
}

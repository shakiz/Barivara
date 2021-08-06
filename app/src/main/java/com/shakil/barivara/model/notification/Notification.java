package com.shakil.barivara.model.notification;

public class Notification {
    private String NotificationId;
    private String Title;
    private String Message;
    private String DateTime;
    private int SeenStatus;

    public Notification(String NotificationId, String Title, String Message, String DateTime, int seenStatus) {
        this.NotificationId = NotificationId;
        this.Title = Title;
        this.Message = Message;
        this.DateTime = DateTime;
        this.SeenStatus = seenStatus;
    }

    public String getNotificationId() {
        return NotificationId;
    }

    public void setNotificationId(String notificationId) {
        NotificationId = notificationId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public int getSeenStatus() {
        return SeenStatus;
    }

    public void setSeenStatus(int seenStatus) {
        SeenStatus = seenStatus;
    }
}

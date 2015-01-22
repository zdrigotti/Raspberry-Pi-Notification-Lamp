package com.zdrigotti.raspberrypinotificationlamp;

import android.service.notification.StatusBarNotification;

public class CustomNotification {

    private boolean sent;
    private String packageName;
    private long postTime;

    public CustomNotification(StatusBarNotification in) {
        sent = false;
        packageName = in.getPackageName();
        postTime = in.getPostTime();
    }

    public boolean getSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }
}
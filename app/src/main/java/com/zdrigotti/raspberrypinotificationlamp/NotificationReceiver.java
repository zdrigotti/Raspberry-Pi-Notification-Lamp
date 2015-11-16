package com.zdrigotti.raspberrypinotificationlamp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends NotificationListenerService {

    private static final String TAG = "NotificationReceiver";
    private List<AppColorMap> selectedMaps;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        selectedMaps = readFromFile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationPosted Package: " + sbn.getPackageName() + " Time: " + sbn.getPostTime());

        //Reset the list of notifications
        StatusBarNotification[] activeNotifications = getActiveNotifications();

        String found = "";

        for (StatusBarNotification notification : activeNotifications) {
            for (AppColorMap appColorMap : selectedMaps) {
                if (notification.getPackageName().equals(appColorMap.getPackageName())) {
                    found += appColorMap.getPackageName() + "," + appColorMap.getHexColor() + "\n";
                }
            }
        }

        Log.i(TAG, "Found: " + found);

        /*//Iterate over all notifications
        for (int i = 0; i < activeNotifications.length; i++) {
            boolean found = false;
            //Iterate over stored notifications
            for (int j = 0; j < notifications.size(); j++) {
                //Set the boolean if we found the notification in both
                if (activeNotifications[i].getPackageName().equals(notifications.get(j).getPackageName())) {
                    found = true;
                }
            }
            //Add the new notification to the stored notification list
            if (!found) {
                notifications.add(new CustomNotification(activeNotifications[i]));
            }
        }

        //Loop through the notifications looking for those we care about
        for (int i = 0; i < notifications.size(); i++) {
            if (!notifications.get(i).getSent()) {
                Log.i(TAG, notifications.get(i).getPackageName() + " " + notifications.get(i).getPostTime() + " " + notifications.get(i).getSent());
                if (notifications.get(i).getPackageName().contains("textra")) { //Check for incoming text messages
                    //TODO Send Textra signal to Pi server
                    Log.i(TAG, "Textra sent");
                    notifications.get(i).setSent(true);
                }
                else if (notifications.get(i).getPackageName().contains("facebook.katana")) { //Check for incoming Facebook notifications
                    //TODO Send Facebook signal to Pi server
                    Log.i(TAG, "Facebook notification sent");
                    notifications.get(i).setSent(true);
                }
                else if (notifications.get(i).getPackageName().contains("facebook.orca")) { //Check for incoming Facebook messages
                    //TODO Send Facebook Messenger signal to Pi server
                    Log.i(TAG, "Facebook message sent");
                    notifications.get(i).setSent(true);
                }
                else if (notifications.get(i).getPackageName().contains("snapchat")) { //Check for incoming Snapchats
                    //TODO Send Snapchat signal to Pi server
                    Log.i(TAG, "snapchat sent");
                    notifications.get(i).setSent(true);
                }
                else if (notifications.get(i).getPackageName().contains("cloudmagic")) { //Check for incoming Emails
                    //TODO Send Email signal to Pi server
                    Log.i(TAG, "cloudmagic sent");
                    notifications.get(i).setSent(true);
                }
                else if (notifications.get(i).getPackageName().contains("preguntados")) { //Check for incoming Trivia Crack
                    //TODO Send Trivia Crack signal to Pi server
                    Log.i(TAG, "Trivia Crack sent");
                    notifications.get(i).setSent(true);
                }
                else {
                    //Set the notification we don't care about as sent to avoid checking it each time there's a new notification
                    notifications.get(i).setSent(true);
                }
            }
        }*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
      /*  Log.i(TAG,"onNotificationRemoved Package: " + sbn.getPackageName() + " Time: " + sbn.getPostTime());

        //Check if the notification we removed matches the ones we care about, if so, tell the Pi to remove it
        boolean removed = false;
        if (sbn.getPackageName().contains("textra")) { //Check for removed text messages
            removed = true;
            //TODO Send Textra signal to Pi server
        }
        else if (sbn.getPackageName().contains("facebook.katana")) { //Check for removed Facebook notifications
            removed = true;
            //TODO Send Facebook signal to Pi server
        }
        else if (sbn.getPackageName().contains("facebook.orca")) { //Check for removed Facebook messages
            removed = true;
            //TODO Send Facebook Messenger signal to Pi server
        }
        else if (sbn.getPackageName().contains("snapchat")) { //Check for removed Snapchats
            removed = true;
            //TODO Send Snapchat signal to Pi server
        }
        else if (sbn.getPackageName().contains("cloudmagic")) { //Check for incoming Emails
            removed = true;
            //TODO Send Email signal to Pi server
        }

        //If a notification was removed, change the ArrayList
        if (removed) {
            for (int i = 0; i < notifications.size(); i++) {
                if (sbn.getPackageName().equals(notifications.get(i).getPackageName())) {
                    notifications.remove(i);
                }
            }
            //Get rid of the null object
            notifications.removeAll(Collections.singleton(null));
        }*/
    }

    public void updateList(List<AppColorMap> appColorMaps) {
        selectedMaps = appColorMaps;
    }

    private List<AppColorMap> readFromFile() {
        List<AppColorMap> appColorMap = new ArrayList<>();

        try {
            InputStream inputStream = openFileInput(Constants.APP_MAP_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    String[] fields = receiveString.split(",");
                    appColorMap.add(new AppColorMap(fields[0], Integer.parseInt(fields[1])));
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return appColorMap;
    }
}
package com.zdrigotti.raspberrypinotificationlamp;

import android.content.SharedPreferences;
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

    private SharedPreferences settings;

    private static final String TAG = "NotificationReceiver";
    @Override
    public void onCreate() {
        super.onCreate();
        settings = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationPosted Package: " + sbn.getPackageName() + " Time: " + sbn.getPostTime());

        // Read the mappings from file
        List<AppColorMap> selectedMaps = readFromFile();

        //Reset the list of notifications
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        ArrayList<String> values = new ArrayList<>();

        // Loop over all notifications and then only the ones in the mapping
        for (StatusBarNotification notification : activeNotifications) {
            for (AppColorMap appColorMap : selectedMaps) {
                // If there's a match, add the mapping to the list
                if (notification.getPackageName().equals(appColorMap.getPackageName())) {
                    values.add(Integer.toHexString(appColorMap.getHexColor()).substring(2));
                }
            }
        }

        // Get the server IP Address and Port
        String serverIP = settings.getString(Constants.SERVER_IP, "");
        String serverPort = settings.getString(Constants.SERVER_PORT, "");

        if (serverIP.equals("") || serverPort.equals("")) {
            Log.i(TAG, "No server IP configured");
        }
        else {
            if (values.size() > 0) {
                String data = "[";

                // Loop over each valid notification, creating a structured string
                for (String value : values) {
                    if (data.length() != 1) {
                        data = data + ",'" + value + "'";
                    }
                    else {
                        data = data + "'" + value + "'";
                    }
                }

                data = data + "]";

                // Post the data to the server
                new RequestTask(data, "http://" + serverIP + ":" + serverPort).execute();
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationRemoved Package: " + sbn.getPackageName() + " Time: " + sbn.getPostTime());

        List<AppColorMap> selectedMaps = readFromFile();

        //Reset the list of notifications
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        ArrayList<String> values = new ArrayList<>();

        // Loop over all notifications and then only the ones in the mapping
        for (StatusBarNotification notification : activeNotifications) {
            for (AppColorMap appColorMap : selectedMaps) {
                if (notification.getPackageName().equals(appColorMap.getPackageName())) {
                    values.add(Integer.toHexString(appColorMap.getHexColor()).substring(2));
                }
            }
        }

        // Get the server IP Address and Port
        String serverIP = settings.getString(Constants.SERVER_IP, "");
        String serverPort = settings.getString(Constants.SERVER_PORT, "");

        if (serverIP.equals("") || serverPort.equals("")) {
            Log.i(TAG, "No server IP configured");
        }
        else {
            if (values.size() > 0) {
                String data = "[";

                // Loop over each valid notification, creating a structured string
                for (String value : values) {
                    if (data.length() != 1) {
                        data = data + ",'" + value + "'";
                    }
                    else {
                        data = data + "'" + value + "'";
                    }
                }

                data = data + "]";

                // Post the data to the server
                new RequestTask(data, "http://" + serverIP + ":" + serverPort).execute();
            }
            else {
                new RequestTask("['FFFFFF']", "http://" + serverIP + ":" + serverPort).execute();
            }
        }
    }

    // Reads all mappings from file
    private List<AppColorMap> readFromFile() {
        List<AppColorMap> appColorMap = new ArrayList<>();

        try {
            // Open a handle to the file
            InputStream inputStream = openFileInput(Constants.APP_MAP_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                // Loop over each entry and create a mapping list
                while ((receiveString = bufferedReader.readLine()) != null) {
                    String[] fields = receiveString.split(",");
                    appColorMap.add(new AppColorMap(fields[0], Integer.parseInt(fields[1])));
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("NotificationReceiver", "File not found: " + e.toString());
        }
        catch (IOException e) {
            Log.e("NotificationReceiver", "Can not read file: " + e.toString());
        }

        return appColorMap;
    }


}
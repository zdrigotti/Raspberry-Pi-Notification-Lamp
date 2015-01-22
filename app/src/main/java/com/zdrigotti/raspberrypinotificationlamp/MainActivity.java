package com.zdrigotti.raspberrypinotificationlamp;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nReceiver = new NotificationReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
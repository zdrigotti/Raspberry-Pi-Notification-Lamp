package com.zdrigotti.raspberrypinotificationlamp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    static final int PICK_NEW_APP = 1;
    private NotificationReceiver nReceiver;
    public static Context context;
    private List<AppColorMap> appColorMaps = null;
    private AppColorAdapter listAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nReceiver = new NotificationReceiver();
        context = this;

        setTitle(R.string.menu_title);

        appColorMaps = readFromFile();
        listAdapter = new AppColorAdapter(MainActivity.this, R.layout.app_color_map_row, appColorMaps);
        ListView listView = (ListView)findViewById(R.id.app_color_list);
        listView.setAdapter(listAdapter);

        /*Button appsButton = (Button) findViewById(R.id.appsButton);

        appsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AllAppsActivity.class);
                startActivityForResult(intent, PICK_NEW_APP);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(context, AllAppsActivity.class);
                startActivityForResult(intent, PICK_NEW_APP);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_NEW_APP) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("MainActivity", data.getStringExtra(Constants.PACKAGE_NAME));
                appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + ",0000FF");
            }
        }
    }

    private void appendToFile(String data) {
        String contents = "";

        try {
            InputStream inputStream = openFileInput(Constants.APP_MAP_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    contents += receiveString + "\n";
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

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Constants.APP_MAP_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.write(contents + data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
                    Log.i("MainActivity", receiveString);
                    String[] fields = receiveString.split(",");
                    appColorMap.add(new AppColorMap(fields[0], fields[1]));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
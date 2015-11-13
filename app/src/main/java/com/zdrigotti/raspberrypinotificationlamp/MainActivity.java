package com.zdrigotti.raspberrypinotificationlamp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private AppColorAdapter listAdapter = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nReceiver = new NotificationReceiver();
        context = this;

        setTitle(R.string.menu_title);

        listAdapter = new AppColorAdapter(MainActivity.this, R.layout.app_color_map_row, readFromFile());
        listView = (ListView)findViewById(R.id.app_color_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                view.setSelected(true);
            }
        });

        listView.setAdapter(listAdapter);
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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PICK_NEW_APP) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("MainActivity", data.getStringExtra(Constants.PACKAGE_NAME));
                //appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + ",0000FF");
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, 0, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        Log.i("MainActivity", "Red: " + Color.red(color) + " Green: " + Color.green(color) + " Blue: " + Color.blue(color));
                        //Log.i("MainActivity", "Color picked: " + );
                        //appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + "," + Integer.toHexString(color).substring(2));
                        appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + "," + color);
                    }

                });
                colorPickerDialog.show();
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

        listAdapter.swapItems(readFromFile());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
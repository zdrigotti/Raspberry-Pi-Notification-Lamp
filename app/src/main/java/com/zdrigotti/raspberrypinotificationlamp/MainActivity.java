package com.zdrigotti.raspberrypinotificationlamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
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
    private List<AppColorMap> appColorMaps;
    private View selectedListItem;
    private int selectedIndex;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //nReceiver = new NotificationReceiver();

        Intent intent = new Intent(this, NotificationReceiver.class);
        startService(intent);

        context = this;

        setTitle(R.string.menu_title);

        appColorMaps = readFromFile();
        listAdapter = new AppColorAdapter(MainActivity.this, R.layout.app_color_map_row, appColorMaps);
        listView = (ListView)findViewById(R.id.app_color_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectedListItem != null){
                    selectedListItem.setBackgroundResource(R.color.list_background);
                    showOption(R.id.add);
                    hideOption(R.id.delete);
                    hideOption(R.id.changeColor);
                }

                if (selectedListItem != view) {
                    view.setBackgroundResource(R.color.list_background_pressed);
                    selectedListItem = view;
                    selectedIndex = position;
                    hideOption(R.id.add);
                    showOption(R.id.delete);
                    showOption(R.id.changeColor);
                }
                else {
                    showOption(R.id.add);
                    hideOption(R.id.delete);
                    hideOption(R.id.changeColor);
                }
            }
        });

        listView.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(context, AllAppsActivity.class);
                startActivityForResult(intent, PICK_NEW_APP);
                return true;
            case R.id.delete:
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Are you sure you want to delete this selection?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                appColorMaps.remove(selectedIndex);
                                writeToFile(appColorMaps);
                                appColorMaps = readFromFile();
                                listAdapter.swapItems(appColorMaps);

                                //nReceiver.stopSelf();
                                //nReceiver = new NotificationReceiver();
                                //nReceiver.updateList(appColorMaps);
                                //Intent intent = new Intent(context, NotificationReceiver.class);
                                //stopService(intent);
                                //startService(intent);
                                stopService(new Intent(NotificationReceiver.NOTIFICATION_SERVICE));
                                startService(new Intent(NotificationReceiver.NOTIFICATION_SERVICE));

                                selectedListItem.setBackgroundResource(R.color.list_background);
                                selectedListItem = null;

                                showOption(R.id.add);
                                hideOption(R.id.delete);
                                hideOption(R.id.changeColor);

                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();

                return true;
            case R.id.changeColor:
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, 0, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        appColorMaps.get(selectedIndex).setHexColor(color);
                        writeToFile(appColorMaps);

                        //nReceiver.updateList(appColorMaps);
                        Intent intent = new Intent(context, NotificationReceiver.class);
                        stopService(intent);
                        startService(intent);

                        appColorMaps = readFromFile();
                        listAdapter.swapItems(appColorMaps);
                    }

                });
                colorPickerDialog.show();
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
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, 0, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + "," + color);
                        appColorMaps = readFromFile();
                        listAdapter.swapItems(appColorMaps);

                        Intent intent = new Intent(context, NotificationReceiver.class);
                        stopService(intent);
                        startService(intent);

                        //nReceiver.stopSelf();
                        //nReceiver = new NotificationReceiver();
                       //nReceiver.updateList(appColorMaps);
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
    }

    private void writeToFile(List<AppColorMap> appColorMaps) {
        String contents = "";

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Constants.APP_MAP_FILE, Context.MODE_PRIVATE));

            for (AppColorMap appColorMap : appColorMaps) {
                contents += appColorMap.getPackageName() + "," + appColorMap.getHexColor() + "\n";
            }

            Log.i("MainActivity", contents);

            outputStreamWriter.write(contents);
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
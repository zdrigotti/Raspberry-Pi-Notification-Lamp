package com.zdrigotti.raspberrypinotificationlamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private Context context;
    private AppColorAdapter listAdapter = null;
    private ListView listView;
    private List<AppColorMap> appColorMaps;
    private View selectedListItem;
    private int selectedIndex;
    private Menu menu;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create a new Notification Receiver
        nReceiver = new NotificationReceiver();

        // Define the context and get access to Shared Preferences
        context = this;
        settings = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

        // Set the title bar
        setTitle(R.string.menu_title);

        // Read in the app configurations from file and create a new list adapter and view with them
        appColorMaps = readFromFile();
        listAdapter = new AppColorAdapter(MainActivity.this, R.layout.app_color_map_row, appColorMaps);
        listView = (ListView)findViewById(R.id.app_color_list);

        // OnClickListener for the list view items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When unselected, change the background back to normal and set the according icons
                if (selectedListItem != null) {
                    selectedListItem.setBackgroundResource(R.color.list_background);
                    showOption(R.id.add);
                    showOption(R.id.connection);
                    hideOption(R.id.delete);
                    hideOption(R.id.changeColor);
                }

                if (selectedListItem != view) {
                    // When selected, change the background to selected and set the according icons
                    view.setBackgroundResource(R.color.list_background_pressed);
                    selectedListItem = view;
                    selectedIndex = position;
                    hideOption(R.id.add);
                    hideOption(R.id.connection);
                    showOption(R.id.delete);
                    showOption(R.id.changeColor);
                }
                else {
                    // When unselected, change the background back to normal and set the according icons
                    selectedListItem.setBackgroundResource(R.color.list_background);
                    showOption(R.id.add);
                    showOption(R.id.connection);
                    hideOption(R.id.delete);
                    hideOption(R.id.changeColor);

                    selectedListItem = null;
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

    // Used to hide an item in the action bar
    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    // Used to show an item in the action bar
    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.connection: {
                // Create a new dialog for specifying the IP Address and Port
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.set_server_details_dialog, null);

                builder.setTitle(R.string.server_dialog_title);
                EditText serverIPEditText = (EditText) dialogView.findViewById(R.id.serverIP);
                EditText serverPortEditText = (EditText) dialogView.findViewById(R.id.serverPort);

                // Get IP Address and Port from preferences
                String currentIP = settings.getString(Constants.SERVER_IP, "");
                String currentPort = settings.getString(Constants.SERVER_PORT, "");

                // Set edit texts if IP Address or Port are specified
                if (!currentIP.equals("")) {
                    serverIPEditText.setText(currentIP);
                }
                if (!currentPort.equals("")) {
                    serverPortEditText.setText(currentPort);
                }

                builder.setView(dialogView)
                        // Add action buttons
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText serverIPEditText = (EditText) dialogView.findViewById(R.id.serverIP);
                                EditText serverPortEditText = (EditText) dialogView.findViewById(R.id.serverPort);

                                // Commit the specified IP Address and Port to Shared Preferences
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString(Constants.SERVER_IP, serverIPEditText.getText().toString());
                                editor.putString(Constants.SERVER_PORT, serverPortEditText.getText().toString());
                                editor.commit();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.show();
                return true;
            }
            case R.id.add: {
                // Start a new activity to get which app to add
                Intent intent = new Intent(context, AllAppsActivity.class);
                startActivityForResult(intent, PICK_NEW_APP);
                return true;
            }
            case R.id.delete: {
                // Create a new dialog for deleting a mapping
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(R.string.delete);
                alertDialog.setMessage(getString(R.string.delete_confirmation));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove the mapping and rewrite the file
                                appColorMaps.remove(selectedIndex);
                                writeToFile(appColorMaps);

                                // Replace the list adapter
                                appColorMaps = readFromFile();
                                listAdapter.swapItems(appColorMaps);

                                // Reset the selected items
                                selectedListItem.setBackgroundResource(R.color.list_background);
                                selectedListItem = null;

                                // Show initial state icons
                                showOption(R.id.add);
                                showOption(R.id.connection);
                                hideOption(R.id.delete);
                                hideOption(R.id.changeColor);

                                dialog.dismiss();
                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();

                return true;
            }
            case R.id.changeColor: {
                // Create a Color Picker Dialog to change the color
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, appColorMaps.get(selectedIndex).getHexColor(), new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        // Write the new mapping to file
                        appColorMaps.get(selectedIndex).setHexColor(color);
                        writeToFile(appColorMaps);

                        // Replace the list adapter
                        appColorMaps = readFromFile();
                        listAdapter.swapItems(appColorMaps);
                    }

                });
                colorPickerDialog.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PICK_NEW_APP) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Create a new Color Picker Dialog to pick a color for the new app
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, 0, new ColorPickerDialog.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        // Write the new mapping to file and replace the list adpater
                        appendToFile(data.getStringExtra(Constants.PACKAGE_NAME) + "," + color);
                        appColorMaps = readFromFile();
                        listAdapter.swapItems(appColorMaps);
                    }

                });
                colorPickerDialog.show();
            }
        }
    }

    // Method for appending to the end of the existing file
    private void appendToFile(String data) {
        String contents = "";

        try {
            // Get a file handle and open it
            InputStream inputStream = openFileInput(Constants.APP_MAP_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                // Read in the file line by line
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
            // Add the new mapping to the contents and write it to file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Constants.APP_MAP_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.write(contents + data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //Writes all mappings to file
    private void writeToFile(List<AppColorMap> appColorMaps) {
        String contents = "";

        try {
            // Open a handle to the file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Constants.APP_MAP_FILE, Context.MODE_PRIVATE));

            // Loop over each mapping and create one string
            for (AppColorMap appColorMap : appColorMaps) {
                contents += appColorMap.getPackageName() + "," + appColorMap.getHexColor() + "\n";
            }

            // Write the contents to file
            outputStreamWriter.write(contents);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    // Reads mappings from file
    private List<AppColorMap> readFromFile() {
        List<AppColorMap> appColorMap = new ArrayList<>();

        try {
            // Open a handle to the file
            InputStream inputStream = openFileInput(Constants.APP_MAP_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                // Loop over each line, parsing the info and creating a new color map
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
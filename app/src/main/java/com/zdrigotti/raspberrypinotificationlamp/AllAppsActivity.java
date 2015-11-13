package com.zdrigotti.raspberrypinotificationlamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class AllAppsActivity extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> appList = null;
    private ApplicationAdapter listAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);

        packageManager = getPackageManager();

        new LoadApplications().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.PACKAGE_NAME, appList.get(position).packageName);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    appList.add(info);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        final ApplicationInfo.DisplayNameComparator comparator = new ApplicationInfo.DisplayNameComparator(packageManager);

        Collections.sort(appList, new ApplicationInfo.DisplayNameComparator(packageManager));

        return appList;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listAdapter = new ApplicationAdapter(AllAppsActivity.this, R.layout.snippet_list_row, appList);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listAdapter);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AllAppsActivity.this, null, "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
package com.zdrigotti.raspberrypinotificationlamp;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppColorAdapter extends ArrayAdapter<AppColorMap> {
    private List<AppColorMap> appsList = null;
    private Context context;
    private PackageManager packageManager;

    public AppColorAdapter(Context context, int textViewResourceId, List<AppColorMap> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public AppColorMap getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    public void swapItems(List<AppColorMap> appsList) {
        this.appsList = appsList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.app_color_map_row, null);
        }

        AppColorMap appColorMap = appsList.get(position);
        if (null != appColorMap) {
            //Use package name to get icon and name
            try {
                ApplicationInfo app = MainActivity.context.getPackageManager().getApplicationInfo(appColorMap.getPackageName(), 0);

                TextView appName = (TextView) view.findViewById(R.id.app_name);
                TextView hexColor = (TextView) view.findViewById(R.id.hex_color);
                ImageView iconView = (ImageView) view.findViewById(R.id.app_icon);

                appName.setText(packageManager.getApplicationLabel(app).toString());
                hexColor.setText("#" + Integer.toHexString(appColorMap.getHexColor()).substring(2));
                iconView.setImageDrawable(packageManager.getApplicationIcon(app));
            }
            catch (PackageManager.NameNotFoundException e) {
                Log.i("AppColorAdapter", "Couldn't find app");
            }
        }
        return view;
    }
};
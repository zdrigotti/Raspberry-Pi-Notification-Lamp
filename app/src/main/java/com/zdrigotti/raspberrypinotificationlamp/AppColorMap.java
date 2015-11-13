package com.zdrigotti.raspberrypinotificationlamp;

public class AppColorMap {

    private String packageName;
    private int hexColor;

    public AppColorMap(String packageName, int hexColor) {
        this.packageName = packageName;
        this.hexColor = hexColor;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getHexColor() {
        return hexColor;
    }

    public void setHexColor(int hexColor) {
        this.hexColor = hexColor;
    }
}

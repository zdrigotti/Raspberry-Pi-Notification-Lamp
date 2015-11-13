package com.zdrigotti.raspberrypinotificationlamp;

public class AppColorMap {

    private String packageName;
    private String hexColor;

    public AppColorMap(String packageName, String hexColor) {
        this.packageName = packageName;
        this.hexColor = hexColor;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}

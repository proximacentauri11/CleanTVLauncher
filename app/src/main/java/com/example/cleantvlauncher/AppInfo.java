package com.example.cleantvlauncher;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public final String label;
    public final String packageName;
    public final Drawable icon;
    public final String activityName;

    public AppInfo(String label, String packageName, Drawable icon, String activityName) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
        this.activityName = activityName;
    }
}
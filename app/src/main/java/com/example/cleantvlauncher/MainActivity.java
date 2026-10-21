package com.example.cleantvlauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {

    private static final String[] DOCK_PACKAGES = {
            "com.happproxy",
            "org.smarttube.stable",
            "com.google.android.youtube.tv",
            "top.rootu.lampa"
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView clock, date, greeting, focusTitle, forFun;
    private RecyclerView dock, grid;
    private AppAdapter dockAdapter, gridAdapter;
    private final List<AppInfo> allApps = new ArrayList<>();
    private final List<AppInfo> dockApps = new ArrayList<>();
    private final List<AppInfo> gridApps = new ArrayList<>();

    private final Runnable clockTick = new Runnable() {
        @Override public void run() { updateClock(); handler.postDelayed(this, 30_000); }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(buildLayout());
        loadApps();
        handler.post(clockTick);
    }

    @Override
    protected void onDestroy() { handler.removeCallbacks(clockTick); super.onDestroy(); }

    private View buildLayout() {
        FrameLayout root = new FrameLayout(this);
        //root.setBackgroundColor(Color.BLACK);
        root.setBackgroundResource(R.drawable.kittens);

        LinearLayout topLeft = new LinearLayout(this);
        topLeft.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams tlLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlLp.leftMargin = dp(30); tlLp.topMargin = dp(30);
        topLeft.setLayoutParams(tlLp);

        LinearLayout topRight = new LinearLayout(this);
        topRight.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams tlRp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlRp.rightMargin = dp(30); tlRp.topMargin = dp(30);
        tlRp.gravity = Gravity.TOP | Gravity.END;
        topRight.setLayoutParams(tlRp);

        forFun = new TextView(this);
        forFun.setText("Че по кайфу то и делаю");
        forFun.setTextColor(Color.BLACK);
        forFun.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        forFun.setTextSize(18f);
        topRight.addView(forFun);

        greeting = new TextView(this);
        greeting.setText("Мюся и Исай классные кошечки");
        greeting.setTextColor(Color.BLACK);
        greeting.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        greeting.setTextSize(18f);
        topLeft.addView(greeting);

        clock = new TextView(this);
        clock.setTextColor(Color.BLACK);
        clock.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        clock.setTextSize(64f);
        topLeft.addView(clock);

        date = new TextView(this);
        date.setTextColor(Color.BLACK);
        date.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        date.setTextSize(16f);
        topLeft.addView(date);

        root.addView(topLeft);
        root.addView(topRight);

        focusTitle = new TextView(this);
        focusTitle.setTextColor(Color.BLACK);
        focusTitle.setTextSize(28f);
        focusTitle.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        focusTitle.setAlpha(0f);
        FrameLayout.LayoutParams ftLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ftLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        ftLp.bottomMargin = dp(145);
        focusTitle.setLayoutParams(ftLp);
        root.addView(focusTitle);

        dock = new RecyclerView(this);
        FrameLayout.LayoutParams dockLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(130));
        dockLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        dockLp.bottomMargin = dp(10);
        dock.setLayoutParams(dockLp);
        dock.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dock.setBackgroundResource(R.drawable.dock_band);
        dock.setClipToPadding(false);
        dock.setPadding(dp(20), 0, dp(20), 0);
        root.addView(dock);

        grid = new RecyclerView(this);
        FrameLayout.LayoutParams gridLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gridLp.topMargin = dp(200);
        gridLp.bottomMargin = dp(170);
        gridLp.leftMargin = dp(40);
        gridLp.rightMargin = dp(40);
        grid.setLayoutParams(gridLp);
        grid.setLayoutManager(new GridLayoutManager(this, 4));
        grid.setClipToPadding(false);
        root.addView(grid);

        return root;
    }
    private void updateClock() {
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("EEEE, d MMMM", new Locale("ru"));
        clock.setText(tf.format(new Date()));
        date.setText(df.format(new Date()));
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> resolved = pm.queryIntentActivities(main, 0);
        if (resolved.isEmpty()) {
            Intent fallback = new Intent(Intent.ACTION_MAIN, null);
            fallback.addCategory(Intent.CATEGORY_LAUNCHER);
            resolved = pm.queryIntentActivities(fallback, 0);
        }

        Set<String> seen = new HashSet<>();
        allApps.clear();
        for (ResolveInfo ri : resolved) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue;
            if (!seen.add(pkg)) continue;
            String label = ri.loadLabel(pm).toString();
            Drawable icon = ri.loadIcon(pm);
            allApps.add(new AppInfo(label, pkg, icon, ri.activityInfo.name));
        }

        dockApps.clear(); gridApps.clear();
        for (String p : DOCK_PACKAGES) {
            for (AppInfo a : allApps) {
                if (a.packageName.equals(p)) { dockApps.add(a); break; }
            }
        }
        for (AppInfo a : allApps) if (!dockApps.contains(a)) gridApps.add(a);

        dockAdapter = new AppAdapter(this, dockApps, true, this::launchApp,
                info -> { focusTitle.setText(info.label); focusTitle.setAlpha(0.9f); });
        dock.setAdapter(dockAdapter);

        gridAdapter = new AppAdapter(this, gridApps, false, this::launchApp,
                info -> focusTitle.setAlpha(0f));
        gridAdapter.setDockFocusRequester(() -> {
            if (!dockApps.isEmpty() && dock.getChildCount() > 0) {
                dock.getChildAt(0).requestFocus();
            }
        });
        grid.setAdapter(gridAdapter);

        grid.post(() -> {
            if (grid.getChildCount() > 0) grid.getChildAt(0).requestFocus();
            else if (dock.getChildCount() > 0) dock.getChildAt(0).requestFocus();
        });
    }

    private void launchApp(AppInfo info) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        i.setComponent(new ComponentName(info.packageName, info.activityName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(i);
        } catch (Exception ignored) {

        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
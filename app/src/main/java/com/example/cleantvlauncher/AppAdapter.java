package com.example.cleantvlauncher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.VH> {

    public interface OnAppClickListener { void onAppClick(AppInfo info); }
    public interface OnAppLongClickListener { void onAppLongClick(AppInfo info); }
    private OnAppLongClickListener longClickListener;
    public void setOnAppLongClickListener(OnAppLongClickListener l) { this.longClickListener = l; }
    public interface RunnableLike { void run(); }
    private RunnableLike dockFocusRequester;
    public void setDockFocusRequester(RunnableLike r) { this.dockFocusRequester = r; }
    public interface OnAppFocusListener { void onAppFocus(AppInfo info); }

    private final Context context;
    private final List<AppInfo> items;
    private final boolean isDock;
    private final OnAppClickListener clickListener;
    private final OnAppFocusListener focusListener;

    public AppAdapter(Context context, List<AppInfo> items, boolean isDock,
                      OnAppClickListener clickListener, OnAppFocusListener focusListener) {
        this.context = context;
        this.items = items;
        this.isDock = isDock;
        this.clickListener = clickListener;
        this.focusListener = focusListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int size = isDock ? dp(96) : dp(120);
        FrameLayout root = new FrameLayout(context);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
        int margin = dp(isDock ? 12 : 16);
        lp.setMargins(margin, margin, margin, margin);
        root.setLayoutParams(lp);
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        root.setBackgroundResource(R.drawable.tile_focus);

        ImageView icon = new ImageView(context);
        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int pad = dp(isDock ? 16 : 18);
        iconLp.setMargins(pad, pad, pad, pad);
        icon.setLayoutParams(iconLp);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(icon);

        if (!isDock) {
            TextView label = new TextView(context);
            FrameLayout.LayoutParams labelLp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            labelLp.gravity = Gravity.BOTTOM;
            label.setLayoutParams(labelLp);
            label.setGravity(Gravity.CENTER);
            label.setTextColor(Color.BLACK);
            label.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            label.setTextSize(13f);
            label.setMaxLines(1);
            label.setAlpha(0f);
            root.addView(label);
        }

        return new VH(root, icon);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AppInfo info = items.get(position);
        holder.icon.setImageDrawable(info.icon);
        holder.itemView.setContentDescription(info.label);

        View label = ((ViewGroup) holder.itemView).getChildAt(1);
        if (label instanceof TextView) {
            ((TextView) label).setText(info.label);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onAppClick(info);
        });
        if (!isDock) {
                    }
        holder.itemView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                if ((keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == android.view.KeyEvent.KEYCODE_ENTER)
                        && (event.isLongPress() || event.getRepeatCount() > 10)) {
                    if (longClickListener != null) longClickListener.onAppLongClick(info);
                    return true;
                }
                if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_DOWN
                        && position >= 0 && position < 4 && dockFocusRequester != null) {
                    dockFocusRequester.run();
                    return true;
                }
            }
            return false;
        });
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            v.animate().scaleX(hasFocus ? 1.08f : 1f).scaleY(hasFocus ? 1.08f : 1f)
                    .setDuration(140).start();
            View lbl = ((ViewGroup) v).getChildAt(1);
            if (lbl instanceof TextView) {
                lbl.animate().alpha(hasFocus ? 1f : 0f).setDuration(140).start();
            }
            if (hasFocus && focusListener != null) focusListener.onAppFocus(info);
        });
    }



    @Override
    public int getItemCount() { return items.size(); }

    private int dp(int v) {
        return Math.round(v * context.getResources().getDisplayMetrics().density);
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView icon;
        VH(@NonNull View itemView, ImageView icon) {
            super(itemView);
            this.icon = icon;
        }
    }
}
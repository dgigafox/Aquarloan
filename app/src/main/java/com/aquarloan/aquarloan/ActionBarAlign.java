package com.aquarloan.aquarloan;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Darren Gegantino on 3/7/2018.
 */

public class ActionBarAlign {

    public static void actionBarCenter(ActionBar actionBar, Activity activity) {

        TextView textView = new TextView(activity.getApplicationContext());

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        textView.setLayoutParams(layoutParams);

        textView.setText(activity.getTitle());

        textView.setTextColor(Color.BLACK);

        textView.setGravity(Gravity.CENTER);

        textView.setTextSize(20);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        actionBar.setCustomView(textView);
    }
}

package com.example.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;

public class Utils {
    private static int sTheme;

    public final static int THEME_RED = 0;
    public final static int THEME_ORANGE = 1;
    public final static int THEME_YELLOW = 2;
    public final static int THEME_GREEN = 3;
    public final static int THEME_BLUE = 4;
    public final static int THEME_PURPLE = 5;
    public final static int THEME_PINK = 6;
    public final static int THEME_GRAY = 7;

    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {

            case THEME_RED:
                activity.setTheme(R.style.AppTheme_Red);
                break;
            case THEME_ORANGE:
                activity.setTheme(R.style.AppTheme_Orange);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppTheme_Yellow);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppTheme_Green);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppTheme_Blue);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.AppTheme_Pink);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.AppTheme_Purple);
                break;
            case THEME_GRAY:
                activity.setTheme(R.style.AppTheme_Gray);
                break;
        }
    }
}
package com.example.todoapp;

import java.io.Serializable;

public class Constant implements Serializable {
    public static int theme = R.style.AppTheme_Blue;
    public static int nav_clicked=0;
    public static Boolean isNavClicked = false;
    public static Boolean isToggle=true;
    public static int color=0;

    public static int convert(int id){
        switch (id){
            case 0:  return R.style.AppTheme_Red;
            case 1:  return R.style.AppTheme_Orange;
            case 2: return R.style.AppTheme_Yellow;
            case 3: return R.style.AppTheme_Green;
            case 4: return R.style.AppTheme_Blue;
            case 5: return R.style.AppTheme_Purple;
            case 6: return R.style.AppTheme_Pink;
            case 7: return R.style.AppTheme_Gray;
        }
        return 4;
    };
}

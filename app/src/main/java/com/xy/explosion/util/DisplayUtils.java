package com.xy.explosion.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by zhouliancheng on 2016/11/17.
 */
public class DisplayUtils {
    private static int mScreenWidth;
    private static int mScreenHeight;

    public static int getScreenWidth(Context context) {
        if (0 == mScreenWidth) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mScreenWidth = wm.getDefaultDisplay().getWidth();
        }
        return mScreenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (0 == mScreenHeight) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mScreenHeight = wm.getDefaultDisplay().getHeight();
        }
        return mScreenHeight;
    }
}

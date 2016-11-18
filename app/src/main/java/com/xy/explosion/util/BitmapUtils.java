package com.xy.explosion.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouliancheng on 2016/11/17.
 */
public class BitmapUtils {

    public static List<Bitmap> generateConfettiBitmaps(Context context, int[] resIds, int size) {
        final List<Bitmap> bitmaps = new ArrayList<>();
        for (int resId : resIds) {
            bitmaps.add(createRandomBitmap(context, resId, size));
        }
        return bitmaps;
    }

    public static Bitmap createRandomBitmap(Context context, int resId, int size) {
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        // 保留两位小数的float类型
        float hwRatio = (float) (Math.round(bitmap.getHeight() * 100 / bitmap.getWidth())) / 100;
        int width = size;
        int height = (int) (width * hwRatio);
        Bitmap bt = Bitmap.createScaledBitmap(bitmap, width, height, true);
        return bt;
    }
}

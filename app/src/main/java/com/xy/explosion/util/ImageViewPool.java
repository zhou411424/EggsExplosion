package com.xy.explosion.util;

import android.content.Context;
import android.support.v4.util.Pools;
import android.widget.ImageView;

/**
 * Created by zhouliancheng on 2016/11/18.
 * ImageView对象池，用于回收利用
 */
public class ImageViewPool {

    private static final String TAG = "ImageViewPool";
    public static final int MAX_LIMIT = 60;//适当加大池的大小
    private static final Pools.SynchronizedPool mCachePool = new Pools.SynchronizedPool<>(MAX_LIMIT);

    /**
     * 创建对象
     * @param context
     * @return
     */
    public static ImageView obtain(Context context) {
        ImageView imageView = (ImageView) mCachePool.acquire();
        return imageView != null ? imageView : new ImageView(context);
    }

    /**
     * 将对象放回对象池
     * @param imageView
     */
    public static void release(ImageView imageView) {
        if (imageView != null) {
            mCachePool.release(imageView);
        }
    }

    /**
     * 清空对象池
     */
    public static void clear() {
        for (int i = 0; i < MAX_LIMIT; i++) {
            mCachePool.acquire();
        }
    }
}


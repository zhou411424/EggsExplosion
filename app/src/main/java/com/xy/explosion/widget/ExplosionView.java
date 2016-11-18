package com.xy.explosion.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by xingyun on 2016/11/17.
 * 彩蛋爆炸效果
 */
public class ExplosionView extends SurfaceView {

    private static final String TAG = "ExplosionView";
    private SurfaceHolder mHolder;
    private ExplosionThread mExplosionThread;

    public ExplosionView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(mCallback);
        mHolder.setKeepScreenOn(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        Paint mPaint = new Paint();

    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw==>");

        if (canvas != null) {
            //清空画布
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (mExplosionThread == null) {
                mExplosionThread = new ExplosionThread();
            }
            mExplosionThread.setRunning(true);
            mExplosionThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            boolean retry = true;// 重试

            if (mExplosionThread != null) {
                mExplosionThread.setRunning(false);

                while (retry) {
                    try {
                        mExplosionThread.join();
                        retry = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            mExplosionThread = null;
        }
    };

    private class ExplosionThread extends Thread {
        private boolean isRunning;

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                // TODO
            }
        }
    }

}

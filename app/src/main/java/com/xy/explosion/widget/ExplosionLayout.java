package com.xy.explosion.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xy.explosion.R;
import com.xy.explosion.util.BitmapUtils;
import com.xy.explosion.util.Constants;
import com.xy.explosion.util.DisplayUtils;
import com.xy.explosion.util.ImageViewPool;
import com.xy.explosion.util.PropertyValuesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouliancheng on 2016/11/17.
 */
public class ExplosionLayout extends RelativeLayout {

    private static final String TAG = "ExplosionLayout";
    private static final int MAX_COUNT = 10;
    private static final int PLAY_EXPLOSION_ANIM_MSG = 0;
    private static final int FINISH_EXPLOSION_ANIM_MSG = 1;
    private int mScreenWidth;
    private int mScreenHeight;
    private List<Bitmap> mBitmaps;
    private ObjectAnimator explosionAnim;
    private boolean isAnimRunning = false;
    private ArrayList<EndPoint> mEndPoints = new ArrayList<>();
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    // 动画的路线数量
    private int mRouteCount;
    private boolean mIsHandle = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!mIsHandle) {
                return;
            }
            switch (msg.what) {
                case PLAY_EXPLOSION_ANIM_MSG:
                    playSingleTransAnim(msg.arg1, msg.arg2);
                    break;
                case FINISH_EXPLOSION_ANIM_MSG:
                    endAnim();
                    break;
            }
        }
    };

    public ExplosionLayout(Context context) {
        this(context, null);
    }

    public ExplosionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExplosionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScreenWidth = DisplayUtils.getScreenWidth(getContext());
        mScreenHeight = DisplayUtils.getScreenHeight(getContext());
        Log.d(TAG, "init==>mScreenWidth="+mScreenWidth+", mScreenHeight="+mScreenHeight);
        mBitmaps = BitmapUtils.generateConfettiBitmaps(getContext(), Constants.RISING_ICONS, getResources().getDimensionPixelSize(R.dimen.default_confetti_size));

        //先算出30度角对应的弧度值
        double radians = Math.toRadians(35);
        // 得到对应的正切值，其中Math tan()参数为弧度
        double tan = Math.tan(radians);
        Log.d(TAG, "radians="+radians + ", tan="+tan+", x="+mScreenWidth / 2+", y="+ (mScreenWidth / 2) * tan);

        // 屏幕左边上下两个点
        EndPoint endPointLeft1 = new EndPoint(-(float) (mScreenWidth / 2), (float)((mScreenHeight / 2) * (1 - tan)));
        EndPoint endPointLeft2 = new EndPoint(-(float) (mScreenWidth / 2), (float)((mScreenHeight / 2) * (1 + tan)));
        // 屏幕右边上下两个点
        EndPoint endPointRight1 = new EndPoint((float) (mScreenWidth / 2), (float)((mScreenHeight / 2) * (1 - tan)));
        EndPoint endPointRight2 = new EndPoint((float) (mScreenWidth / 2), (float)((mScreenHeight / 2) * (1 + tan)));
        // 屏幕上方左边两个点
        EndPoint endPointtop1 = new EndPoint(-(float) (mScreenWidth / 2), -(float)((mScreenHeight / 2) * (1 - tan)));
        EndPoint endPointtop2 = new EndPoint(-(float) (mScreenWidth / 2), -(float)((mScreenHeight / 2) * (1 + tan)));
        // 屏幕上方右边两个点
        EndPoint endPointtop3 = new EndPoint((float) (mScreenWidth / 2), -(float)((mScreenHeight / 2) * (1 - tan)));
        EndPoint endPointtop4 = new EndPoint((float) (mScreenWidth / 2), -(float)((mScreenHeight / 2) * (1 + tan)));
        mEndPoints.add(endPointLeft1);
        mEndPoints.add(endPointLeft2);
        mEndPoints.add(endPointRight1);
        mEndPoints.add(endPointRight2);
        mEndPoints.add(endPointtop1);
        mEndPoints.add(endPointtop2);
        mEndPoints.add(endPointtop3);
        mEndPoints.add(endPointtop4);
    }

    private void playExplosionAnim() {
        Log.d(TAG, "playExplosionAnim==>");
        if (!isAnimRunning) {
            return;
        }

        //屏幕右边上下两个点
        Log.d(TAG, "mPointMap size="+mEndPoints.size());
        if (mEndPoints != null && mEndPoints.size() > 0) {
            for (EndPoint endPoint : mEndPoints) {
                Log.d(TAG, "playExplosionAnim============pointX="+endPoint.pointX+", pointY="+endPoint.pointY);
                mThreadPool.execute(new SingleTransRunnable(endPoint.pointX, endPoint.pointY));
            }
        }
    }

    private class EndPoint {
        public float pointX;
        public float pointY;
        public EndPoint(float pointX, float pointY) {
            this.pointX = pointX;
            this.pointY = pointY;
        }
    }

    private class SingleTransRunnable implements Runnable {
        private float endX;
        private float endY;

        public SingleTransRunnable(float endX, float endY) {
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        public void run() {
            // 用来记录单条路线产生的图片数量
            int curNum = 0;
            while (curNum < MAX_COUNT) {
                if (!isAnimRunning) {
                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = mHandler.obtainMessage();
                msg.arg1 = (int) endX;
                msg.arg2 = (int) endY;
                msg.what = PLAY_EXPLOSION_ANIM_MSG;
                mHandler.sendMessage(msg);

                curNum++;
                if (curNum == MAX_COUNT) {
                    mRouteCount ++;
                    Log.d(TAG, "mRouteCount="+mRouteCount);
                    if (mRouteCount == mEndPoints.size()) {
                        //所有路线的动画均执行完毕，此动画也就结束了
                        mHandler.sendEmptyMessageDelayed(FINISH_EXPLOSION_ANIM_MSG, 1000);
                        break;
                    }
                }
            }
        }
    }

    private void playSingleTransAnim(float endX, float endY) {
        if(mBitmaps == null || mBitmaps.size() <= 0) {
            return;
        }
        final ImageView imageView = ImageViewPool.obtain(getContext());
        imageView.setImageBitmap(mBitmaps.get((int) (Math.random() * mBitmaps.size())));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(params);
        addView(imageView);

        explosionAnim = ObjectAnimator.ofPropertyValuesHolder(imageView,
                PropertyValuesHelper.translationX(0, endX), PropertyValuesHelper.translationY(0, endY),
                PropertyValuesHelper.scaleX(0.7f, 1f), PropertyValuesHelper.scaleY(0.7f, 1f));
        explosionAnim.setDuration(400);
        explosionAnim.setInterpolator(new AccelerateInterpolator());
        explosionAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (imageView != null) {
                    ImageViewPool.release(imageView);
                    removeView(imageView);
                    Log.d(TAG, "onAnimationEnd==>removeView...");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        explosionAnim.start();
    }

    public void startAnim() {
        // 动画开始
        isAnimRunning = true;
        reset();
        playExplosionAnim();
    }

    public void endAnim() {
        Log.d(TAG, "endAnim==>");
        // 动画结束
        isAnimRunning = false;
        reset();
    }

    private void reset() {
        cancel();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        // 必须置为0
        mRouteCount = 0;
        removeAllViews();
    }

    private void cancel() {
        if (explosionAnim != null) {
            explosionAnim.cancel();
        }
    }

    public void onDestroy() {
        mIsHandle = false;
        reset();
        if (mBitmaps != null) {
            mBitmaps.clear();
            mBitmaps = null;
        }
        // 关闭线程池
        if (mThreadPool != null && !mThreadPool.isShutdown()) {
            Log.d(TAG, "mThreadPool shutdown.....");
            //然后调用 shutdownNow（如有必要）取消所有遗留的任务
            try {
                //调用shutdown拒绝传入任务
                mThreadPool.shutdown();
                if (!mThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                    mThreadPool.shutdownNow();
                }
            } catch (InterruptedException ie) {
                try {
                    mThreadPool.shutdownNow();
                } catch (Exception e) {
                    Log.e(TAG, "error: "+e.getMessage());
                }
            }
        }
    }
}

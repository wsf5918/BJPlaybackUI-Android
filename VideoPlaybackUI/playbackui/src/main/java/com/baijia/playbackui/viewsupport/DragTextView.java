package com.baijia.playbackui.viewsupport;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.baijiahulian.livecore.utils.DisplayUtils;

/**
 * Created by wangkangfei on 17/8/15.
 */

public class DragTextView extends FrameLayout {

    private int lastX = 0;
    private int lastY = 0;
    private int x1 = 0;
    private int x2 = 0;

    private int screenWidth = 10;
    private int screenHeight = 10;
    private Context context;
    private int dx;
    private int dy;
    RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(
            DisplayUtils.dip2px(getContext(), 150), DisplayUtils.dip2px(getContext(), 90));

    public DragTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScreenParam(context);
        this.context = context;
    }

    private void initScreenParam(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
    }

    public void configurationChanged() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;
    }

    private int threshold = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;

//                Log.e("onGlobal", "dx:" + dx + "-dy:" + dy);
                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = screenHeight - getHeight();
                }
                layout(left, top, right, bottom);
                lpFeedback.setMargins(left, top, 0, 0);
                setLayoutParams(lpFeedback);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
//                Log.e("onGlobal", getLeft() + ":" + getTop() + ":" + getRight() + ":" + getBottom());
                threshold = Math.max(threshold, Math.abs(dx) + Math.abs(dy));
                break;
            case MotionEvent.ACTION_UP:
                if (threshold > 10) {
                    threshold = 0;
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                //Log.e("onGlobal", getLeft() + ":" + getTop() + ":" + getRight() + ":" + getBottom());
                threshold = Math.max(threshold, Math.abs(dx) + Math.abs(dy));
                break;
            case MotionEvent.ACTION_UP:
                if (threshold > 10) {
                    threshold = 0;
                    Log.d("yjm", "DragTextView onInterceptTouchEvent invoke");
                    return true;
                }
        }
        return super.onInterceptTouchEvent(event);
    }
}

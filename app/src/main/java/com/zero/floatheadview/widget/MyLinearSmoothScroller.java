package com.zero.floatheadview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;

/**
 * Created by Administrator on 2016/5/24.
 */
public class MyLinearSmoothScroller extends LinearSmoothScroller{
    private SmoothScrollerListener smoothScrollerListener;
    public MyLinearSmoothScroller(Context context) {
        super(context);
    }

    public void setSmoothScrollerListener(SmoothScrollerListener smoothScrollerListener){
        this.smoothScrollerListener = smoothScrollerListener;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != smoothScrollerListener){
            smoothScrollerListener.onStop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(null != smoothScrollerListener){
            smoothScrollerListener.onStart();
        }
    }

    public interface SmoothScrollerListener{
        void onStart();
        void onStop();
    }
}

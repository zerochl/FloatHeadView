package com.zero.floatheadview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/5/24.
 */
public class MyLinearLayoutManager extends LinearLayoutManager{
    private Context context;
    private MyLinearSmoothScroller.SmoothScrollerListener smoothScrollerListener;
    public MyLinearLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }
    public void setSmoothScrollerListener(MyLinearSmoothScroller.SmoothScrollerListener smoothScrollerListener){
        this.smoothScrollerListener = smoothScrollerListener;
    }
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return MyLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
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
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }


}

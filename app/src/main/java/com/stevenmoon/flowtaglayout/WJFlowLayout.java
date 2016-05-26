package com.stevenmoon.flowtaglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by WangJun on 2016/5/24.
 */
public class WJFlowLayout extends ViewGroup {

    public static final String TAG = "WJFlowLayout";


    @IntDef({START, CENTER, END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GravityMode {}

    public static final int START = 0;
    public static final int CENTER = 1;
    public static final int END = 2;

    public static final int DEFAULT_SPACING = 8;
    public static final int DEFAULT_GRAVITY = START;

    private int mVerticalSpacing; //每个item纵向间距
    private int mHorizontalSpacing; //每个item横向间距
    private int mGravity;



    public WJFlowLayout(Context context) {
        this(context,null);
    }

    public WJFlowLayout(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WJFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs, defStyleAttr,0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WJFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WJFlowLayout, defStyleAttr, defStyleRes);

        mHorizontalSpacing = (int) ta.getDimension(R.styleable.WJFlowLayout_horizonSpacing,DEFAULT_SPACING);
        mVerticalSpacing = (int) ta.getDimension(R.styleable.WJFlowLayout_verticalSpacing,DEFAULT_SPACING);
        @GravityMode int index = ta.getInt(R.styleable.WJFlowLayout_gravity,DEFAULT_GRAVITY);
        if(index >= 0){
            setGravity(index);
        }
        ta.recycle();
    }


    public void setHorizontalSpacing(int pixelSize) {
        mHorizontalSpacing = pixelSize;
        requestLayout();
    }

    public void setVerticalSpacing(int pixelSize) {
        mVerticalSpacing = pixelSize;
        requestLayout();
    }

    public void setGravity(@GravityMode int gravity){
        mGravity = gravity;
    }

    public void setSpacing(int horizonSpacePx, int verticalSpacePx) {
        mHorizontalSpacing = horizonSpacePx;
        mVerticalSpacing = verticalSpacePx;
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //调用View的默认测量方法，当布局是wrap_content时，宽度还是measureWidth
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        //TODO 当此布局是wrap_content时，宽度不会是0么？PS:是0
//        int selfWidth = resolveSize(0, widthMeasureSpec);
//        Log.i(TAG, "self width: " + selfWidth);
//        if (measureWidthMode != MeasureSpec.EXACTLY) {
//            selfWidth = measureWidth;
//        }
        /*
        考虑自身的padding
        考虑子View的margin
        考虑子View是否显示？

        如果子View的宽度加起来大于流布局的可用宽度（宽度-padding），则换行

         */






        int rowWidth = getPaddingLeft() + mHorizontalSpacing, rowHeight = 0;
        int totalHeight = 0;
        int maxRowWidth = 0;
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // 测量每个child 用measureChildWithMargins()，而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, mHorizontalSpacing * 2, heightMeasureSpec, mVerticalSpacing * 2);
            MarginLayoutParams childLP = (MarginLayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight() + childLP.topMargin + childLP.bottomMargin;
            int childWidth = child.getMeasuredWidth() + childLP.leftMargin + childLP.rightMargin;
            //记录这一行的最大高度
            rowHeight = Math.max(rowHeight, childHeight);
            if (rowWidth + childWidth + getPaddingRight() + mHorizontalSpacing > selfWidth) {//需要换行
                maxRowWidth = Math.max(rowWidth, maxRowWidth);//记录最大宽度
                rowWidth = getPaddingLeft() + childWidth + mHorizontalSpacing;
                totalHeight += rowHeight + mVerticalSpacing;
                rowHeight = childHeight;
            } else {
                rowWidth += childWidth + mHorizontalSpacing;

            }

        }

        //以防有子View却没有换行的情况
        maxRowWidth = Math.max(rowWidth, maxRowWidth);//记录最大宽度
        totalHeight += rowHeight;
        totalHeight = totalHeight + getPaddingTop() + getPaddingBottom() + 2 * mHorizontalSpacing;

        //TODO 处理getMinimumWidth/height的情况

        setMeasuredDimension(measureWidthMode == MeasureSpec.EXACTLY ? selfWidth : Math.min(maxRowWidth, selfWidth),
                resolveSize(totalHeight, heightMeasureSpec));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measureWidth = getMeasuredWidth();
        Log.i(TAG, "onLayout--measureWidth：" + measureWidth);
        int childCount = getChildCount();
        int left = getPaddingLeft() + mHorizontalSpacing;
        int top = getPaddingTop() + mVerticalSpacing;
        int rowHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams childLP = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            //记录这一行的最大高度
            rowHeight = Math.max(rowHeight, childHeight + childLP.topMargin + childLP.bottomMargin);
            if (left + childWidth + childLP.leftMargin + childLP.rightMargin + getPaddingRight() + mHorizontalSpacing > measureWidth) {
                top += rowHeight + mVerticalSpacing;
                left = getPaddingLeft() + +mHorizontalSpacing;
                rowHeight = childHeight + childLP.topMargin + childLP.bottomMargin;
                int childTop = top + childLP.topMargin;
                int childLeft = left + childLP.leftMargin;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            } else {
                int childTop = top + childLP.topMargin;
                int childLeft = left + childLP.leftMargin;
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            }
            left += childWidth + childLP.leftMargin + childLP.rightMargin + mHorizontalSpacing;
            //TODO 加上Gravity

            Log.i(TAG, String.format("child %d (%d,%d,%d,%d)",
                    i, child.getLeft(), child.getTop(), child.getRight(), child.getBottom()));

        }

    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}

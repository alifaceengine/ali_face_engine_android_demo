package com.alibaba.cloud.alifaceenginedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by h on 2018/9/4.
 */

public class FaceFrameView extends android.support.v7.widget.AppCompatTextView {

    private Boolean result = false;
    private String mText = "";

    public FaceFrameView(Context context, int top, int bottom, int left, int right, Boolean result, String text) {
        super(context);
        this.result = result;
        this.mText = text;
    }

    public FaceFrameView(Context context) {
        super(context);
    }

    public FaceFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setResult(String text, boolean success) {
        this.result = success;
        this.mText = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String[] texts = mText.split(",");

        //绘制矩形
        Paint paint = new Paint();
        Paint tpaint = new Paint();
        if (result == true) {
            paint.setColor(Color.GREEN);
            tpaint.setColor(Color.GREEN);
        } else if (result == false) {
            paint.setColor(Color.RED);
            tpaint.setColor(Color.RED);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setTextSize(50);
        tpaint.setStyle(Paint.Style.FILL);
        tpaint.setTextSize(40);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        canvas.save();
        canvas.translate(20, 0);

        if (texts != null) {
            for (int i = 0; i < texts.length; i++) {
                canvas.drawText(texts[i], 10, 70 + i * 50, tpaint);
            }
        }

        super.onDraw(canvas);
        canvas.restore();
    }

}

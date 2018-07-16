package com.bot48.wavebubble;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


public class DoubleWaveBubbleView extends View {
    final int layerCount = 2;
    Context ctx;
    Paint mPaint;
    Path mPath;
    int mWidth;
    int mHeight;

    int mPercent = 0;
    int waveInterval = 20;
    int waveCount = 2;
    int[] waveColor;
    int mSpeed = 10;//offset increased by speed pre loop //TODO:
    int mWaveHeight=10;//peak height
    int mDx = 0;//offset distant current loop
    ValueAnimator valueAnimator;
    Interpolator interpolator;

    private String TAG = "BezierView";

    public DoubleWaveBubbleView(Context ctx) {
        this(ctx,null);
    }

    public DoubleWaveBubbleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPath = new Path();
        waveColor = new int[]{getColor(R.color.colorLightGreen), getColor(R.color.colorAccent)};
        interpolator = new LinearInterpolator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFrame(canvas);
        drawWave(canvas);
        drawText(canvas);
    }

    void drawFrame(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getColor(R.color.colorDimGray));
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth/2-2,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getColor(R.color.colorSilver));
        mPath.addCircle(mWidth/2,mHeight/2,mWidth/2-2,Path.Direction.CCW);
        canvas.drawPath(mPath,mPaint);
        if(mPercent>85){
            mPaint.setColor(getColor(R.color.colorFireBrick));
        }else if(mPercent>=75){
            mPaint.setColor(getColor(R.color.colorChocolate));
        }else {
            mPaint.setColor(getColor(R.color.colorGreen));
        }
        mPath.reset();
        mPath.addCircle(mWidth/2,mHeight/2,mWidth/2-8,Path.Direction.CCW);
        canvas.drawPath(mPath,mPaint);
        mPath.reset();
    }


    int mHalfWaveWidth;
    int mQuadWaveWidth;
    int baseLineY;
    int centerX,centerY;

    int getColor(int resId){
        return ContextCompat.getColor(ctx,resId);
    }
    void drawWave(Canvas canvas){
        if(mPercent>85){
            mPaint.setColor(getColor(R.color.colorRed));
        }else
        if(mPercent>=75){
            mPaint.setColor(getColor(R.color.colorOrange));
        }else{
            mPaint.setColor(getColor(R.color.colorLightGreen));
        }
        mPaint.setStyle(Paint.Style.FILL);
        canvas.save();// before clip the canvas save the complete canvas
        mPath.moveTo(-mHalfWaveWidth*2f+mDx,baseLineY);
        for(int i=0; i<waveCount+1; i++){
            mPath.rQuadTo(mQuadWaveWidth,mWaveHeight*2,mHalfWaveWidth,0);
            mPath.rQuadTo(mQuadWaveWidth,-mWaveHeight*2,mHalfWaveWidth,0);
        }
        mPath.lineTo(mWidth,mHeight);
        mPath.lineTo(0,mHeight);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        canvas.drawCircle(centerX,centerY,mWidth/2-10,mPaint);
        mPath.reset();
        canvas.restore();//restore full canvas in order to draw complete text
    }

    void drawText(Canvas canvas){
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(25f);
        mPaint.setColor(getColor(R.color.colorWhite));
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(mPercent)+"%",mWidth/2,mHeight/2,mPaint);
    }

    private int lastX;
    private int lastY;//if define as float will result in cut display error

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX()-lastX);
                int dy = (int) (event.getY()-lastY);
                layout((getLeft()+dx),(getTop()+dy),(getRight()+dx),(getBottom()+dy));
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth,mHeight);
        mHalfWaveWidth = getMeasuredWidth()/waveCount/2;
        mQuadWaveWidth = mHalfWaveWidth/2;
        baseLineY = (int) ((1f-mPercent/100f)*getMeasuredHeight());//wave base line y value
        centerX = getMeasuredWidth()/2;
        centerY = getMeasuredHeight()/2;
        valueAnimator = ValueAnimator.ofInt(0,mHalfWaveWidth*2);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDx = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void setPercent(int mPercent) {
        this.mPercent = mPercent;
        baseLineY = (int) ((1f-mPercent/100f)*getMeasuredHeight());
        invalidate();
    }
}

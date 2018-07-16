package com.bot48.wavebubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * simple demonstrate BezierCure
 */
public class BezierWaveView extends View {

    Context ctx;
    Paint mPaint;
    Path mPath ;
    private String TAG = "BezierView";

    public BezierWaveView(Context context) {
        this(context,null);
    }

    public BezierWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        mPaint = new Paint();
        mPath = new Path();
    }

    //fixed line end point
    float lineStartX,lineEndX;
    float lineStartY,lineEndY;
    //coordinate of the control point
    private float ctrX;
    private float ctrY;

    @Override
    protected void onDraw(Canvas canvas) {
        drawWave(canvas);
    }

    void drawWave(Canvas canvas){
        lineStartX =  getMeasuredWidth()/8;
        lineEndX = getMeasuredWidth()*7/8;
        lineStartY=lineEndY = getMeasuredHeight()/2;

        mPaint.setColor(ContextCompat.getColor(ctx,R.color.colorAccent));
        mPaint.setStrokeWidth(2f);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(lineStartX,lineStartY,lineEndX,lineEndY,mPaint);
        canvas.drawLine(lineStartX,lineStartY,ctrX,ctrY,mPaint);
        canvas.drawLine(ctrX,ctrY,getMeasuredWidth()/2,lineEndY,mPaint);
        mPath.addCircle(ctrX,ctrY,10f,Path.Direction.CCW);
        canvas.drawPath(mPath,mPaint);

        mPaint.setColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
        mPath.moveTo(lineStartX,lineStartY);
        mPath.quadTo(ctrX,ctrY,getMeasuredWidth()/2,lineEndY);
        mPath.quadTo(ctrX+getMeasuredWidth()*3/8,lineEndY+lineEndY-ctrY,lineEndX,lineEndY);
        canvas.drawPath(mPath,mPaint);
        mPath.reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ctrX = getMeasuredWidth()*5/16;
        ctrY = getMeasuredHeight()/4;
    }

    float lastX=0;
    float lastY=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=lastY=0;
                if(Math.sqrt((ctrX-event.getX())*(ctrX-event.getX())+
                        (ctrY-event.getY())*(ctrY-event.getY()))<10){
                    lastX = event.getX();
                    lastY = ctrY = event.getY();
                }
                break;
                case MotionEvent.ACTION_MOVE:
                    if(lastX==0||lastY==0)
                    break;
                    lastX  = event.getX();
                    lastY = ctrY = event.getY();
                    invalidate();
                break;
        }
        return true;
    }
}

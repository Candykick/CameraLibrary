package com.candykick.cdkgallery.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by candykick on 2019. 9. 30..
 */

//화면 밖으로는 드래그 안 되게 하는 것도 추가해야 함

public class CroppingView extends View
{
    //ArrayList<Point> points = new ArrayList<>();
    Point startPoint, endPoint;

    Paint p = new Paint();
    int color = Color.BLACK;

    boolean isRatio = false, isCircle = false;
    int xRatio, yRatio;

    public CroppingView(Context context) { super(context); }

    public void setRatio(boolean isRatio) {
        this.isRatio = isRatio;
        this.isCircle = false;
    }
    public void setRatio(boolean isRatio, int xRatio, int yRatio) {
        this.isRatio = isRatio;
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.isCircle = false;
    }
    public void setRatio(boolean isRatio, int xRatio, int yRatio, boolean isCircle) {
        this.isRatio = isRatio;
        this.xRatio = xRatio;
        this.yRatio = yRatio;
        this.isCircle = isCircle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        p.setStrokeWidth(5);

        if(startPoint != null && endPoint != null) {
            if(isCircle) {
                if(Math.abs(startPoint.x - endPoint.x) > Math.abs(startPoint.y - endPoint.y)) {
                    float radius = Math.abs(startPoint.x - endPoint.x)/2;
                } else {
                    float radius = Math.abs(startPoint.y - endPoint.y)/2;
                }
            } else {
                canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, p);
            }
        }
        /*for(int i=1 ; i<points.size() ; i++)
        {
            p.setColor(points.get(i).color);
            if(!points.get(i).check)
                continue;
            //canvas.drawLine(points.get(i-1).x,points.get(i-1).y,points.get(i).x,points.get(i).y,p);
            canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, p);
        }*/
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startPoint = new Point(x, y, false, color);
                //points.add(new Point(x,y,false , color));
            case MotionEvent.ACTION_MOVE :
                //points.add(new Point(x,y,true , color));
                //break;
            case MotionEvent.ACTION_UP :
                if(isRatio) {
                    if(xRatio > yRatio) {
                        float ratio = (float)xRatio/(float)yRatio;
                        if (startPoint.x < x) {
                            x = startPoint.x + (Math.abs(y - startPoint.y) * ratio);
                        } else {
                            x = startPoint.x - (Math.abs(y - startPoint.y) * ratio);
                        }
                    } else {
                        float ratio = (float)yRatio/(float)xRatio;
                        if (startPoint.y < y) {
                            y = startPoint.y + (Math.abs(x - startPoint.x) * ratio);
                        } else {
                            y = startPoint.y - (Math.abs(x - startPoint.x) * ratio);
                        }
                    }
                    /*if (Math.abs(x - startPoint.x) > Math.abs(y - startPoint.y)) {
                        if (startPoint.y < y) {
                            y = startPoint.y + Math.abs(x - startPoint.x);
                        } else {
                            y = startPoint.y - Math.abs(x - startPoint.x);
                        }
                    } else {
                        if (startPoint.x < x) {
                            x = startPoint.x + Math.abs(y - startPoint.y);
                        } else {
                            x = startPoint.x - Math.abs(y - startPoint.y);
                        }
                    }*/
                }
                endPoint = new Point(x, y, false, color);
                break;
        }
        invalidate();
        return true;
    }

    class Point{
        float x;
        float y;
        boolean check;
        int color;

        public Point(float x, float y, boolean check,int color)
        {
            this.x = x;
            this.y = y;
            this.check = check;
            this.color = color;
        }
    }
}
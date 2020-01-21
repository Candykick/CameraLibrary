package com.candykick.cdkgallery.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.candykick.cdkgallery.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by candykick on 2019. 9. 30..
 */

public class CroppingViewEx extends View {

    Context context;

    Point startPoint, endPoint;
    Paint paint = new Paint();
    int color = Color.BLACK;
    int colorForeground = 0x33000000;

    Bitmap bitmap;
    byte[] bitmapBytes;
    int width, height;

    boolean isRatio = false, isCircle = false;
    int xRatio, yRatio;

    public CroppingViewEx(Context context, String path) {
        super(context);

        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        Bitmap original = BitmapFactory.decodeFile(path);
        float scale = ((width/(float)original.getWidth()));
        int image_w = (int)(original.getWidth()*scale);
        int image_h = (int)(original.getHeight()*scale);
        bitmap = Bitmap.createScaledBitmap(original, image_w, image_h, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bitmapBytes = stream.toByteArray();

        setBackgroundColor(context.getResources().getColor(R.color.cropBackground));
    }
    public CroppingViewEx(Context context, AttributeSet attrs, String path) {
        super(context, attrs);

        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        Bitmap original = BitmapFactory.decodeFile(path);
        float scale = ((width/(float)original.getWidth()));
        int image_w = (int)(original.getWidth()*scale);
        int image_h = (int)(original.getHeight()*scale);
        bitmap = Bitmap.createScaledBitmap(original, image_w, image_h, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bitmapBytes = stream.toByteArray();

        setBackgroundColor(context.getResources().getColor(R.color.cropBackground));
    }

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
        if(bitmap.getWidth() > bitmap.getHeight()) {
            canvas.drawBitmap(bitmap, 0, (int)(this.height/2-(bitmap.getHeight()/2)), null);
        } else {
            canvas.drawBitmap(bitmap, (int)(this.width/2-(bitmap.getWidth()/2)), 0, null);
        }

        paint.setStrokeWidth(5);
        paint.setColor(colorForeground);

        if(startPoint != null && endPoint != null) {
            if(isCircle) {
                canvas.drawCircle(startPoint.x ,startPoint.y, Math.abs(startPoint.x - endPoint.x), paint);
            } else {
                canvas.drawRect(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
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

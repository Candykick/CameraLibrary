package com.candykick.cdkgallery.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.candykick.cdkgallery.R;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 24..
 */

public class DrawingImageView extends View {

    //이미지 사이즈가 너무 큰 경우
    //BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(myStream, false);
    //Bitmap region = decoder.decodeRegion(new Rect(10, 10, 50, 50), null);

    Context context;
    int pointIndex = -1;
    int lineNum = 0;
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Integer> startPointIndex = new ArrayList<>();
    //float downx = 0; float downy = 0; float upx = 0; float upy = 0;

    Paint paint;
    boolean isPencil = true;
    int color = Color.BLACK;
    float penWidth = 5f;

    Bitmap bitmap;
    byte[] bitmapBytes;
    int width, height;

    public DrawingImageView(Context context, String path) {
        super(context);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

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

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(color);
    }
    public DrawingImageView(Context context, AttributeSet attrs, String path) {
        super(context, attrs);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

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

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(penWidth);
        paint.setColor(color);
    }
    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr, String path) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

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

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(color);
    }

    public void setPenColor(int color) {
        this.color = color;
    }
    public void setPencil(boolean isPencil) {
        this.isPencil = isPencil;
        if(isPencil) {
            paint.setStrokeWidth(5);
            paint.setColor(color);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        } else {
            paint.setStrokeWidth(25);
            paint.setColor(Color.TRANSPARENT);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }
    public void undo() {
        /*if(pointIndex != 0) {
            pointIndex = pointIndex - 2;
            invalidate();
        }*/
        if(lineNum == 1) {
            lineNum--;
            pointIndex = -1;
            invalidate();
        }
        else if(lineNum != 0) {
            lineNum--;
            pointIndex = startPointIndex.get(lineNum) - 1;
            invalidate();
        }
    }
    public void redo() {
        /*if(pointIndex != points.size() - 1) {
            pointIndex = pointIndex + 2;
            invalidate();
        }*/
        if(lineNum == startPointIndex.size() - 1) {
            lineNum++;
            pointIndex = points.size() - 1;
            invalidate();
        }
        else if(lineNum != startPointIndex.size()) {
            lineNum++;
            pointIndex = startPointIndex.get(lineNum) - 1;
            invalidate();
        }
    }
    public void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        // bfirstpoint = false;
                        pointIndex = -1;
                        lineNum = 0;
                        points.clear();
                        startPointIndex.clear();
                        invalidate();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.delete_drawing_all))
                .setPositiveButton(context.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(context.getString(R.string.no), dialogClickListener).show()
                .setCancelable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap.getWidth() > bitmap.getHeight()) {
            canvas.drawBitmap(bitmap, 0, (int)(this.height/2-(bitmap.getHeight()/2)), null);
        } else {
            canvas.drawBitmap(bitmap, (int)(this.width/2-(bitmap.getWidth()/2)), 0, null);
        }

        for(int i=1 ; i<=pointIndex; i++)
        {
            if(points.get(i).ispencil) {
                paint.setStrokeWidth(5);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

                if (!points.get(i).check)
                    continue;

                paint.setColor(points.get(i - 1).color);
                canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, paint);
            } else {
                paint.setStrokeWidth(25);
                paint.setColor(Color.TRANSPARENT);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

                if (!points.get(i).check)
                    continue;

                canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, paint);
            }
        }

        /*Path path = new Path();
        boolean first = true;

        for(int i=0;i<points.size();i+=2) {
            Point point = points.get(i);
            if(first) {
                first = false;
                path.moveTo(point.x, point.y);
            } else if(i < points.size() - 1) {
                Point next = points.get(i+1);
                path.quadTo(point.x, point.y, next.x, next.y);
            } else {
                lastPoint = points.get(i);
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, paint); */
        //paths.add(path);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointIndex++;
                if(pointIndex == points.size()) {
                    points.add(new Point(x, y, false, color, isPencil));
                    startPointIndex.add(pointIndex);
                }
                else {
                    points.set(pointIndex, new Point(x, y, false, color, isPencil));
                    //startPointIndex.set(lineNum, pointIndex);
                }
            case MotionEvent.ACTION_MOVE:
                pointIndex++;
                if(pointIndex == points.size())
                    points.add(new Point(x, y, true, color, isPencil));
                else
                    points.set(pointIndex, new Point(x, y, true, color, isPencil));
                break;
            case MotionEvent.ACTION_UP:
                lineNum++;

                if(pointIndex < points.size()-1) {
                    for(int i=pointIndex+1; i<points.size(); i++)
                        points.remove(i);
                }
                break;
        }
        invalidate();

        return true;
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoords(event)[0];//event.getX();
                downy = getPointerCoords(event)[1];//event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];
                upy = getPointerCoords(event)[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];
                upy = getPointerCoords(event)[1];
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        return true;
    }

    final float[] getPointerCoords(MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[]{e.getX(index), e.getY(index)};
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }*/

    class Point{
        float x;
        float y;
        boolean check;
        int color;
        boolean ispencil;

        public Point(float x, float y, boolean check,int color,boolean ispencil)
        {
            this.x = x;
            this.y = y;
            this.check = check;
            this.color = color;
            this.ispencil = ispencil;
        }
    }
}

    /*private fun setRecyclerView(items: MutableList<UserAccountItem>) {
        val userAccountAdapter = object : UserAccountAdapter.ACallback {
            override fun onClickCheckBox(position: Int, isChecked: Boolean) {
                showToast("position = $position, isChecked = $isChecked")
            }
        }

        binding.userAccountRv.apply {
            this.adapter = UserAccountAdapter(items, userAccountAdapter)
            this.layoutManager = LinearLayoutManager(this@UserAccountActivity)
        }
    }*/

package com.candykick.cdkgallery.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candykick.cdkgallery.R;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 20..
 */

public class ImageViewSimpleAdapter extends PagerAdapter {

    private Context context;
    //private ArrayList<String> images;
    private ArrayList<Bitmap> images;
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private LayoutInflater inflater;

    public ImageViewSimpleAdapter(Context context, ArrayList<Bitmap> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ViewGroup) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.rawsimpleimage, container, false);
        ImageView imageView = v.findViewById(R.id.ivRawVPSimpleImage);
        Glide.with(context).load(images.get(position)).into(imageView);
        imageViews.add(imageView);

        /*if(isSrc) {
            if(imagesrcList.get(position).equals("")) {
                imageView.setVisibility(View.GONE);
                xButton.setVisibility(View.GONE);
                addButton.setVisibility(View.VISIBLE);

                addButton.setOnClickListener(view -> {
                    Pix.start(context,                    //Activity or Fragment Instance
                            1009,                //Request code for activity results
                            9);
                });
            } else {
                imageView.setVisibility(View.VISIBLE);
                xButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.GONE);
                Glide.with(context).load(imagesrcList.get(position)).into(imageView);
            }
        } else {
            Glide.with(context).load(imageList.get(position)).into(imageView);
        }*/

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

    public ImageView getSelectedImageView(int position) {
        return imageViews.get(position);
    }
}

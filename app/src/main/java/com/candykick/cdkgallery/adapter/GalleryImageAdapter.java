package com.candykick.cdkgallery.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.candykick.cdkgallery.R;
import com.candykick.cdkgallery.data.GalleryImageData;

import java.util.List;

/**
 * Created by candykick on 2019. 9. 23..
 */

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.ViewHolder> {
    List<String> imageList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivGalleryImage;

        public ViewHolder(View view) {
            super(view);
            this.ivGalleryImage = view.findViewById(R.id.ivRawImage);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                mListener.onItemClick(v, position);
            });
        }
    }

    public GalleryImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.raw_image_adapter,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {
            Glide.with(context).load(imageList.get(position)).into(viewHolder.ivGalleryImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}


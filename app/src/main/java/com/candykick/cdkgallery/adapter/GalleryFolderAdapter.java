package com.candykick.cdkgallery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.candykick.cdkgallery.R;
import com.candykick.cdkgallery.data.GalleryFolderData;

import java.util.List;

public class GalleryFolderAdapter extends RecyclerView.Adapter<GalleryFolderAdapter.ViewHolder> {

    List<GalleryFolderData> folderList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivFolderImage;
        protected TextView tvFolderName, tvFolderImageNum;

        public ViewHolder(View view) {
            super(view);
            this.ivFolderImage = view.findViewById(R.id.ivRawFolderSrc);
            this.tvFolderName = view.findViewById(R.id.tvRawFolderName);
            this.tvFolderImageNum = view.findViewById(R.id.tvRawFolderNum);

            view.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, position);
                    }
                });
        }
    }

    public GalleryFolderAdapter(Context context, List<GalleryFolderData> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.raw_folder_adapter,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        try {
            viewHolder.tvFolderName.setText(folderList.get(position).folderName);
            viewHolder.tvFolderImageNum.setText(""+folderList.get(position).folderImageNum);
            Glide.with(context).load(folderList.get(position).folderImageSrc).into(viewHolder.ivFolderImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }
}


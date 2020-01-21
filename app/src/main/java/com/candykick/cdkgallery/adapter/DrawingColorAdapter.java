package com.candykick.cdkgallery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.candykick.cdkgallery.R;

/**
 * Created by candykick on 2019. 10. 1..
 */

public class DrawingColorAdapter extends RecyclerView.Adapter<DrawingColorAdapter.ViewHolder> {

    private int colorPickerDrawable[] = new int[]{R.drawable.colorcircle_black, R.drawable.colorcircle_white,
            R.drawable.colorcircle_red, R.drawable.colorcircle_orange, R.drawable.colorcircle_yellow, R.drawable.colorcircle_vividgreen,
            R.drawable.colorcircle_green, R.drawable.colorcircle_blue, R.drawable.colorcircle_cobaltblue, R.drawable.colorcircle_violet,
            R.drawable.colorcircle_pink, R.drawable.colorcircle_oak, R.drawable.colorcircle_ochre};
    private int colorPickerHex[] = new int[]{0xFF000000,0xFFFFFFFF,
            0xFFF71A1B,0xFFFCB02A,0xFFFEE76E,0xFF19AF1D,
            0xFF138C1A,0xFF1F84B6,0xFF6156C6,0xFF651E94,
            0xFFFB1580,0xFF87120A,0xFFC17C1D};
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(View v, int color);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivRawColor, ivRawColorSelected;

        public ViewHolder(View view) {
            super(view);
            this.ivRawColor = view.findViewById(R.id.ivRawColor);
            this.ivRawColorSelected = view.findViewById(R.id.ivRawColorSelected);

            view.setOnClickListener(v -> {
                this.ivRawColorSelected.setVisibility(View.VISIBLE);
                int position = getAdapterPosition();
                mListener.onItemClick(v, colorPickerHex[position]);
            });
        }
    }

    public DrawingColorAdapter(Context context) {
        this.context = context;
    }

    // RecyclerView에 새로운 데이터를 보여주기 위해 필요한 ViewHolder를 생성해야 할 때 호출됩니다.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.raw_drawing_color,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Adapter의 특정 위치(position)에 있는 데이터를 보여줘야 할때 호출됩니다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.ivRawColor.setImageResource(colorPickerDrawable[position]);
    }

    @Override
    public int getItemCount() {
        return colorPickerDrawable.length;
    }
}


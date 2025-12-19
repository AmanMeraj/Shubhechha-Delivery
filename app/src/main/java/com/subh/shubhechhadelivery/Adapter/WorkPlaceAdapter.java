package com.subh.shubhechhadelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.subh.shubhechhadelivery.Model.WorkPlace;
import com.subh.shubhechhadelivery.R;

import java.util.ArrayList;
import java.util.List;

public class WorkPlaceAdapter extends RecyclerView.Adapter<WorkPlaceAdapter.WorkPlaceViewHolder> {

    private Context context;
    private List<WorkPlace> workPlaceList;
    private OnWorkPlaceClickListener listener;

    public interface OnWorkPlaceClickListener {
        void onWorkPlaceClick(WorkPlace workPlace);
    }

    public WorkPlaceAdapter(Context context, List<WorkPlace> workPlaceList) {
        this.context = context;
        this.workPlaceList = workPlaceList != null ? workPlaceList : new ArrayList<>();
    }

    public void setOnWorkPlaceClickListener(OnWorkPlaceClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_card, parent, false);
        return new WorkPlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkPlaceViewHolder holder, int position) {
        WorkPlace workPlace = workPlaceList.get(position);
        holder.tvStoreName.setText(workPlace.getStoreName());

        // Load image with Glide if URL is available, otherwise use default
        String imageUrl = workPlace.getStoreImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.subh_img2)
                    .error(R.drawable.subh_img2)
                    .centerCrop()
                    .into(holder.ivStoreImage);
        } else {
            holder.ivStoreImage.setImageResource(R.drawable.subh_img2);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWorkPlaceClick(workPlace);
            } else {
                Toast.makeText(context, "Opening " + workPlace.getStoreName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return workPlaceList != null ? workPlaceList.size() : 0;
    }

    public void updateList(List<WorkPlace> newList) {
        this.workPlaceList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class WorkPlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStoreImage, ivStoreIcon;
        TextView tvStoreName;

        public WorkPlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoreImage = itemView.findViewById(R.id.ivStoreImage);
            ivStoreIcon = itemView.findViewById(R.id.ivStoreIcon);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
        }
    }
}
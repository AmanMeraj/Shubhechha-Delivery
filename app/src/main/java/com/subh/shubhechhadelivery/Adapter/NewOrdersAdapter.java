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

import com.subh.shubhechhadelivery.Model.NewOrder;
import com.subh.shubhechhadelivery.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewOrdersAdapter extends RecyclerView.Adapter<NewOrdersAdapter.NewOrderViewHolder> {

    private Context context;
    private List<NewOrder> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(NewOrder order);
    }

    public NewOrdersAdapter(Context context, List<NewOrder> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_orders, parent, false);
        return new NewOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderViewHolder holder, int position) {
        NewOrder order = orderList.get(position);

        holder.tvStoreName.setText(order.getOwnerName());
        holder.tvStoreNameLabel.setText(order.getStoreName());
        holder.tvLocation.setText(order.getLocation());
        holder.tvProcessingBadge.setText(order.getStatus());

        // Set profile image from drawable (you can use Glide/Picasso for URLs)
        holder.ivStoreProfile.setImageResource(R.drawable.cart_profile);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            } else {
                Toast.makeText(context, "Opening order: " + order.getStoreName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.ivArrowNext.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            } else {
                Toast.makeText(context, "Viewing details for " + order.getStoreName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(List<NewOrder> newList) {
        this.orderList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class NewOrderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivStoreProfile;
        TextView tvStoreNameLabel, tvStoreName, tvLocation, tvProcessingBadge;
        ImageView ivArrowNext;

        public NewOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoreProfile = itemView.findViewById(R.id.ivStoreProfile);
            tvStoreNameLabel = itemView.findViewById(R.id.tvStoreNameLabel);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvProcessingBadge = itemView.findViewById(R.id.tvProcessingBadge);
            ivArrowNext = itemView.findViewById(R.id.ivArrowNext);
        }
    }
}
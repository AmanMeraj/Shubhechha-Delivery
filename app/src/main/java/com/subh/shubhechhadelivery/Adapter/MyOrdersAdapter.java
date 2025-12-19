package com.subh.shubhechhadelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechhadelivery.Model.MyOrdersResponse;
import com.subh.shubhechhadelivery.databinding.ItemOrderBinding;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    private Context context;
    private List<MyOrdersResponse.Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(MyOrdersResponse.Order order, int position);
    }

    public MyOrdersAdapter(Context context, List<MyOrdersResponse.Order> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyOrdersResponse.Order order = orderList.get(position);

        holder.binding.tvOrderId.setText("Order ID : #" + order.getOrderno());
        holder.binding.tvFinalTotal.setText("Final Total : ₹ " + order.getTotal());
//        holder.binding.ivArrow.setVisibility(View.GONE);

        // Set up horizontal LayoutManager for product images
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        holder.binding.rvProductImages.setLayoutManager(layoutManager);
        holder.binding.rvProductImages.setHasFixedSize(true);

        // Set up product images adapter
        ArrayList<String> imageUrls = order.getItem_images_array() != null ?
                new ArrayList<>(order.getItem_images_array()) : new ArrayList<>();

        ProductImageAdapter productAdapter = new ProductImageAdapter(imageUrls, 4);
        holder.binding.rvProductImages.setAdapter(productAdapter);

        // For history orders - hide time and buttons, show arrow
        holder.binding.linearTime.setVisibility(View.GONE);
        holder.binding.buttonsLayout.setVisibility(View.GONE);
        holder.binding.ivArrow.setVisibility(View.VISIBLE);

        // Handle arrow button click
        holder.binding.ivArrow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order, holder.getAdapterPosition());
            }
        });

        // Handle card click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    /**
     * Update the entire list with new data
     */
    public void updateList(List<MyOrdersResponse.Order> newList) {
        this.orderList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Remove an item from the list at the specified position
     */
    public void removeItem(int position) {
        if (orderList != null && position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());
        }
    }

    /**
     * Remove an order from the list by order ID
     */
    public void removeOrderById(int orderId) {
        if (orderList != null) {
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).getId() == orderId) {
                    removeItem(i);
                    break;
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;

        public ViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
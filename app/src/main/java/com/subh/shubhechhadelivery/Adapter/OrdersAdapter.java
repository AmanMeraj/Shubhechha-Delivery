package com.subh.shubhechhadelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechhadelivery.Model.HomeResponse;
import com.subh.shubhechhadelivery.databinding.ItemOrderBinding;
import com.subh.shubhechhadelivery.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context context;
    private List<HomeResponse.Order> orderList;
    private OnOrderClickListener listener;
    private OnOrderActionListener actionListener;

    public interface OnOrderClickListener {
        void onOrderClick(HomeResponse.Order order, int position);
    }

    public interface OnOrderActionListener {
        void onAcceptOrder(HomeResponse.Order order, int position);
        void onRejectOrder(HomeResponse.Order order, int position);
    }

    public OrdersAdapter(Context context, List<HomeResponse.Order> orderList) {
        this.context = context;
        this.orderList = orderList != null ? orderList : new ArrayList<>();
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOnOrderActionListener(OnOrderActionListener actionListener) {
        this.actionListener = actionListener;
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
        HomeResponse.Order order = orderList.get(position);

        holder.binding.tvOrderId.setText("Order ID : #" + order.orderno);
        holder.binding.tvFinalTotal.setText("Final Total : ₹ " + order.total);

        // Set up horizontal LayoutManager for product images
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        holder.binding.rvProductImages.setLayoutManager(layoutManager);
        holder.binding.rvProductImages.setHasFixedSize(true);

        // Set up product images adapter
        ArrayList<String> imageUrls = order.item_images_array != null ?
                order.item_images_array : new ArrayList<>();

        ProductImageAdapter productAdapter = new ProductImageAdapter(imageUrls, 4);
        holder.binding.rvProductImages.setAdapter(productAdapter);

        // Handle visibility based on order status
        // Show buttons only for "pending" status, show arrow for all other statuses
        boolean isPending = "pending".equalsIgnoreCase(order.status);

        if (isPending) {
            // For pending orders - show time and buttons, hide arrow
            holder.binding.linearTime.setVisibility(View.VISIBLE);
            holder.binding.buttonsLayout.setVisibility(View.VISIBLE);
            holder.binding.ivArrow.setVisibility(View.GONE);

            // Set time text from created_at
            if (order.created_at != null && !order.created_at.isEmpty()) {
                holder.binding.tvTime.setText(formatTime(order.created_at));
            }
        } else {
            // For all other statuses (completed, delivered, accepted, rejected, etc.)
            // Hide time and buttons, show arrow
            holder.binding.linearTime.setVisibility(View.GONE);
            holder.binding.buttonsLayout.setVisibility(View.GONE);
            holder.binding.ivArrow.setVisibility(View.VISIBLE);
        }

        // Handle Accept button click
        holder.binding.btnAccept.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onAcceptOrder(order, holder.getAdapterPosition());
            }
        });

        // Handle Reject button click
        holder.binding.btnReject.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRejectOrder(order, holder.getAdapterPosition());
            }
        });

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
    public void updateList(List<HomeResponse.Order> newList) {
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
                if (orderList.get(i).id == orderId) {
                    removeItem(i);
                    break;
                }
            }
        }
    }

    /**
     * Format timestamp to display relative time
     */
    private String formatTime(String createdAt) {
        return TimeUtil.getRelativeTime(createdAt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;

        public ViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
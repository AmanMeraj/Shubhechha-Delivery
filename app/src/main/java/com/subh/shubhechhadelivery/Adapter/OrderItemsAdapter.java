package com.subh.shubhechhadelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechhadelivery.Model.OrderItem;
import com.subh.shubhechhadelivery.databinding.ItemOrderdItemsBinding;

import java.util.ArrayList;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderItem> itemList;

    public OrderItemsAdapter(Context context, ArrayList<OrderItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderdItemsBinding binding = ItemOrderdItemsBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = itemList.get(position);

        // Display item name with quantity
        String displayName = item.getItemName() + " x" + item.getQuantity();
        holder.binding.tvName.setText(displayName);

        // Display amount
        holder.binding.tvAmount.setText("₹" + item.getItemAmount());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderdItemsBinding binding;

        public ViewHolder(ItemOrderdItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
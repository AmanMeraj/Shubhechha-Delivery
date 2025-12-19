package com.subh.shubhechhadelivery.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.subh.shubhechhadelivery.R;

import java.util.ArrayList;
import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder> {

    private List<Object> displayItems;
    private int moreItemsCount;
    private int maxVisibleItems;

    public ProductImageAdapter(List<String> imageUrls, int maxVisibleItems) {
        this.maxVisibleItems = maxVisibleItems;
        this.displayItems = new ArrayList<>();

        if (imageUrls != null && !imageUrls.isEmpty()) {
            if (imageUrls.size() > maxVisibleItems) {
                // Show first (maxVisibleItems - 1) items + "More" indicator
                for (int i = 0; i < maxVisibleItems - 1; i++) {
                    displayItems.add(imageUrls.get(i));
                }
                displayItems.add("MORE");
                moreItemsCount = imageUrls.size() - (maxVisibleItems - 1);
            } else {
                // Show all items
                displayItems.addAll(imageUrls);
                moreItemsCount = 0;
            }
        }
    }

    @NonNull
    @Override
    public ProductImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image, parent, false);
        return new ProductImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageViewHolder holder, int position) {
        Object item = displayItems.get(position);

        if (item instanceof String && !item.equals("MORE")) {
            // This is an image URL string
            holder.bind((String) item);
        } else if (item.equals("MORE")) {
            // This is the "MORE" indicator
            holder.bindMoreIndicator(moreItemsCount);
        }
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    static class ProductImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private LinearLayout llMoreItems;
        private TextView tvMoreCount;

        public ProductImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            llMoreItems = itemView.findViewById(R.id.llMoreItems);
            tvMoreCount = itemView.findViewById(R.id.tvMoreCount);
        }

        public void bind(String imageUrl) {
            ivProduct.setVisibility(View.VISIBLE);
            llMoreItems.setVisibility(View.GONE);

            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.kk_vegetables_category) // default placeholder
                    .error(R.drawable.kk_vegetables_category) // error image
                    .centerCrop()
                    .into(ivProduct);
        }

        public void bindMoreIndicator(int count) {
            ivProduct.setVisibility(View.GONE);
            llMoreItems.setVisibility(View.VISIBLE);
            tvMoreCount.setText("+" + count);
        }
    }
}
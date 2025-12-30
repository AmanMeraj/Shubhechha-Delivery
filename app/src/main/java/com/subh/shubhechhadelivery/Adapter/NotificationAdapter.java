package com.subh.shubhechhadelivery.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechhadelivery.Model.NotificationResponse;
import com.subh.shubhechhadelivery.databinding.ItemNotificationBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationResponse.NotificationItem> notificationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationResponse.NotificationItem notification);
    }

    public NotificationAdapter() {
        this.notificationList = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationResponse.NotificationItem notification = notificationList.get(position);
        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    /**
     * Add new notifications to the list (for pagination)
     */
    public void addNotifications(List<NotificationResponse.NotificationItem> newNotifications) {
        if (newNotifications != null && !newNotifications.isEmpty()) {
            int startPosition = notificationList.size();
            notificationList.addAll(newNotifications);
            notifyItemRangeInserted(startPosition, newNotifications.size());
        }
    }

    /**
     * Set notifications (replace existing list)
     */
    public void setNotifications(List<NotificationResponse.NotificationItem> notifications) {
        this.notificationList.clear();
        if (notifications != null) {
            this.notificationList.addAll(notifications);
        }
        notifyDataSetChanged();
    }

    /**
     * Clear all notifications
     */
    public void clearNotifications() {
        notificationList.clear();
        notifyDataSetChanged();
    }

    /**
     * Get current notification list
     */
    public List<NotificationResponse.NotificationItem> getNotificationList() {
        return notificationList;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationResponse.NotificationItem notification, OnItemClickListener listener) {
            if (notification == null) return;

            // Set title
            binding.tvTitle.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");

            // Set description
            binding.tvSubtitle.setText(notification.getDescription() != null ? notification.getDescription() : "");

            // Set formatted date
            binding.tvDate.setText(getFormattedDate(notification.getCreated_at()));

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(notification);
                }
            });
        }

        /**
         * Format date to relative time (e.g., "2 hours ago", "3 days ago")
         */
        private String getFormattedDate(String createdAt) {
            if (createdAt == null || createdAt.isEmpty()) {
                return "";
            }

            try {
                // Parse the date string (adjust format based on your API response)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date notificationDate = sdf.parse(createdAt);

                if (notificationDate == null) {
                    return createdAt;
                }

                long timeInMillis = notificationDate.getTime();
                long currentTimeMillis = System.currentTimeMillis();
                long diffInMillis = currentTimeMillis - timeInMillis;

                long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
                long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
                long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                if (seconds < 60) {
                    return "Just now";
                } else if (minutes < 60) {
                    return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
                } else if (hours < 24) {
                    return hours + (hours == 1 ? " hour ago" : " hours ago");
                } else if (days < 7) {
                    return days + (days == 1 ? " day ago" : " days ago");
                } else if (days < 30) {
                    long weeks = days / 7;
                    return weeks + (weeks == 1 ? " week ago" : " weeks ago");
                } else if (days < 365) {
                    long months = days / 30;
                    return months + (months == 1 ? " month ago" : " months ago");
                } else {
                    long years = days / 365;
                    return years + (years == 1 ? " year ago" : " years ago");
                }

            } catch (ParseException e) {
                e.printStackTrace();
                return createdAt;
            }
        }
    }
}
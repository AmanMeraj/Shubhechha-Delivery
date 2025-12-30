package com.subh.shubhechhadelivery.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechhadelivery.Adapter.NotificationAdapter;
import com.subh.shubhechhadelivery.Model.NotificationResponse;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.ActivityNotificationBinding;
import com.subh.shubhechhadelivery.utils.Utility;

import java.util.List;

public class NotificationActivity extends Utility {

    private ActivityNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private ViewModel viewModel;

    // Pagination variables
    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String nextPageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityNotificationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Initialize ViewModel
            viewModel = new ViewModelProvider(this).get(ViewModel.class);

            initializeViews();
            setupCollapsingToolbar();
            setupRecyclerView();
            observeViewModel();
            loadNotifications();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize notifications");
        }
    }

    private void initializeViews() {
        try {
            // Setup toolbar back button
            if (binding.backBtn != null) {
                binding.backBtn.setOnClickListener(v -> onBackPressed());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCollapsingToolbar() {
        try {
            binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isCollapsed = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int scrollRange = appBarLayout.getTotalScrollRange();
                    if (scrollRange == 0) return;

                    float percentage = Math.abs(verticalOffset / (float) scrollRange);
                    if (Float.isNaN(percentage) || Float.isInfinite(percentage)) return;

                    // Fade in/out toolbar title
                    if (binding.tvToolbarTitle != null) {
                        binding.tvToolbarTitle.setAlpha(percentage);
                    }

                    // Fade in/out expanded title
                    if (binding.tvNotificationExpanded != null) {
                        binding.tvNotificationExpanded.setAlpha(1 - percentage);
                    }

                    // Scale the background curve
                    if (binding.peachCurveBg != null) {
                        float scale = 1 - (percentage * 0.2f);
                        scale = Math.max(0.8f, Math.min(1f, scale));
                        binding.peachCurveBg.setScaleY(scale);
                    }

                    // Check if fully collapsed or expanded
                    if (Math.abs(verticalOffset) >= scrollRange) {
                        if (!isCollapsed) {
                            isCollapsed = true;
                            onToolbarCollapsed();
                        }
                    } else {
                        if (isCollapsed) {
                            isCollapsed = false;
                            onToolbarExpanded();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onToolbarCollapsed() {
        // Called when toolbar is fully collapsed
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
    }

    private void setupRecyclerView() {
        try {
            // Setup RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rcNotification.setLayoutManager(layoutManager);
            binding.rcNotification.setHasFixedSize(true);

            // Initialize adapter
            notificationAdapter = new NotificationAdapter();
            binding.rcNotification.setAdapter(notificationAdapter);

            // Setup item click listener
            notificationAdapter.setOnItemClickListener(notification -> {
                // Handle notification item click
                showToast("Notification: " + notification.getTitle());
            });

            // Setup pagination scroll listener
            binding.rcNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) { // Scrolling down
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                // Load more data
                                loadMoreNotifications();
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void observeViewModel() {
        try {
            // Observe loading state
            viewModel.getLoadingState().observe(this, isLoading -> {
                if (isLoading != null) {
                    this.isLoading = isLoading;
                    if (currentPage == 1) {
                        showLoading(isLoading);
                    } else {
                        showLoadingMore(isLoading);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        try {
            if (isLoading || isLastPage) return;

            isLoading = true;
            currentPage = 1;

            String auth = "Bearer " + getAuthToken();

            viewModel.getNotification(auth).observe(this, response -> {
                isLoading = false;

                if (response != null && response.isSuccess() && response.data != null) {
                    handleNotificationResponse(response.data, true);
                } else {
                    handleError(response);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load notifications");
            isLoading = false;
        }
    }

    private void loadMoreNotifications() {
        try {
            if (isLoading || isLastPage || nextPageUrl == null) return;

            isLoading = true;
            currentPage++;

            String auth = "Bearer " + getAuthToken();

            viewModel.getNotification(auth).observe(this, response -> {
                isLoading = false;

                if (response != null && response.isSuccess() && response.data != null) {
                    handleNotificationResponse(response.data, false);
                } else {
                    currentPage--; // Revert page increment on error
                    showError("Failed to load more notifications");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            currentPage--;
        }
    }

    private void handleNotificationResponse(NotificationResponse response, boolean isFirstPage) {
        try {
            if (response.getData() == null || response.getData().getNotifications() == null) {
                updateEmptyState(true);
                return;
            }

            NotificationResponse.Notifications notifications = response.getData().getNotifications();
            List<NotificationResponse.NotificationItem> notificationItems = notifications.getData();

            if (notificationItems != null && !notificationItems.isEmpty()) {
                if (isFirstPage) {
                    notificationAdapter.setNotifications(notificationItems);
                } else {
                    notificationAdapter.addNotifications(notificationItems);
                }

                // Update pagination info
                currentPage = notifications.getCurrent_page();
                lastPage = notifications.getLast_page();
                nextPageUrl = notifications.getNext_page_url();
                isLastPage = (currentPage >= lastPage) || (nextPageUrl == null);

                updateEmptyState(false);
            } else {
                if (isFirstPage) {
                    updateEmptyState(true);
                }
                isLastPage = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error processing notifications");
        }
    }

    private void handleError(Repository.ApiResponse<NotificationResponse> response) {
        try {
            if (response != null && response.code == Repository.ERROR_SESSION_EXPIRED) {
                handleSessionExpired();
            } else {
                String errorMessage = response != null && response.message != null
                        ? response.message
                        : "Failed to load notifications";
                showError(errorMessage);

                if (currentPage == 1) {
                    updateEmptyState(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSessionExpired() {
        showError("Session expired. Please login again.");
        // Clear token and redirect to login
        pref.setPrefString(this,pref.user_token,"");
        // Navigate to login activity
         startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void updateEmptyState(boolean isEmpty) {
        try {
            if (binding.emptyStateLayout != null) {
                binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }

            if (binding.rcNotification != null) {
                binding.rcNotification.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoading(boolean show) {
        try {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            if (binding.rcNotification != null) {
                binding.rcNotification.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoadingMore(boolean show) {
        try {
            // You can add a footer loading indicator here if needed
            // For now, we'll just use the progress bar if it's the first page
            if (currentPage == 1 && binding.progressBar != null) {
                binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (binding != null) {
            binding = null;
        }
        notificationAdapter = null;
    }
    private  String getAuthToken(){
        return pref.getPrefString(this,pref.user_token);
    }
}
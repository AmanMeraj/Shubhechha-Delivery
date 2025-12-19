package com.subh.shubhechhadelivery.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.subh.shubhechhadelivery.Activities.OrderDetailsActivity;
import com.subh.shubhechhadelivery.Adapter.OrdersAdapter;
import com.subh.shubhechhadelivery.Adapter.WorkPlaceAdapter;
import com.subh.shubhechhadelivery.Model.HomeResponse;
import com.subh.shubhechhadelivery.Model.UpdateStatusModel;
import com.subh.shubhechhadelivery.Model.WorkPlace;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.FragmentHomeBinding;
import com.subh.shubhechhadelivery.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private WorkPlaceAdapter workPlaceAdapter;
    private OrdersAdapter ordersAdapter;
    private ViewModel viewModel;
    private SharedPref sharedPref;
    private String authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initializeComponents();
        setupRecyclerViews();
        setupClickListeners();
        observeViewModel();
        loadHomeData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload orders when fragment is resumed
        loadHomeData();
        Log.d(TAG, "onResume: Reloading home data");
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        sharedPref = new SharedPref();

        // Get auth token from SharedPreferences
        String token = sharedPref.getPrefString(requireContext(), sharedPref.user_token);
        authToken = token != null ? "Bearer " + token : null;
    }

    private void setupRecyclerViews() {
        // Setup Work Place RecyclerView (Horizontal)
        LinearLayoutManager workPlaceLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        binding.workPlaceRecyclerView.setLayoutManager(workPlaceLayoutManager);
        binding.workPlaceRecyclerView.setHasFixedSize(true);
        workPlaceAdapter = new WorkPlaceAdapter(getContext(), new ArrayList<>());
        binding.workPlaceRecyclerView.setAdapter(workPlaceAdapter);

        // Setup Orders RecyclerView (Vertical)
        LinearLayoutManager ordersLayoutManager = new LinearLayoutManager(getContext());
        binding.newOrdersRecyclerView.setLayoutManager(ordersLayoutManager);
        binding.newOrdersRecyclerView.setHasFixedSize(true);
        ordersAdapter = new OrdersAdapter(getContext(), new ArrayList<>());
        binding.newOrdersRecyclerView.setAdapter(ordersAdapter);
    }

    private void setupClickListeners() {
        // Work Place item click
        workPlaceAdapter.setOnWorkPlaceClickListener(workPlace ->
                Toast.makeText(getContext(),
                        "Selected: " + workPlace.getStoreName(),
                        Toast.LENGTH_SHORT).show()
        );

        // Order item click - Navigate to details
        ordersAdapter.setOnOrderClickListener((order, position) -> {
            Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
            intent.putExtra("fromPage", "homepage");
            intent.putExtra("orderId", order.getId());
            intent.putExtra("order_id", order.getId());
            intent.putExtra("order_no", order.getOrderno());
            intent.putExtra("orderNo", order.getOrderno());
            intent.putExtra("finalTotal", order.getTotal());
            intent.putExtra("status", order.getStatus());
            intent.putExtra("createdAt", order.getCreated_at());
            startActivity(intent);
        });

        // Order action listeners for Accept/Reject
        ordersAdapter.setOnOrderActionListener(new OrdersAdapter.OnOrderActionListener() {
            @Override
            public void onAcceptOrder(HomeResponse.Order order, int position) {
                updateOrderStatus(order, "accept", position);
            }

            @Override
            public void onRejectOrder(HomeResponse.Order order, int position) {
                updateOrderStatus(order, "reject", position);
            }
        });
    }

    private void observeViewModel() {
        // Observe loading state
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadHomeData() {
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
            // Navigate to login if needed
            return;
        }

        viewModel.home(authToken).observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccess() && response.data != null) {
                    handleHomeResponse(response.data);
                } else {
                    handleError(response.message, response.code);
                }
            }
        });
    }

    private void handleHomeResponse(HomeResponse homeResponse) {
        try {
            HomeResponse.Data data = homeResponse.getData();

            if (data != null) {
                // Update Today's Shops
                List<HomeResponse.TodaysShop> shopList = data.getTodays_shop();
                if (shopList != null && !shopList.isEmpty()) {
                    List<WorkPlace> workPlaces = convertToWorkPlaceList(shopList);
                    workPlaceAdapter.updateList(workPlaces);
                    binding.workPlaceRecyclerView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Loaded " + shopList.size() + " shops");
                } else {
                    binding.workPlaceRecyclerView.setVisibility(View.GONE);
                    Log.d(TAG, "No shops available");
                }

                // Update Orders
                List<HomeResponse.Order> orderList = data.getOrders();
                if (orderList != null && !orderList.isEmpty()) {
                    ordersAdapter.updateList(orderList);
                    binding.newOrdersRecyclerView.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Loaded " + orderList.size() + " orders");
                } else {
                    ordersAdapter.updateList(new ArrayList<>());
                    binding.newOrdersRecyclerView.setVisibility(View.GONE);
                    Log.d(TAG, "No orders available");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling home response: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update order status (accept/reject)
     */
    private void updateOrderStatus(HomeResponse.Order order, String status, int position) {
        if (authToken == null) {
            Toast.makeText(getContext(), "Authentication token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create UpdateStatusModel with order ID and status
        UpdateStatusModel updateStatusModel = new UpdateStatusModel();
        updateStatusModel.setOrder_id(String.valueOf(order.getId()));
        updateStatusModel.setStatus(status);

        Log.d(TAG, "Updating order status: " + order.getOrderno() + " to " + status);

        // Call API through ViewModel
        viewModel.updateStatus(authToken, updateStatusModel)
                .observe(getViewLifecycleOwner(), apiResponse -> {
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess() && apiResponse.data != null) {
                            handleStatusUpdateSuccess(apiResponse, status, position);
                        } else {
                            handleStatusUpdateError(apiResponse);
                        }
                    }
                });
    }

    /**
     * Handle successful status update
     */
    private void handleStatusUpdateSuccess(Repository.ApiResponse<?> apiResponse, String status, int position) {
        String successMessage = "Order " + status + "ed successfully";

        // Try to get message from response if available
        try {
            if (apiResponse.data != null) {
                // Assuming GenericPostResponse has getMessage() method
                // successMessage = apiResponse.data.getMessage();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting success message: " + e.getMessage());
        }

        Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Status update success: " + successMessage);

        // Remove item if rejected, reload if accepted
        if ("reject".equalsIgnoreCase(status)) {
            ordersAdapter.removeItem(position);
        } else {
            // Reload the entire list to get updated data
            loadHomeData();
        }
    }

    /**
     * Handle status update error
     */
    private void handleStatusUpdateError(Repository.ApiResponse<?> apiResponse) {
        String errorMessage = apiResponse.message != null ?
                apiResponse.message : "Failed to update order status";

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Order status error: " + errorMessage + " (Code: " + apiResponse.code + ")");

        if (apiResponse.code == Repository.ERROR_SESSION_EXPIRED) {
            // Handle session expired
            Toast.makeText(getContext(),
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG).show();

            // Clear user data and navigate to login
            sharedPref.clearAll(requireContext());
            // Navigate to login activity
            // Intent intent = new Intent(getContext(), LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
        }
    }

    /**
     * Convert HomeResponse.TodaysShop list to WorkPlace list
     */
    private List<WorkPlace> convertToWorkPlaceList(List<HomeResponse.TodaysShop> todaysShops) {
        List<WorkPlace> workPlaces = new ArrayList<>();

        if (todaysShops != null) {
            for (HomeResponse.TodaysShop shop : todaysShops) {
                WorkPlace workPlace = new WorkPlace(
                        String.valueOf(shop.getId()),
                        shop.getName(),
                        shop.getImage_path() != null ? shop.getImage_path() : "",
                        shop.getAddress() != null ? shop.getAddress() : ""
                );
                workPlaces.add(workPlace);
            }
        }

        return workPlaces;
    }

    private void handleError(String message, int code) {
        Log.e(TAG, "API Error: " + message + " (Code: " + code + ")");

        // Handle session expired
        if (code == Repository.ERROR_SESSION_EXPIRED) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            // Clear user data and navigate to login
            sharedPref.clearAll(requireContext());
            // Navigate to login activity
            // Intent intent = new Intent(getContext(), LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // startActivity(intent);
            return;
        }

        // Handle other errors
        String errorMessage = message != null && !message.isEmpty()
                ? message
                : "Failed to load data. Please try again.";
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
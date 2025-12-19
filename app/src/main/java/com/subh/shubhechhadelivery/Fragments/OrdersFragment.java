package com.subh.shubhechhadelivery.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.subh.shubhechhadelivery.Activities.OrderDetailsActivity;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.Adapter.MyOrdersAdapter;
import com.subh.shubhechhadelivery.Model.MyOrdersResponse;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.FragmentOrdersBinding;
import com.subh.shubhechhadelivery.utils.SharedPref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrdersFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "OrdersFragment";

    private FragmentOrdersBinding binding;
    private MyOrdersAdapter myOrdersAdapter;
    private ViewModel viewModel;
    private SharedPref pref;
    private String authToken;
    private String selectedFilterDate = "";
    private String totalAmount = "0.00";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        loadOrders("");
    }

    private void initViews() {
        pref = new SharedPref();
        authToken = pref.getPrefString(requireContext(), pref.user_token);

        // Set click listeners
        binding.llOne.setOnClickListener(this);

        // Initialize total price if you have it in layout
        if (binding.tvPrice != null) {
            binding.tvPrice.setText("₹ 0.00");
        }
    }

    private void setupRecyclerView() {
        binding.ordersRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );
        binding.ordersRecyclerView.setHasFixedSize(true);

        // Create adapter with empty list initially
        myOrdersAdapter = new MyOrdersAdapter(requireContext(), new ArrayList<>());

        // Set click listener for order items
        myOrdersAdapter.setOnOrderClickListener((order, position) -> {
            Intent orderDetailsIntent = new Intent(requireContext(), OrderDetailsActivity.class);
            orderDetailsIntent.putExtra("fromPage", "delivery");
            orderDetailsIntent.putExtra("order_id", order.getId());
            Log.d(TAG, "setupRecyclerView: ORDER ID....." + order.getId());
            orderDetailsIntent.putExtra("orderNo", order.getOrderno());
            orderDetailsIntent.putExtra("finalTotal", order.getTotal());
            orderDetailsIntent.putExtra("status", order.getStatus());
            orderDetailsIntent.putExtra("createdAt", order.getCreated_at());
            startActivity(orderDetailsIntent);
        });

        binding.ordersRecyclerView.setAdapter(myOrdersAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void observeViewModel() {
        // Observe loading state from ViewModel
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadOrders(String filterDate) {
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(requireContext(), "Authentication required", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.getMyOrders("Bearer " + authToken, filterDate)
                .observe(getViewLifecycleOwner(), apiResponse -> {
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess && apiResponse.data != null) {
                            MyOrdersResponse response = apiResponse.data;

                            if (response.getData() != null && response.getData().getOrders() != null) {
                                MyOrdersResponse.Orders orders = response.getData().getOrders();

                                // Update total amount
                                totalAmount = response.getData().getTotal_amount() != null ?
                                        response.getData().getTotal_amount() : "0.00";

                                if (binding.tvPrice != null) {
                                    binding.tvPrice.setText("₹ " + totalAmount);
                                }

                                // Update orders list
                                if (orders.getData() != null && !orders.getData().isEmpty()) {
                                    myOrdersAdapter.updateList(orders.getData());
                                    Log.d(TAG, "Orders loaded: " + orders.getData().size());
                                } else {
                                    myOrdersAdapter.updateList(new ArrayList<>());
                                    Toast.makeText(requireContext(), "No orders found for selected date", Toast.LENGTH_SHORT).show();
                                    if (binding.tvPrice != null) {
                                        binding.tvPrice.setText("₹ 0.00");
                                    }
                                    Log.d(TAG, "No orders available");
                                }
                            } else {
                                myOrdersAdapter.updateList(new ArrayList<>());
                                if (binding.tvPrice != null) {
                                    binding.tvPrice.setText("₹ 0.00");
                                }
                                Toast.makeText(requireContext(), "No orders data available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            handleError(apiResponse);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Network error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleError(Repository.ApiResponse<MyOrdersResponse> apiResponse) {
        if (apiResponse.code == Repository.ERROR_SESSION_EXPIRED) {
            Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
        } else {
            String errorMessage = apiResponse.message != null ?
                    apiResponse.message : "Failed to load orders";
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + errorMessage);
        }

        // Clear list on error
        myOrdersAdapter.updateList(new ArrayList<>());
        if (binding.tvPrice != null) {
            binding.tvPrice.setText("₹ 0.00");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.ll_one) {
            showDatePicker();
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Create calendar with selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format date for API (yyyy-MM-dd)
                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    selectedFilterDate = apiFormat.format(selectedCalendar.getTime());

                    // Format date for display (dd MMM yyyy)
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                    String displayDate = displayFormat.format(selectedCalendar.getTime());

                    // Update UI
                    binding.selectDateFilter.setText(displayDate);

                    Log.d(TAG, "Date selected: " + selectedFilterDate);

                    // Load orders with selected date
                    loadOrders(selectedFilterDate);
                },
                year, month, day
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload orders when fragment is resumed
        loadOrders(selectedFilterDate);
        Log.d(TAG, "onResume: Reloading orders");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
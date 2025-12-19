package com.subh.shubhechhadelivery.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.subh.shubhechhadelivery.Adapter.OrderItemsAdapter;
import com.subh.shubhechhadelivery.Model.GenericPostResponse;
import com.subh.shubhechhadelivery.Model.OrderDetails;
import com.subh.shubhechhadelivery.Model.OrderItem;
import com.subh.shubhechhadelivery.Model.RefundAmount;
import com.subh.shubhechhadelivery.Model.UpdateStatusModel;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.ActivityOrderDetailsBinding;
import com.subh.shubhechhadelivery.utils.Utility;

import java.util.ArrayList;

public class OrderDetailsActivity extends Utility {
    private static final String TAG = "OrderDetailsActivity";

    private ActivityOrderDetailsBinding binding;
    private ViewModel viewModel;
    private OrderItemsAdapter adapter;
    private ArrayList<OrderItem> orderItemsList;
    private OrderDetails.Order orderData;

    private String customerPhone = "";
    private String shopPhone = "";
    private String customerName = "";
    private String shopName = "";
    private String deliveryAddress = "";
    private String orderNo = "";
    private String otp = "";
    private String customerImage = "";
    private String shopImage = "";
    private String paymentMethod = "";

    private double subtotal = 0.0;
    private double tax = 0.0;
    private double deliveryCharge = 0.0;
    private double packagingCharge = 0.0;
    private double discountAmount = 0.0;
    private double totalAmount = 0.0;

    private int orderId = -1;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        getOrderIdFromIntent();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

        if (orderId != -1 && authToken != null && !authToken.isEmpty()) {
            fetchOrderDetails();
        } else {
            Toast.makeText(this, "Invalid Order ID or Authentication required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        orderItemsList = new ArrayList<>();

        // Get auth token from SharedPreferences using pref utility
        authToken = getAuthToken();
    }

    private String getAuthToken() {
        String token = pref.getPrefString(this, pref.user_token);
        if (token != null && !token.isEmpty()) {
            return "Bearer " + token;
        }
        return "";
    }

    private void getOrderIdFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getIntExtra("order_id", -1);
            Log.d(TAG, "Order ID: " + orderId);
        }
    }

    private void setupRecyclerView() {
        adapter = new OrderItemsAdapter(this, orderItemsList);
        binding.rcItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rcItems.setAdapter(adapter);
    }

    /**
     * Observe ViewModel loading state
     */
    private void observeViewModel() {
        viewModel.getLoadingState().observe(this, isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

                // Only disable/enable buttons if they are visible
                if (binding.orderDeliveredButton.getVisibility() == View.VISIBLE) {
                    binding.orderDeliveredButton.setEnabled(!isLoading);
                }

                if (binding.sendButton.getVisibility() == View.VISIBLE) {
                    binding.sendButton.setEnabled(!isLoading);
                }
            }
        });
    }

    private void fetchOrderDetails() {
        viewModel.orderDetails(authToken, orderId).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess() && response.data != null) {
                    handleSuccessResponse(response.data);
                } else {
                    handleErrorResponse(response);
                }
            } else {
                Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessResponse(OrderDetails orderDetails) {
        if (orderDetails.getData() != null && orderDetails.getData().getOrder() != null) {
            orderData = orderDetails.getData().getOrder();
            extractOrderData();
            populateUI();
            populateOrderItems();
        } else {
            Toast.makeText(this, "No order data available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleErrorResponse(Repository.ApiResponse<OrderDetails> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            handleSessionExpired();
        } else {
            String errorMsg = response.message != null ? response.message : "Failed to load order details";
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: " + errorMsg + ", Code: " + response.code);
        }
    }

    private void extractOrderData() {
        // Extract customer details
        customerName = orderData.getReceipent_name() != null ? orderData.getReceipent_name() : "N/A";
        customerPhone = orderData.getUser_mobile() != null ? orderData.getUser_mobile() : "";
        customerImage = orderData.getUser_image() != null ? orderData.getUser_image() : "";

        // Extract shop details
        shopName = orderData.getShop_name() != null ? orderData.getShop_name() : "N/A";
        shopPhone = orderData.getShop_mobile() != null ? orderData.getShop_mobile() : "";
        shopImage = orderData.getShop_image() != null ? orderData.getShop_image() : "";

        // Extract address
        String address = orderData.getAddress() != null ? orderData.getAddress() : "";
        String pincode = orderData.getPincode() != null ? orderData.getPincode() : "";
        deliveryAddress = address + (pincode.isEmpty() ? "" : ", " + pincode);

        // Extract order details
        orderNo = orderData.getOrderno() != null ? orderData.getOrderno() : "N/A";
        otp = orderData.getOtp() != null ? orderData.getOtp() : "";

        // Extract payment method
        paymentMethod = orderData.getOrder_type() != null ? orderData.getOrder_type().toLowerCase() : "";
        Log.d(TAG, "extractOrderData: Payment Method = " + paymentMethod);

        // Extract amounts
        try {
            subtotal = orderData.getSubtotal() != null ? Double.parseDouble(orderData.getSubtotal()) : 0.0;
            tax = orderData.getTax() != null ? Double.parseDouble(orderData.getTax()) : 0.0;
            deliveryCharge = orderData.getDelivery_charge() != null ? Double.parseDouble(orderData.getDelivery_charge()) : 0.0;
            packagingCharge = orderData.getPackaging_charge() != null ? Double.parseDouble(orderData.getPackaging_charge()) : 0.0;
            discountAmount = orderData.getDiscount_amount() != null ? Double.parseDouble(orderData.getDiscount_amount()) : 0.0;
            totalAmount = orderData.getTotal() != null ? Double.parseDouble(orderData.getTotal()) : 0.0;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing amounts: " + e.getMessage());
            totalAmount = 0.0;
        }
    }

    private void populateUI() {
        // Populate customer details
        binding.customerName.setText(customerName);
        binding.customerPhone.setText(customerPhone);

        // Load customer image using Glide
        if (!TextUtils.isEmpty(customerImage)) {
            Glide.with(this)
                    .load(customerImage)
                    .placeholder(R.drawable.dummy_profile)
                    .error(R.drawable.dummy_profile)
                    .circleCrop()
                    .into(binding.customerProfileImage);
        } else {
            binding.customerProfileImage.setImageResource(R.drawable.dummy_profile);
        }

        // Populate shop details
        binding.shopName.setText(shopName);
        binding.shopPhone.setText(shopPhone);

        // Load shop image using Glide
        if (!TextUtils.isEmpty(shopImage)) {
            Glide.with(this)
                    .load(shopImage)
                    .placeholder(R.drawable.subh_img2)
                    .error(R.drawable.subh_img2)
                    .circleCrop()
                    .into(binding.shopProfileImage);
        } else {
            binding.shopProfileImage.setImageResource(R.drawable.subh_img2);
        }

        // Check if order is delivered
        boolean isDelivered = orderData.getStatus() != null &&
                orderData.getStatus().equalsIgnoreCase("delivered");

        // Hide button and wallet section if order is delivered
        if (isDelivered) {
            binding.orderDeliveredButton.setVisibility(View.GONE);
            binding.tvWalletHeading.setVisibility(View.GONE);
            binding.llWallet.setVisibility(View.GONE);
            binding.shopCallButton.setVisibility(View.GONE);
            binding.customerCallButton.setVisibility(View.GONE);
            binding.viewMapButton.setVisibility(View.GONE);
        } else {
            binding.orderDeliveredButton.setVisibility(View.VISIBLE);
            binding.shopCallButton.setVisibility(View.VISIBLE);
            binding.customerCallButton.setVisibility(View.VISIBLE);
            binding.viewMapButton.setVisibility(View.VISIBLE);
            // Handle wallet visibility based on payment method only if not delivered
            handleWalletVisibility();
        }

        // Populate delivery address
        binding.deliveryAddress.setText(deliveryAddress);

        // Populate order ID
        binding.orderId.setText(orderNo);
        binding.tvPaymentMethod.setText(orderData.getOrder_type());

        // Populate total amount
        binding.tvTotalAmount.setText("₹" + String.format("%.2f", totalAmount));
    }

    private void handleWalletVisibility() {
        // Check if payment method is COD (Cash on Delivery)
        boolean isCOD = paymentMethod.equalsIgnoreCase("cod") ||
                paymentMethod.equalsIgnoreCase("cash on delivery") ||
                paymentMethod.contains("cash") ||
                paymentMethod.contains("cod");

        if (isCOD) {
            // Show wallet transfer section for COD orders
            binding.tvWalletHeading.setVisibility(View.VISIBLE);
            binding.llWallet.setVisibility(View.VISIBLE);
        } else {
            // Hide wallet transfer section for other payment methods
            binding.tvWalletHeading.setVisibility(View.GONE);
            binding.llWallet.setVisibility(View.GONE);
        }

        Log.d(TAG, "Payment Method: " + paymentMethod + ", Wallet visible: " + isCOD);
    }

    private void populateOrderItems() {
        orderItemsList.clear();

        if (orderData.getOrdritems() != null && !orderData.getOrdritems().isEmpty()) {
            for (OrderDetails.Ordritem item : orderData.getOrdritems()) {
                String itemName = item.getName() != null ? item.getName() : "Unknown Item";
                String itemAmount = item.getAmount() != null ? item.getAmount() : "0";
                int quantity = item.getQuantity();

                orderItemsList.add(new OrderItem(itemName, itemAmount, quantity));
            }

            adapter.notifyDataSetChanged();
        }
    }

    private void setupClickListeners() {
        // Customer call button
        binding.customerCallButton.setOnClickListener(v -> makePhoneCall(customerPhone));

        // Shop call button
        binding.shopCallButton.setOnClickListener(v -> makePhoneCall(shopPhone));

        // View on map button
        binding.viewMapButton.setOnClickListener(v -> openMapLocation());

        // Send money button
        binding.sendButton.setOnClickListener(v -> sendMoneyToWallet());

        // Order delivered button
        binding.orderDeliveredButton.setOnClickListener(v -> updateOrderStatus("delivered"));
    }

    private void makePhoneCall(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void openMapLocation() {
        if (TextUtils.isEmpty(deliveryAddress)) {
            Toast.makeText(this, "Address not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(deliveryAddress));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMoneyToWallet() {
        String amountStr = binding.amountInput.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount > totalAmount) {
                Toast.makeText(this, "Amount cannot exceed total order amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the return change API
            transferBalanceToWallet(amountStr);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Transfer balance to customer wallet using returnChange API
     */
    private void transferBalanceToWallet(String amount) {
        // Create RefundAmount object
        RefundAmount refundAmount = new RefundAmount();
        refundAmount.setOrder_id(String.valueOf(orderId));
        refundAmount.setAmount(amount);

        Log.d(TAG, "Transferring ₹" + amount + " to wallet for Order #" + orderId);

        // Call API through ViewModel (loader will be shown via observeViewModel)
        viewModel.returnChange(authToken, refundAmount).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess() && response.data != null) {
                    handleWalletTransferSuccess(response, amount);
                } else {
                    handleWalletTransferError(response);
                }
            } else {
                Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle successful wallet transfer
     */
    private void handleWalletTransferSuccess(Repository.ApiResponse<?> response, String amount) {
        String successMessage = "₹" + amount + " transferred to customer wallet successfully";

        Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Wallet transfer successful for Order #" + orderId + ", Amount: ₹" + amount);

        // Clear the amount input field
        binding.amountInput.setText("");
    }

    /**
     * Handle wallet transfer error
     */
    private void handleWalletTransferError(Repository.ApiResponse<?> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            handleSessionExpired();
        } else {
            String errorMessage = response.message != null ?
                    response.message : "Failed to transfer amount to wallet";

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Wallet transfer failed for Order #" + orderId + " - " + errorMessage + ", Code: " + response.code);
        }
    }

    /**
     * Update order status to 'delivered'
     */
    private void updateOrderStatus(String status) {
        if (authToken == null || authToken.isEmpty()) {
            Toast.makeText(this, "Authentication token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create UpdateStatusModel with order ID and status
        UpdateStatusModel updateStatusModel = new UpdateStatusModel();
        updateStatusModel.setOrder_id(String.valueOf(orderId));
        updateStatusModel.setStatus(status);

        Log.d(TAG, "Updating order status: Order #" + orderId + " to " + status);

        // Call API through ViewModel (loader will be shown via observeViewModel)
        viewModel.updateStatus(authToken, updateStatusModel)
                .observe(this, response -> {
                    if (response != null) {
                        if (response.isSuccess() && response.data != null) {
                            handleStatusUpdateSuccess(response, status);
                        } else {
                            handleStatusUpdateError(response);
                        }
                    } else {
                        Toast.makeText(this, "Network error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Handle successful status update
     */
    private void handleStatusUpdateSuccess(Repository.ApiResponse<GenericPostResponse> response, String status) {
        String successMessage = "Order marked as " + status + " successfully";

        // Try to get message from response if available
        try {
            if (response.data != null && response.data.getMessage() != null) {
                successMessage = response.data.getMessage();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting success message: " + e.getMessage());
        }

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Order #" + orderId + " status updated to: " + status);

        // Clear the code input field
        binding.codeInput.setText("");

        // Finish activity and return to previous screen
        finish();
    }

    /**
     * Handle status update error
     */
    private void handleStatusUpdateError(Repository.ApiResponse<?> response) {
        String errorMessage = response.message != null ?
                response.message : "Failed to update order status";

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Order status error: " + errorMessage + " (Code: " + response.code + ")");

        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            handleSessionExpired();
        }
    }

    /**
     * Handle session expired scenario
     */
    private void handleSessionExpired() {
        // Clear user data
        pref.clearAll(this);

        // Navigate to login activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
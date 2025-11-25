package com.subh.shubhechhadelivery.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.subh.shubhechhadelivery.Adapter.OrderItemsAdapter;
import com.subh.shubhechhadelivery.Model.OrderItem;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.databinding.ActivityOrderDetailsBinding;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private ActivityOrderDetailsBinding binding;
    private OrderItemsAdapter adapter;
    private ArrayList<OrderItem> orderItemsList;
    private String customerPhone = "";
    private String shopPhone = "";
    private double totalAmount = 0.0;

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

        initializeData();
        setupRecyclerView();
        setupClickListeners();
        displayOrderDetails();
    }

    private void initializeData() {
        // Initialize order items list
        orderItemsList = new ArrayList<>();

        // TODO: Get data from Intent or API
        // For now, using dummy data
        orderItemsList.add(new OrderItem("Chicken Biryani", "250", 2));
        orderItemsList.add(new OrderItem("Paneer Tikka", "180", 1));
        orderItemsList.add(new OrderItem("Garlic Naan", "40", 3));
        orderItemsList.add(new OrderItem("Raita", "50", 2));

        // Calculate total
        calculateTotal();
    }

    private void setupRecyclerView() {
        adapter = new OrderItemsAdapter(this, orderItemsList);
        binding.rcItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rcItems.setAdapter(adapter);
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
        binding.orderDeliveredButton.setOnClickListener(v -> markOrderDelivered());
    }

    private void displayOrderDetails() {
        // TODO: Set actual data from Intent or API
        // Customer details
        binding.customerName.setText("Nilima Sharma");
        customerPhone = "+912569546950";
        binding.customerPhone.setText(customerPhone);

        // Shop details
        binding.shopName.setText("Shop Name");
        shopPhone = "+912569546950";
        binding.shopPhone.setText(shopPhone);

        // Delivery address
        binding.deliveryAddress.setText("Mondol gathi road, biswas para, koikhali, west bengal, 700052.");

        // Order ID
        binding.orderId.setText("#245698569");

        // Total amount
        binding.tvTotalAmount.setText("₹" + String.format("%.2f", totalAmount));
    }

    private void calculateTotal() {
        totalAmount = 0.0;
        for (OrderItem item : orderItemsList) {
            try {
                double amount = Double.parseDouble(item.getItemAmount());
                totalAmount += amount * item.getQuantity();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
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
        // TODO: Get actual latitude and longitude
        // For now, using a generic map intent with the address
        String address = binding.deliveryAddress.getText().toString();

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
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

            // TODO: Implement actual wallet transfer logic
            Toast.makeText(this, "Transferring ₹" + amount + " to wallet...", Toast.LENGTH_SHORT).show();
            binding.amountInput.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void markOrderDelivered() {
        String code = binding.codeInput.getText().toString().trim();

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Please enter delivery code", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Verify code with backend and mark order as delivered
        // For now, just showing a success message
        Toast.makeText(this, "Verifying delivery code...", Toast.LENGTH_SHORT).show();

        // Simulate successful delivery
        // In actual implementation, this should be done after backend verification
        // finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
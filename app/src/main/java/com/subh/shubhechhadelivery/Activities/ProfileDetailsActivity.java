package com.subh.shubhechhadelivery.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.databinding.ActivityProfileDetailsBinding;
import com.subh.shubhechhadelivery.utils.Utility;

public class ProfileDetailsActivity extends Utility implements View.OnClickListener {

    private ActivityProfileDetailsBinding binding;

    // Data from intent
    private String shopName;
    private String shopMobile;
    private String shopAddress;
    private String shopImagePath;
    private String userEmail;
    private String userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize View Binding
        binding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        getIntentData();
        populateData();
    }

    private void initializeComponents() {
        binding.toolbar.backBtn.setOnClickListener(this);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            shopName = getIntent().getStringExtra("shop_name");
            shopMobile = getIntent().getStringExtra("shop_mobile");
            shopAddress = getIntent().getStringExtra("shop_address");
            shopImagePath = getIntent().getStringExtra("shop_image_path");
            userEmail = getIntent().getStringExtra("user_email");
            userAddress = getIntent().getStringExtra("user_address");
        }
    }

    private void populateData() {
        // Set shop name
        if (shopName != null && !shopName.isEmpty()) {
            binding.etName.setText(shopName);
        } else {
            binding.etName.setText("Shop Name");
        }

        // Set shop mobile
        if (shopMobile != null && !shopMobile.isEmpty()) {
            binding.etPhone.setText(shopMobile);
        } else {
            binding.etPhone.setText("N/A");
        }

        // Set email (from user)
        if (userEmail != null && !userEmail.isEmpty()) {
            binding.etEmail.setText(userEmail);
        } else {
            binding.etEmail.setText("N/A");
        }

        // Load profile image
        if (shopImagePath != null && !shopImagePath.isEmpty()) {
            Glide.with(this)
                    .load(shopImagePath)
                    .placeholder(R.drawable.no_profile)
                    .error(R.drawable.no_profile)
                    .into(binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.no_profile);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.toolbar) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
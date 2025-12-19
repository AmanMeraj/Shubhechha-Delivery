package com.subh.shubhechhadelivery.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.subh.shubhechhadelivery.Activities.ContainerActivity;
import com.subh.shubhechhadelivery.Activities.LoginActivity;
import com.subh.shubhechhadelivery.Activities.ProfileDetailsActivity;
import com.subh.shubhechhadelivery.Model.ProfileResponse;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.FragmentProfileBinding;
import com.subh.shubhechhadelivery.utils.SharedPref;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPref pref;
    private ViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();
        setupClickListeners();
        observeViewModel();

        // Load cached data first for instant display
        loadCachedProfileData();

        // Then fetch fresh data from API
        fetchProfileData();
    }

    /**
     * Initialize SharedPref and ViewModel
     */
    private void initializeComponents() {
        pref = new SharedPref();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    /**
     * Setup all click listeners
     */
    private void setupClickListeners() {
        // My Profile - Pass data to ProfileDetailsActivity
        binding.menuMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileDetailsActivity.class);

            // Get data from SharedPreferences
            String shopName = pref.getPrefString(requireActivity(), pref.user_name);
            String shopMobile = pref.getPrefString(requireActivity(), pref.user_mobile);
            String shopAddress = pref.getPrefString(requireActivity(), pref.user_address);
            String shopImagePath = pref.getPrefString(requireActivity(), pref.user_image);
            String userEmail = pref.getPrefString(requireActivity(), pref.user_email);

            // Put extras in intent
            intent.putExtra("shop_name", shopName);
            intent.putExtra("shop_mobile", shopMobile);
            intent.putExtra("shop_address", shopAddress);
            intent.putExtra("shop_image_path", shopImagePath);
            intent.putExtra("user_email", userEmail);
            intent.putExtra("user_address", shopAddress); // Using same address

            startActivity(intent);
        });

        // My Orders
        binding.menuMyOrders.setOnClickListener(v -> {
            // Switch bottom navigation selection
            if (getActivity() instanceof ContainerActivity) {
                ((ContainerActivity) getActivity()).setBottomTab(R.id.orders);
            }

            // Load OrdersFragment
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentContainer, new OrdersFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Logout
        binding.btnLogout.setOnClickListener(v -> logoutUser());
    }

    /**
     * Observe loading state and other LiveData from ViewModel
     */
    private void observeViewModel() {
        // Observe loading state from ViewModel
        viewModel.getLoadingState().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

                // Optionally disable interactions during loading
                binding.menuMyProfile.setEnabled(!isLoading);
                binding.menuMyOrders.setEnabled(!isLoading);
                binding.btnLogout.setEnabled(!isLoading);
            }
        });
    }

    /**
     * Fetch profile data from API
     */
    private void fetchProfileData() {
        String token = pref.getPrefString(requireActivity(), pref.user_token);

        if (token == null || token.isEmpty()) {
            handleSessionExpired();
            return;
        }

        String authHeader = "Bearer " + token;

        viewModel.profile(authHeader).observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccess() && response.data != null) {
                    handleProfileSuccess(response.data);
                } else {
                    handleProfileError(response.message, response.code);
                }
            }
        });
    }

    /**
     * Handle successful profile data fetch
     */
    private void handleProfileSuccess(ProfileResponse profileResponse) {
        try {
            if (profileResponse.getData() != null &&
                    profileResponse.getData().getUser() != null) {

                ProfileResponse.User user = profileResponse.getData().getUser();
                String role = profileResponse.getData().getRole();

                // Update UI
                updateProfileUI(user);

                // Save profile data locally for offline access
                saveProfileDataLocally(user, role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error displaying profile data");
        }
    }

    /**
     * Update profile UI with user data
     */
    private void updateProfileUI(ProfileResponse.User user) {
        if (binding == null) return;

        // Set user name
        if (user.getName() != null && !user.getName().isEmpty()) {
            binding.profileName.setText(user.getName());
        } else {
            binding.profileName.setText("User");
        }

        // Set phone number
        if (user.getMobile() != null && !user.getMobile().isEmpty()) {
            binding.profilePhone.setText(user.getMobile());
        } else {
            binding.profilePhone.setText("");
        }

        // Load profile image using Glide
        if (user.getImage_path() != null && !user.getImage_path().isEmpty()) {
            Glide.with(requireContext())
                    .load(user.getImage_path())
                    .placeholder(R.drawable.cart_profile)
                    .error(R.drawable.cart_profile)
                    .circleCrop()
                    .into(binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.cart_profile);
        }
    }

    /**
     * Handle profile data fetch error
     */
    private void handleProfileError(String message, int code) {
        if (code == Repository.ERROR_SESSION_EXPIRED) {
            handleSessionExpired();
        } else {
            // Show error message
            if (message != null && !message.isEmpty()) {
                showToast(message);
            } else {
                showToast("Failed to load profile");
            }
        }
    }

    /**
     * Save profile data locally for offline access using existing SharedPref structure
     */
    private void saveProfileDataLocally(ProfileResponse.User user, String role) {
        if (user != null && getContext() != null) {
            // Save user ID
            pref.setPrefInteger(requireActivity(), pref.user_id, user.getId());

            // Save user name
            if (user.getName() != null) {
                pref.setPrefString(requireActivity(), pref.user_name, user.getName());
            }

            // Save mobile
            if (user.getMobile() != null) {
                pref.setPrefString(requireActivity(), pref.user_mobile, user.getMobile());
            }

            // Save email
            if (user.getEmail() != null) {
                pref.setPrefString(requireActivity(), pref.user_email, user.getEmail());
            }

            // Save address
            if (user.getAddress() != null) {
                pref.setPrefString(requireActivity(), pref.user_address, user.getAddress());
            }

            // Save image path
            if (user.getImage_path() != null) {
                pref.setPrefString(requireActivity(), pref.user_image, user.getImage_path());
            }

            // Save role/user type
            if (role != null) {
                pref.setPrefString(requireActivity(), pref.user_type, role);
            }
        }
    }

    /**
     * Load cached profile data from SharedPreferences
     */
    private void loadCachedProfileData() {
        if (binding == null || getContext() == null) return;

        String cachedName = pref.getPrefString(requireActivity(), pref.user_name);
        String cachedPhone = pref.getPrefString(requireActivity(), pref.user_mobile);
        String cachedImage = pref.getPrefString(requireActivity(), pref.user_image);

        // Set name
        if (cachedName != null && !cachedName.isEmpty()) {
            binding.profileName.setText(cachedName);
        } else {
            binding.profileName.setText("Loading...");
        }

        // Set phone
        if (cachedPhone != null && !cachedPhone.isEmpty()) {
            binding.profilePhone.setText(cachedPhone);
        } else {
            binding.profilePhone.setText("");
        }

        // Load image
        if (cachedImage != null && !cachedImage.isEmpty()) {
            Glide.with(requireContext())
                    .load(cachedImage)
                    .placeholder(R.drawable.cart_profile)
                    .error(R.drawable.cart_profile)
                    .circleCrop()
                    .into(binding.profileImage);
        } else {
            binding.profileImage.setImageResource(R.drawable.cart_profile);
        }
    }

    /**
     * Handle session expired scenario
     */
    private void handleSessionExpired() {
        showToast("Session expired. Please login again.");
        logoutUser();
    }

    /**
     * Logout user and clear all data
     */
    private void logoutUser() {
        pref.clearAll(requireActivity());

        // Redirect to LoginActivity
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Show toast message
     */
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh profile data when fragment becomes visible
        fetchProfileData();
    }
}
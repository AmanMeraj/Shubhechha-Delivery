package com.subh.shubhechhadelivery.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechhadelivery.Model.LoginRequest;
import com.subh.shubhechhadelivery.Model.LoginResponse;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.Repository.Repository;
import com.subh.shubhechhadelivery.ViewModel.ViewModel;
import com.subh.shubhechhadelivery.databinding.ActivityLoginBinding;
import com.subh.shubhechhadelivery.utils.Utility;

public class LoginActivity extends Utility {

    private ActivityLoginBinding binding;
    private ViewModel viewModel;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainScreen();
            return;
        }

        // Initialize binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Setup UI
        setupWindowInsets();
        initializeLoadingDialog();
        setupViews();
        setupClickListeners();
        observeLoadingState();
    }

    /**
     * Check if user is already logged in
     */
    private boolean isUserLoggedIn() {
        return pref.getPrefBoolean(this, pref.login_status);
    }

    /**
     * Setup edge-to-edge window insets with simplified keyboard handling
     */
    private void setupWindowInsets() {
        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply only system bars padding (status bar and navigation bar)
            binding.getRoot().setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        // Use SOFT_INPUT_ADJUST_PAN for better keyboard behavior with ScrollView
        // This will scroll the focused field into view automatically
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
    }

    /**
     * Setup views and input configurations
     */
    private void setupViews() {
        // Configure phone input
        binding.etPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        // Configure password input
        binding.etPass.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> validateAndProceed());
    }

    /**
     * Toggle password visibility
     */
    private void togglePasswordVisibility() {
        if (binding.etPass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Show password
            binding.etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            // Hide password
            binding.etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        // Move cursor to end
        binding.etPass.setSelection(binding.etPass.getText().length());
    }

    /**
     * Set loading state UI
     */
    private void initializeLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dailog);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void observeLoadingState() {
        // Observe loading state
        viewModel.getLoadingState().observe(this, loading -> {
            if (loading != null) {
                if (loading) {
                    if (!loadingDialog.isShowing()) {
                        loadingDialog.show();
                    }
                } else {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     * Validate input and proceed with login
     */
    private void validateAndProceed() {
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPass.getText().toString().trim();

        // Check Internet connection
        if (!isInternetConnected(this)) {
            showSnackBar("No Internet connection!", false);
            return;
        }

        // Validate phone number
        if (phone.isEmpty()) {
            showSnackBar("Please enter your phone number!", false);
            binding.etPhone.requestFocus();
            return;
        }

        if (phone.length() != 10 || !phone.matches("[0-9]+")) {
            showSnackBar("Enter a valid 10-digit phone number!", false);
            binding.etPhone.requestFocus();
            return;
        }

        // Validate password
        if (password.isEmpty()) {
            showSnackBar("Please enter your password!", false);
            binding.etPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showSnackBar("Password must be at least 6 characters!", false);
            binding.etPass.requestFocus();
            return;
        }

        // Proceed with login API call
        performLogin(phone, password);
    }

    /**
     * Call login API through ViewModel
     */
    private void performLogin(String phone, String password) {
        // Create login request object
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMobile(phone);
        loginRequest.setPassword(password);
        loginRequest.setType("deliveryboy"); // Set type as deliveryboy

        // Call API
        viewModel.loginWithPass(loginRequest).observe(this, response -> {
            if (response != null) {
                handleLoginResponse(response);
            } else {
                showSnackBar("Unexpected error occurred!", false);
            }
        });
    }

    /**
     * Handle login API response
     */
    private void handleLoginResponse(Repository.ApiResponse<LoginResponse> response) {
        if (response.isSuccess() && response.data != null) {
            LoginResponse loginResponse = response.data;

            if (loginResponse.getStatus() == 1 || loginResponse.getStatus() == 200) {
                // Success
                handleSuccessfulLogin(loginResponse);
            } else {
                // API returned error status
                showSnackBar(loginResponse.getMessage() != null ?
                        loginResponse.getMessage() : "Login failed!", false);
            }
        } else {
            // Handle error response
            handleErrorResponse(response);
        }
    }

    /**
     * Handle successful login
     */
    private void handleSuccessfulLogin(LoginResponse loginResponse) {
        // Show success message
        showSnackBar(loginResponse.getMessage() != null ?
                loginResponse.getMessage() : "Login successful!", true);

        // Save user data using SharedPref
        if (loginResponse.getData() != null) {
            LoginResponse.Data data = loginResponse.getData();

            // Save login data to SharedPreferences
            saveUserData(data);

            // Navigate to main screen
            navigateToMainScreen();
        }
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserData(LoginResponse.Data data) {
        // Save login status
        pref.setPrefBoolean(this, pref.login_status, true);

        // Save auth token
        pref.setPrefString(this, pref.user_token, data.getToken());

        // Save user details
        pref.setPrefString(this, pref.user_name, data.getName());
        pref.setPrefString(this, pref.user_mobile, data.getMobile());
        pref.setPrefString(this, pref.user_type, data.getType());

        // Save cart count
        pref.setPrefInteger(this, pref.cart_item, data.getCart_count());

        // Save new user status
        pref.setPrefInteger(this, pref.user_status, data.getIs_new());
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(Repository.ApiResponse<LoginResponse> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            showSnackBar("Session expired. Please try again!", false);
        } else if (response.message != null) {
            showSnackBar(response.message, false);
        } else {
            showSnackBar("Login failed. Please try again!", false);
        }
    }

    /**
     * Navigate to main screen
     */
    /**
     * Navigate to main screen
     */
    private void navigateToMainScreen() {
        Intent intent = new Intent(LoginActivity.this, ContainerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    /**
     * Show a custom colored Snackbar
     */
    private void showSnackBar(String message, boolean success) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        int bgColor = success ? getColor(R.color.success_green) : getColor(R.color.error_red);
        snackView.setBackgroundColor(bgColor);
        snackbar.setTextColor(getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding to prevent memory leaks
        binding = null;
    }
}
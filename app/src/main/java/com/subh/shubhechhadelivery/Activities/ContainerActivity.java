package com.subh.shubhechhadelivery.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.subh.shubhechhadelivery.Fragments.HomeFragment;
import com.subh.shubhechhadelivery.Fragments.OrdersFragment;
import com.subh.shubhechhadelivery.Fragments.ProfileFragment;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.databinding.ActivityContainerBinding;
import com.subh.shubhechhadelivery.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerActivity extends Utility {

    ActivityContainerBinding binding;
    private Fragment activeFragment = null;

    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initializePermissionLauncher();
        requestAllPermissions();

        // Notification click
        binding.notification.setOnClickListener(v -> {
            Intent intent = new Intent(ContainerActivity.this, NotificationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Load default fragment on first time launch
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.home);
            loadFragment(new HomeFragment());
        }


        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.home) {
                binding.toolbar.setVisibility(View.VISIBLE);
                loadFragment(new HomeFragment());
                return true;

            } else if (id == R.id.orders) {
                binding.toolbar.setVisibility(View.GONE);
                loadFragment(new OrdersFragment());
                return true;

            } else if (id == R.id.profile) {
                binding.toolbar.setVisibility(View.GONE);
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });

    }

    /**
     * Load fragments safely and prevent reloading same fragment
     */
    private void loadFragment(Fragment fragment) {
        if (activeFragment != null && activeFragment.getClass() == fragment.getClass())
            return;

        activeFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.contentContainer, fragment)
                .commit();
    }

    /**
     * Setup permission launcher
     */
    private void initializePermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {

                    List<String> deniedPermissions = new ArrayList<>();
                    List<String> permanentlyDeniedPermissions = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            deniedPermissions.add(entry.getKey());

                            if (!shouldShowRequestPermissionRationale(entry.getKey())) {
                                permanentlyDeniedPermissions.add(entry.getKey());
                            }
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!permanentlyDeniedPermissions.isEmpty()) {
                            showPermissionSettingsDialog();
                        } else {
                            showPermissionRationaleDialog(deniedPermissions);
                        }
                    }
                }
        );
    }

    /**
     * Request all permissions
     */
    private void requestAllPermissions() {
        List<String> permissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.POST_NOTIFICATIONS);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.READ_MEDIA_VIDEO);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            permissionLauncher.launch(permissions.toArray(new String[0]));
        }
    }

    private void showPermissionRationaleDialog(List<String> deniedPermissions) {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("Camera, Location, Storage & Notification permissions are required.")
                .setPositiveButton("Grant", (dialog, which) ->
                        permissionLauncher.launch(deniedPermissions.toArray(new String[0])))
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }

    private void showPermissionSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Permanently Denied")
                .setMessage("Please enable required permissions from Settings.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .show();
    }
    public void setBottomTab(int id) {
        binding.bottomNavigation.setSelectedItemId(id);
    }

}

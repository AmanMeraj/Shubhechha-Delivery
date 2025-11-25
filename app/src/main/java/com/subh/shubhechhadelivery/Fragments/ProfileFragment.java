package com.subh.shubhechhadelivery.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subh.shubhechhadelivery.Activities.ContainerActivity;
import com.subh.shubhechhadelivery.Activities.LoginActivity;
import com.subh.shubhechhadelivery.Activities.ProfileDetailsActivity;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---------------------------
        // CLICK LISTENERS
        // ---------------------------

        // My Profile
        binding.menuMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileDetailsActivity.class);
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
        binding.btnLogout.setOnClickListener(v -> {
            logoutUser();
        });
    }

    // ---------------------------
    // LOGOUT HANDLER
    // ---------------------------
    private void logoutUser() {

        // Clear shared prefs (if required)
        // SharedPreferences prefs = requireContext().getSharedPreferences("USER_PREF", MODE_PRIVATE);
        // prefs.edit().clear().apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}

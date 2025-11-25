package com.subh.shubhechhadelivery.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.subh.shubhechhadelivery.Activities.OrderDetailsActivity;
import com.subh.shubhechhadelivery.Adapter.NewOrdersAdapter;
import com.subh.shubhechhadelivery.Adapter.WorkPlaceAdapter;
import com.subh.shubhechhadelivery.Model.NewOrder;
import com.subh.shubhechhadelivery.Model.WorkPlace;
import com.subh.shubhechhadelivery.R;
import com.subh.shubhechhadelivery.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private WorkPlaceAdapter workPlaceAdapter;
    private NewOrdersAdapter newOrdersAdapter;
    private List<WorkPlace> workPlaceList;
    private List<NewOrder> newOrdersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initializeViews();
        setupRecyclerViews();
        loadDummyData();
        setupClickListeners();

        return binding.getRoot();
    }

    private void initializeViews() {
        workPlaceList = new ArrayList<>();
        newOrdersList = new ArrayList<>();
    }

    private void setupRecyclerViews() {
        // Setup Work Place RecyclerView (Horizontal)
        LinearLayoutManager workPlaceLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        binding.workPlaceRecyclerView.setLayoutManager(workPlaceLayoutManager);
        workPlaceAdapter = new WorkPlaceAdapter(getContext(), workPlaceList);
        binding.workPlaceRecyclerView.setAdapter(workPlaceAdapter);

        // Setup New Orders RecyclerView (Vertical)
        LinearLayoutManager ordersLayoutManager = new LinearLayoutManager(getContext());
        binding.newOrdersRecyclerView.setLayoutManager(ordersLayoutManager);
        newOrdersAdapter = new NewOrdersAdapter(getContext(), newOrdersList);
        binding.newOrdersRecyclerView.setAdapter(newOrdersAdapter);
    }

    private void loadDummyData() {
        // Show progress bar
        binding.progressBar.setVisibility(View.VISIBLE);

        // Simulate data loading with a delay
        binding.getRoot().postDelayed(() -> {
            loadWorkPlaces();
            loadNewOrders();

            // Hide progress bar
            binding.progressBar.setVisibility(View.GONE);
        }, 1000);
    }

    private void loadWorkPlaces() {
        workPlaceList.clear();

        workPlaceList.add(new WorkPlace(
                "1",
                "Green Valley Store",
                "",
                "Park Street, Kolkata"
        ));

        workPlaceList.add(new WorkPlace(
                "2",
                "Sunrise Mart",
                "",
                "Salt Lake, Kolkata"
        ));

        workPlaceList.add(new WorkPlace(
                "3",
                "City Center Shop",
                "",
                "Ballygunge, Kolkata"
        ));

        workPlaceList.add(new WorkPlace(
                "4",
                "Metro Bazaar",
                "",
                "Howrah, Kolkata"
        ));

        workPlaceList.add(new WorkPlace(
                "5",
                "Quick Stop",
                "",
                "Gariahat, Kolkata"
        ));

        workPlaceAdapter.updateList(workPlaceList);
    }

    private void loadNewOrders() {
        newOrdersList.clear();

        newOrdersList.add(new NewOrder(
                "ORD001",
                "Fresh Fruits Store",
                "Nilima Sharma",
                "Mondolganthi, Koikhali...",
                "Processing",
                ""
        ));

        newOrdersList.add(new NewOrder(
                "ORD002",
                "Grocery Junction",
                "Rajesh Kumar",
                "New Town, Action Area 1...",
                "Pending",
                ""
        ));

        newOrdersList.add(new NewOrder(
                "ORD003",
                "Daily Needs Shop",
                "Priya Das",
                "EM Bypass, Kasba...",
                "Processing",
                ""
        ));

        newOrdersList.add(new NewOrder(
                "ORD004",
                "Super Market Plus",
                "Amit Singh",
                "Jadavpur, 8B Bus Stand...",
                "Ready",
                ""
        ));

        newOrdersList.add(new NewOrder(
                "ORD005",
                "Corner Store",
                "Sneha Roy",
                "Lake Gardens, Near Park...",
                "Processing",
                ""
        ));

        newOrdersList.add(new NewOrder(
                "ORD006",
                "Value Mart",
                "Suresh Gupta",
                "Behala, Sakher Bazar...",
                "Confirmed",
                ""
        ));

        newOrdersAdapter.updateList(newOrdersList);
    }

    private void setupClickListeners() {
        // Notification icon click

        // Work Place item click
        workPlaceAdapter.setOnWorkPlaceClickListener(workPlace ->
                Toast.makeText(getContext(),
                        "Selected: " + workPlace.getStoreName(),
                        Toast.LENGTH_SHORT).show()
        );

        // New Order item click
        newOrdersAdapter.setOnOrderClickListener(new NewOrdersAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(NewOrder order) {
                Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
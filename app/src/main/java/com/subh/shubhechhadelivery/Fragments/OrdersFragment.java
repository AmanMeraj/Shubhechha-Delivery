package com.subh.shubhechhadelivery.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.subh.shubhechhadelivery.Adapter.NewOrdersAdapter;
import com.subh.shubhechhadelivery.Model.NewOrder;
import com.subh.shubhechhadelivery.databinding.FragmentOrdersBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersFragment extends Fragment implements NewOrdersAdapter.OnOrderClickListener {
    private FragmentOrdersBinding binding;
    private NewOrdersAdapter adapter;
    private List<NewOrder> orderList;
    private String selectedDate = "";
    private String selectedStore = "All Stores";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupClickListeners();
        loadDummyData();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        adapter = new NewOrdersAdapter(getContext(), orderList);
        adapter.setOnOrderClickListener(this);

        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ordersRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Date Selector Click
        binding.dateSelector.setOnClickListener(v -> showDatePicker());

        // Store Selector Click
        binding.storeSelector.setOnClickListener(v -> showStoreSelector());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    Toast.makeText(getContext(), "Date selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                    filterOrders();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showStoreSelector() {
        // Simple store selection - you can implement a proper dialog/bottom sheet
        String[] stores = {"All Stores", "Big Mart", "Fresh Food Shop", "Quick Grocery", "Daily Needs Store"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Select Store")
                .setItems(stores, (dialog, which) -> {
                    selectedStore = stores[which];
                    Toast.makeText(getContext(), "Store: " + selectedStore, Toast.LENGTH_SHORT).show();
                    filterOrders();
                })
                .show();
    }

    private void loadDummyData() {
        orderList.clear();

        // Adding 10 dummy orders
        orderList.add(new NewOrder(
                "1",
                "Big Mart",
                "John Smith",
                "123 Main Street, Downtown",
                "Processing",
                "2024-11-24"
        ));

        orderList.add(new NewOrder(
                "2",
                "Fresh Food Shop",
                "Sarah Johnson",
                "456 Oak Avenue, City Center",
                "Pending",
                "2024-11-24"
        ));

        orderList.add(new NewOrder(
                "3",
                "Quick Grocery",
                "Mike Wilson",
                "789 Pine Road, Suburb Area",
                "Processing",
                "2024-11-23"
        ));

        orderList.add(new NewOrder(
                "4",
                "Daily Needs Store",
                "Emma Davis",
                "321 Elm Street, North Side",
                "Ready",
                "2024-11-23"
        ));

        orderList.add(new NewOrder(
                "5",
                "Super Mart",
                "Robert Brown",
                "654 Maple Drive, East End",
                "Processing",
                "2024-11-24"
        ));

        orderList.add(new NewOrder(
                "6",
                "Green Valley Store",
                "Lisa Anderson",
                "987 Cedar Lane, West Park",
                "Pending",
                "2024-11-22"
        ));

        orderList.add(new NewOrder(
                "7",
                "City Supermarket",
                "David Martinez",
                "147 Birch Boulevard, Central",
                "Processing",
                "2024-11-24"
        ));

        orderList.add(new NewOrder(
                "8",
                "Corner Shop",
                "Jennifer Taylor",
                "258 Spruce Street, South Area",
                "Ready",
                "2024-11-23"
        ));

        orderList.add(new NewOrder(
                "9",
                "Family Mart",
                "Michael Lee",
                "369 Walnut Way, Hill View",
                "Processing",
                "2024-11-24"
        ));

        orderList.add(new NewOrder(
                "10",
                "Express Store",
                "Amanda White",
                "741 Ash Avenue, Lake Side",
                "Pending",
                "2024-11-22"
        ));

        adapter.updateList(orderList);
    }

    private void filterOrders() {
        // Simple filter logic - you can enhance this based on your needs
        List<NewOrder> filteredList = new ArrayList<>();

        for (NewOrder order : orderList) {
            boolean matchesDate = selectedDate.isEmpty() || order.getDate().equals(selectedDate);
            boolean matchesStore = selectedStore.equals("All Stores") || order.getStoreName().equals(selectedStore);

            if (matchesDate && matchesStore) {
                filteredList.add(order);
            }
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No orders found for selected filters", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOrderClick(NewOrder order) {
        // Handle order click - navigate to detail screen or show details
        Toast.makeText(getContext(),
                "Order: " + order.getStoreName() + "\nStatus: " + order.getStatus(),
                Toast.LENGTH_LONG).show();

        // You can navigate to detail fragment/activity here
        // Example: NavController or FragmentTransaction
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
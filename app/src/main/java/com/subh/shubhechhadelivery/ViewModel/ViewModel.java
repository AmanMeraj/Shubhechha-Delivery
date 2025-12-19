package com.subh.shubhechhadelivery.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.subh.shubhechhadelivery.Model.GenericPostResponse;
import com.subh.shubhechhadelivery.Model.HomeResponse;
import com.subh.shubhechhadelivery.Model.LoginRequest;
import com.subh.shubhechhadelivery.Model.LoginResponse;
import com.subh.shubhechhadelivery.Model.MyOrdersResponse;
import com.subh.shubhechhadelivery.Model.OrderDetails;
import com.subh.shubhechhadelivery.Model.ProfileResponse;
import com.subh.shubhechhadelivery.Model.RefundAmount;
import com.subh.shubhechhadelivery.Model.UpdateStatusModel;
import com.subh.shubhechhadelivery.Repository.Repository;

import java.util.List;

public class ViewModel extends AndroidViewModel {

    private Repository repository;


    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    /**
     * Get loading state from repository
     */
    public LiveData<Boolean> getLoadingState() {
        return repository.getLoadingState();
    }

    /**
     * Fetch profile data
     */
    public LiveData<Repository.ApiResponse<LoginResponse>> loginWithPass(LoginRequest loginRequest) {
        return repository.loginWithPass(loginRequest);
    }

    public LiveData<Repository.ApiResponse<HomeResponse>> home(String auth) {
        return repository.home(auth);
    }
    public LiveData<Repository.ApiResponse<GenericPostResponse>> updateStatus(String auth, UpdateStatusModel updateStatusModel) {
        return repository.updateStatus(auth, updateStatusModel);
    }

    public LiveData<Repository.ApiResponse<ProfileResponse>> profile(String auth) {
        return repository.profile(auth);
    }

    public LiveData<Repository.ApiResponse<OrderDetails>> orderDetails(String auth, int id) {
        return repository.orderDetails(auth, id);
    }
    public LiveData<Repository.ApiResponse<MyOrdersResponse>> getMyOrders(String auth, String filterDate) {
        return repository.getMyOrders(auth, filterDate);
    }
    public LiveData<Repository.ApiResponse<GenericPostResponse>> returnChange(String auth, RefundAmount refundAmount) {
        return repository.returnChange(auth, refundAmount);
    }


}
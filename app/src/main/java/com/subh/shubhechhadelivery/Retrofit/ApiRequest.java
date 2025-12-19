package com.subh.shubhechhadelivery.Retrofit;


import com.subh.shubhechhadelivery.Model.GenericPostResponse;
import com.subh.shubhechhadelivery.Model.HomeResponse;
import com.subh.shubhechhadelivery.Model.LoginRequest;
import com.subh.shubhechhadelivery.Model.LoginResponse;
import com.subh.shubhechhadelivery.Model.MyOrdersResponse;
import com.subh.shubhechhadelivery.Model.OrderDetails;
import com.subh.shubhechhadelivery.Model.ProfileResponse;
import com.subh.shubhechhadelivery.Model.RefundAmount;
import com.subh.shubhechhadelivery.Model.UpdateStatusModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequest {
    @Headers({"Accept: application/json"})
    @POST("login-with-password")
    Call<LoginResponse> loginWithPass(
            @Body LoginRequest loginRequest
    );

    @Headers({"Accept: application/json"})
    @GET("dashboard")
    Call<HomeResponse> home(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @POST("order-status-update")
    Call<GenericPostResponse> updateStatus(
            @Header("Authorization") String authorization,
            @Body UpdateStatusModel updateStatusModel
    );

    @Headers({"Accept: application/json"})
    @GET("my-profile")
    Call<ProfileResponse> profile(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("order/{id}")
    Call<OrderDetails> orderDetails(
            @Header("Authorization") String authorization,
            @Path("id") int orderId
    );

    @Headers({"Accept: application/json"})
    @GET("my-orders")
    Call<MyOrdersResponse> getMyOrders(
            @Header("Authorization") String authorization,
            @Query("filter_date") String filterDate
    );
    @Headers({"Accept: application/json"})
    @POST("balance-transfer-to-customer-wallet")
    Call<GenericPostResponse> returnChange(
            @Header("Authorization") String authorization,
            @Body RefundAmount refundAmount
    );

}

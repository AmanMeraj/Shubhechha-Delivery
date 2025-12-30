package com.subh.shubhechhadelivery.Repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.subh.shubhechhadelivery.Model.GenericPostResponse;
import com.subh.shubhechhadelivery.Model.HomeResponse;
import com.subh.shubhechhadelivery.Model.LoginRequest;
import com.subh.shubhechhadelivery.Model.LoginResponse;
import com.subh.shubhechhadelivery.Model.MyOrdersResponse;
import com.subh.shubhechhadelivery.Model.NotificationResponse;
import com.subh.shubhechhadelivery.Model.OrderDetails;
import com.subh.shubhechhadelivery.Model.ProfileResponse;
import com.subh.shubhechhadelivery.Model.RefundAmount;
import com.subh.shubhechhadelivery.Model.UpdateFcm;
import com.subh.shubhechhadelivery.Model.UpdateStatusModel;
import com.subh.shubhechhadelivery.Retrofit.ApiRequest;
import com.subh.shubhechhadelivery.Retrofit.RetrofitRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public static final int ERROR_SESSION_EXPIRED = 401;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public Repository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }


//    public LiveData<ApiResponse<ProfileResponse>> profile(String auth) {
//        Call<ProfileResponse> call = apiRequest.profile(auth);
//        return performRequest(call);
//    }
//
//    public LiveData<ApiResponse<ProductResponse>> getShopItems(String auth, String longitude, String latitude, String shopId, String menuId, List<String> filterBy, String sortBy) {
//        Call<ProductResponse> call = apiRequest.getShopItems(auth, longitude, latitude, shopId, menuId, filterBy, sortBy
//        );
//        return performRequest(call);
//    }
//    public LiveData<ApiResponse<MyOrdersResponse>> getMyOrders(String auth,String filterDate) {
//        Call<MyOrdersResponse> call = apiRequest.getMyOrders(auth,filterDate);
//        return performRequest(call);
//    }
//    public LiveData<ApiResponse<HomeResponse>> home(String auth) {
//        Call<HomeResponse> call = apiRequest.home(auth);
//        return performRequest(call);
//    }
//    public LiveData<ApiResponse<NotificationResponse>> getNotification(String auth) {
//        Call<NotificationResponse> call = apiRequest.getNotification(auth);
//        return performRequest(call);
//    }
//
//    public LiveData<ApiResponse<OrderDetails>> orderDetails(String auth,int id) {
//        Call<OrderDetails> call = apiRequest.orderDetails(auth,id);
//        return performRequest(call);
//    }
//
//    public LiveData<ApiResponse<GenericPostResponse>> updateStatus(String auth, UpdateStatusModel updateStatusModel) {
//        Call<GenericPostResponse> call = apiRequest.updateStatus(auth, updateStatusModel);
//        return performRequest(call);
//    }
//
//    public LiveData<ApiResponse<GenericPostResponse>> changeStatus(String auth, ChangeStatus changeStatus) {
//        Call<GenericPostResponse> call = apiRequest.changeStatus(auth, changeStatus);
//        return performRequest(call);
//    }

    public LiveData<ApiResponse<LoginResponse>> loginWithPass(LoginRequest loginRequest) {
        Call<LoginResponse> call = apiRequest.loginWithPass( loginRequest );
        return performRequest(call);
    }

    public LiveData<ApiResponse<HomeResponse>> home(String auth) {
        Call<HomeResponse> call = apiRequest.home(auth);
        return performRequest(call);
    }

    public LiveData<ApiResponse<GenericPostResponse>> updateStatus(String auth, UpdateStatusModel updateStatusModel) {
        Call<GenericPostResponse> call = apiRequest.updateStatus(auth, updateStatusModel);
        return performRequest(call);
    }
    public LiveData<ApiResponse<ProfileResponse>> profile(String auth) {
        Call<ProfileResponse> call = apiRequest.profile(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<OrderDetails>> orderDetails(String auth, int id) {
        Call<OrderDetails> call = apiRequest.orderDetails(auth,id);
        return performRequest(call);
    }
    public LiveData<ApiResponse<GenericPostResponse>> addFcm(String auth, UpdateFcm updateFcm) {
        Call<GenericPostResponse> call = apiRequest.addFcm(auth, updateFcm );
        return performRequest(call);
    }
    public LiveData<ApiResponse<NotificationResponse>> getNotification(String auth) {
        Call<NotificationResponse> call = apiRequest.getNotification(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<MyOrdersResponse>> getMyOrders(String auth,String filterDate) {
        Call<MyOrdersResponse> call = apiRequest.getMyOrders(auth,filterDate);
        return performRequest(call);
    }

    public LiveData<ApiResponse<GenericPostResponse>> returnChange(String auth, RefundAmount refundAmount) {
        Call<GenericPostResponse> call = apiRequest.returnChange(auth,refundAmount);
        return performRequest(call);
    }

    /**
     * Convert URI to File (null-safe)
     */
    private File getFileFromUri(Uri uri, Context context) {
        if (uri == null || context == null) {
            return null;
        }

        try {
            // Handle content:// URIs
            if ("content".equals(uri.getScheme())) {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    return null;
                }

                // Create a temporary file
                File tempFile = new File(context.getCacheDir(), "profile_image_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                return tempFile;
            }
            // Handle file:// URIs
            else if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to File: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create RequestBody from String (null-safe)
     */
    private RequestBody createPartFromString(String value) {
        if (value == null) {
            value = "";
        }
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }



    /**
     * Generic request performer for all API calls.
     * This method can be used by all future repositories.
     */
    protected <T> LiveData<ApiResponse<T>> performRequest(Call<T> call) {
        final MutableLiveData<ApiResponse<T>> liveData = new MutableLiveData<>();
        setLoading(true);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                setLoading(false);
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }

    /**
     * Handles error responses for any API.
     */
    private <T> void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<T>> liveData) {
        try {
            String errorMessage = "An unknown error occurred.";

            if (response.code() == ERROR_SESSION_EXPIRED) {
                errorMessage = "Your session has expired. Please login again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, ERROR_SESSION_EXPIRED));
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", response.code()));
        }
    }

    /**
     * Extracts dynamic error message from JSON or HTML.
     */
    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }
            Document document = Jsoup.parse(errorBody);
            return document.body() != null ? document.body().text().trim() : "An unknown error occurred.";
        } catch (Exception e) {
            Log.e(TAG, "Error while parsing the error message: " + e.getMessage());
            return "An unknown error occurred.";
        }
    }

    /**
     * Handles network failure.
     */
    private <T> void handleNetworkFailure(Call<?> call, Throwable t, MutableLiveData<ApiResponse<T>> liveData) {
        Log.e(TAG, "API call failed: " + t.getMessage(), t);
        String errorMessage = call.isCanceled()
                ? "Request was canceled"
                : "Failed to connect. Please check your network.";
        liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
    }

    /**
     * Generic API response wrapper.
     */
    public static class ApiResponse<T> {
        public final T data;
        public final boolean isSuccess;
        public final String message;
        public final int code;

        public ApiResponse(T data, boolean isSuccess, String message, int code) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
            this.code = code;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }
}
package com.example.currencycoverter.network;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

// Model class for storing api response
public class ApiResponse<T> {

    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final Throwable error;

    private ApiResponse(Status status, @Nullable T data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static ApiResponse success(@NonNull Object data) {
        return new ApiResponse(Status.SUCCESS, data, null);
    }

    public static ApiResponse error(@NonNull Throwable error) {
        return new ApiResponse(Status.ERROR, null, error);
    }

}

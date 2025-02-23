package com.krackhack.olxiitmandi.utils;

import com.krackhack.olxiitmandi.modal.CloudinaryResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudinaryService {
    @Multipart
    @POST("image/upload")  // ✅ Correct API path
    Call<CloudinaryResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset
    );
}
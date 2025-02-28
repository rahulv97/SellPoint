package com.sellpoint.services;

import com.sellpoint.models.Banner;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface BannerApi {
    @Multipart
    @POST("banner_api.php?action=add")
    Call<ResponseBody> uploadBanner(@Part MultipartBody.Part bannerImage);

    @GET("banner_api.php?action=list")
    Call<List<Banner>> getBanners();

    @GET("banner_api.php?action=delete")
    Call<ResponseBody> deleteBanner(@Query("id") int bannerId);
}
package com.sellpoint.services;

import com.google.gson.JsonObject;
import com.sellpoint.models.ItemRequest;
import com.sellpoint.models.LoginRequest;
import com.sellpoint.models.LoginResponse;
import com.sellpoint.models.ProfileResponse;
import com.sellpoint.models.SignupResponse;
import com.sellpoint.models.TransactionResponse;
import com.sellpoint.models.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @POST("signup.php") // Replace with your actual endpoint
    Call<SignupResponse> createUser(@Body User user);

    @POST("login.php") // Replace with your actual endpoint
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @GET("get_profile.php") // Replace with your actual endpoint
    Call<ProfileResponse> getProfile(@Query("mobile") String mobile);

    @Multipart
    @POST("add_item.php")
    Call<ApiResponse> addItem(
            @Part("mobile") RequestBody mobile,
            @Part("category") RequestBody category,
            @Part("item_name") RequestBody itemName,
            @Part("purchase_price") RequestBody purchasePrice,
            @Part("description") RequestBody description,
            @Part("seller_name") RequestBody sellerName,
            @Part("seller_mobile") RequestBody sellerMobile,
            @Part("alternate_seller_mobile") RequestBody alternateSellerMobile,
            @Part("seller_email") RequestBody sellerEmail,
            @Part("remarks") RequestBody remarks,
            @Part("warranty_date") RequestBody warrantyDate,
            @Part("creation_date") RequestBody creationDate,
            @Part("uid1") RequestBody uid1,
            @Part("uid2") RequestBody uid2,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part billPhoto,
            @Part MultipartBody.Part idProof1,
            @Part MultipartBody.Part idProof2,
            @Part MultipartBody.Part sellerPhoto,
            @Part MultipartBody.Part signaturePhoto
    );

    @GET("get_items.php")  // Adjust URL to match the location of your PHP file
    Call<ResponseBody> getItemsByMobile(@Query("mobile") String mobile);

    @Multipart
    @POST("sell_item.php")
    Call<ResponseBody> updateItem(
            @Part("item_id") RequestBody itemId,
            @Part("buyer_name") RequestBody buyerName,
            @Part("buyer_mob") RequestBody buyerMob,
            @Part("buyer_alt_mob") RequestBody buyerAltMob,
            @Part("buyer_email") RequestBody buyerEmail,
            @Part("buyer_address") RequestBody buyerAddress,
            @Part("buyer_note") RequestBody buyerNote,
            @Part("selling_date") RequestBody sellingDate,
            @Part("sell_price") RequestBody sellPrice,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part buyerProof1,
            @Part MultipartBody.Part buyerProof2,
            @Part MultipartBody.Part buyerPhoto
    );

    @Multipart
    @POST("update_items.php")
    Call<ResponseBody> updateItem(
            @Part("id") RequestBody id,
            @Part("mobile") RequestBody mobile,
            @Part("category") RequestBody category,
            @Part("item_name") RequestBody itemName,
            @Part("purchase_price") RequestBody purchasePrice,
            @Part("description") RequestBody description,
            @Part("seller_name") RequestBody sellerName,
            @Part("seller_mobile") RequestBody sellerMobile,
            @Part("alternate_seller_mobile") RequestBody alternateSellerMobile,
            @Part("seller_email") RequestBody sellerEmail,
            @Part("remarks") RequestBody remarks,
            @Part("warranty_date") RequestBody warrantyDate,
            @Part("uid1") RequestBody uid1,
            @Part("uid2") RequestBody uid2,
            @Part MultipartBody.Part billPhoto,
            @Part MultipartBody.Part idProof1,
            @Part MultipartBody.Part idProof2,
            @Part MultipartBody.Part sellerPhoto,
            @Part MultipartBody.Part signaturePhoto
    );

    @Multipart
    @POST("update_seller.php")
    Call<ResponseBody> updateSeller(
            @Part("item_id") RequestBody itemId,
            @Part("buyer_name") RequestBody buyerName,
            @Part("buyer_mob") RequestBody buyerMob,
            @Part("buyer_alt_mob") RequestBody buyerAltMob,
            @Part("buyer_email") RequestBody buyerEmail,
            @Part("buyer_address") RequestBody buyerAddress,
            @Part("buyer_note") RequestBody buyerNote,
            @Part("selling_date") RequestBody sellingDate,
            @Part("sell_price") RequestBody sellPrice,
            @Part("expiry_date") RequestBody expiry_date,
            @Part MultipartBody.Part buyerProof1,
            @Part MultipartBody.Part buyerProof2,
            @Part MultipartBody.Part buyerPhoto
    );

    @PATCH("update_profile.php")
    @Headers("Content-Type: application/json")
    Call<JsonObject> updateUser(@Body JsonObject userData);

    @GET("get_users.php")  // Adjust URL to match the location of your PHP file
    Call<ResponseBody> getUsers();

    @GET("fetch_transactions.php")
    Call<TransactionResponse> getTransactionsByMobile(@Query("mobile") String mobile);

    @Multipart
    @POST("create_udhaar.php")
    Call<ApiResponse> createUdhaar(
            @Part("user_mobile") RequestBody user_mobile,
            @Part("person_name") RequestBody person_name,
            @Part("purchase_price") RequestBody purchase_price,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("mobile") RequestBody mobile,
            @Part("return_date") RequestBody return_date,
            @Part("creation_date") RequestBody creation_date,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part person_image,
            @Part MultipartBody.Part id_proof
    );

    @GET("get_udhaar.php")  // Adjust URL to match the location of your PHP file
    Call<ResponseBody> getUdhaar(@Query("mobile") String mobile);

    @Multipart
    @POST("update_udhaar.php")
    Call<ResponseBody> updateUdhaar(
            @Part("id") RequestBody id,
            @Part("person_name") RequestBody person_name,
            @Part("purchase_price") RequestBody purchase_price,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("mobile") RequestBody mobile,
            @Part("return_date") RequestBody return_date,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part person_image,
            @Part MultipartBody.Part id_proof
    );

}
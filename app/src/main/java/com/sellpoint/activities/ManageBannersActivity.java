package com.sellpoint.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sellpoint.R;
import com.sellpoint.adapter.BannersAdapter;
import com.sellpoint.interfaces.DeleteInterface;
import com.sellpoint.models.Banner;
import com.sellpoint.services.BannerApi;
import com.sellpoint.services.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageBannersActivity extends AppCompatActivity implements DeleteInterface {

    CardView newBannerCard;

    int BANNER_IMG_CODE = 90;

    ProgressDialog progressDialog;

    BannersAdapter bannersAdapter;

    RecyclerView bannersRecycler;

    ArrayList<Banner> bannerList = new ArrayList<>();

    ImageView refreshBtn, back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_banners);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClick();
        listBanners();

    }

    void init(){
        progressDialog = new ProgressDialog(ManageBannersActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        newBannerCard = findViewById(R.id.newBannerCard);
        bannersRecycler = findViewById(R.id.bannersRecycler);
        refreshBtn = findViewById(R.id.refreshBtn);
        back_btn = findViewById(R.id.back_btn);

        bannersAdapter = new BannersAdapter(ManageBannersActivity.this, bannerList, this::deleteBanner);
        bannersRecycler.setLayoutManager(new LinearLayoutManager(ManageBannersActivity.this));
        bannersRecycler.setAdapter(bannersAdapter);
    }

    void onClick(){
        newBannerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(BANNER_IMG_CODE);
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBanners();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void listBanners() {
        progressDialog.show();
        BannerApi api = RetrofitClient.getInstance(getResources().getString(R.string.BASE_URL)).create(BannerApi.class);

        api.getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful()) {
                    List<Banner> banners = response.body();
                    bannerList.clear();
                    for (Banner banner : banners) {
                        Log.d("Banner", "ID: " + banner.getId() + ", Path: " + banner.getImagePath());
                        bannerList.add(banner);
                    }
                    bannersAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ManageBannersActivity.this, "Failed to fetch banners", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageBannersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void uploadBanner(File file) {
        progressDialog.show();
        BannerApi api = RetrofitClient.getInstance(getResources().getString(R.string.BASE_URL)).create(BannerApi.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("banner_image", file.getName(), requestFile);

        api.uploadBanner(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(ManageBannersActivity.this, "Banner uploaded successfully", Toast.LENGTH_SHORT).show();
                    listBanners();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ManageBannersActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ManageBannersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectImage(int request) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri fileUri = null;

            // If from Gallery
            if (data != null && data.getData() != null) {
                fileUri = data.getData();
            }

            if (fileUri != null) {
                String imagePath = FileUtils.getPath(this, fileUri); // Custom method to resolve path
                Log.d("CheckImgPath", "onActivityResult: "+imagePath);

                if (requestCode == BANNER_IMG_CODE) {
                    File selectedImage = new File(imagePath);
                    uploadBanner(selectedImage);
                }
            }
        }
    }

    @Override
    public void deleteBanner(int id) {
        deleteBannerApi(id);
    }

    private void deleteBannerApi(int bannerId) {
        BannerApi api = RetrofitClient.getInstance(getResources().getString(R.string.BASE_URL)).create(BannerApi.class);

        api.deleteBanner(bannerId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listBanners();
                    Toast.makeText(ManageBannersActivity.this, "Banner deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageBannersActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ManageBannersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static class RetrofitClient {
        private static Retrofit retrofit;

        public static Retrofit getInstance(String BASE_URL) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
}
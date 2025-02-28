package com.sellpoint.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sellpoint.R;
import com.sellpoint.models.Banner;
import com.sellpoint.models.ProfileResponse;
import com.sellpoint.services.ApiService;
import com.sellpoint.services.BannerApi;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageCarousel carousel;

    String expiryDate = "null";

    LinearLayout linear1, viewUsers, linear2;

    ProfileResponse.Data data;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    List<CarouselItem> list;

    ImageView notificationBtn, ivMenu, refreshBtn, switchBtn;

    DrawerLayout dl;

    NavigationView navigationView;

   // CardView salePurchaseCard, udhaarCard;

    TextView shop_name, dashAvlStock, dashSoldlStock, dashPurchasedStock, dashPurchaseNew, dashChangePass, logout,
            dashEditProfile, shopNameVal, devExp, dashBanner, dashTransactions;

    CardView availStock, newPurchaseCard, soldItemsCard, purchasedItemsCard;
    TextView txtAvlNum, txtSoldNum, txtPurNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        navigationView = findViewById(R.id.navigator);
        navigationView.setNavigationItemSelectedListener(DashboardActivity.this);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        askPermissions();

        init();

        handleCarousel();

        onclick();

        fetchProfileDetails(sharedPreferences.getString("Mobile", ""));

        getItems(sharedPreferences.getString("Mobile", ""));


    }

    void init(){
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        dl = findViewById(R.id.drawer_layout);
        refreshBtn = findViewById(R.id.refreshBtn);
        linear1 = findViewById(R.id.linear1);
        linear2 = findViewById(R.id.linear2);
        devExp = findViewById(R.id.devExp);
        viewUsers = findViewById(R.id.viewUsers);
        dashEditProfile = findViewById(R.id.dashEditProfile);
        list = new ArrayList<>();
        carousel = findViewById(R.id.carousel);
        ivMenu = findViewById(R.id.ivMenu);
        notificationBtn = findViewById(R.id.notificationBtn);
        switchBtn = findViewById(R.id.switchBtn);
        shop_name = findViewById(R.id.shop_name);
        shopNameVal = findViewById(R.id.shopNameVal);
        dashAvlStock = findViewById(R.id.dashAvlStock);
        dashTransactions = findViewById(R.id.dashTransactions);
        dashBanner = findViewById(R.id.dashBanner);
        dashSoldlStock = findViewById(R.id.dashSoldlStock);
        dashPurchasedStock = findViewById(R.id.dashPurchasedStock);
        dashPurchaseNew = findViewById(R.id.dashPurchaseNew);
        dashChangePass = findViewById(R.id.dashChangePass);
        logout = findViewById(R.id.logout);
        /*salePurchaseCard = findViewById(R.id.salePurchaseCard);
        udhaarCard = findViewById(R.id.udhaarCard);*/

        availStock = findViewById(R.id.availStock);
        newPurchaseCard = findViewById(R.id.newPurchaseCard);
        soldItemsCard = findViewById(R.id.soldItemsCard);
        purchasedItemsCard = findViewById(R.id.purchasedItemsCard);
        txtAvlNum = findViewById(R.id.txtAvlNum);
        txtSoldNum = findViewById(R.id.txtSoldNum);
        txtPurNum = findViewById(R.id.txtPurNum);
        refreshBtn = findViewById(R.id.refreshBtn);

        carousel.registerLifecycle(getLifecycle());

        if (!sharedPreferences.getString("AdminLogin", "").equals("")){
            switchBtn.setVisibility(View.VISIBLE);
        } else {
            switchBtn.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sharedPreferences.getString("AdminLogin", "").equals("")){
            switchBtn.setVisibility(View.VISIBLE);
        } else {
            switchBtn.setVisibility(View.GONE);
        }
        handleCarousel();
        fetchProfileDetails(sharedPreferences.getString("Mobile", ""));
        getItems(sharedPreferences.getString("Mobile", ""));

    }

    void onclick(){

        availStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "avlStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });
        newPurchaseCard.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if (expiryDate.equals("null")){
                    Toast.makeText(DashboardActivity.this, "You don't have any package yet", Toast.LENGTH_LONG).show();

                }else {
                    String inputDateStr = expiryDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

                    try {
                        // Parse input date
                        Date inputDate = dateFormat.parse(inputDateStr);
                        // Get the current date
                        Date currentDate = new Date();

                        // Compare dates
                        if (inputDate.after(currentDate)) {
                            Intent intent = new Intent(DashboardActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                        } else if (inputDate.before(currentDate)) {
                            Toast.makeText(DashboardActivity.this, "Your package has been expired", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(DashboardActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            }
        });

        soldItemsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "soldStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });
        purchasedItemsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "purStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });

       /* salePurchaseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });

        udhaarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, UdhaarActivity.class);
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });*/

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DashboardActivity.this)
                        .setMessage("Do you want to switch back to admin?")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("Mobile", sharedPreferences.getString("AdminLogin", ""));
                                editor.putString("AdminLogin", "");
                                editor.apply();
                                fetchProfileDetails(sharedPreferences.getString("Mobile", ""));
                                switchBtn.setVisibility(View.GONE);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ListUsersActivity.class);
                startActivity(intent);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCarousel();
                fetchProfileDetails(sharedPreferences.getString("Mobile", ""));
                getItems(sharedPreferences.getString("Mobile", ""));

            }
        });

        dashEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SignupActivity.class);
                intent.putExtra("NewOrEdit", "Edit");
                intent.putExtra("Values", data);
                startActivity(intent);
                dl.closeDrawer(GravityCompat.START);
            }
        });

        dashTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TransactionsActivity.class));
            }
        });

        dashAvlStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "avlStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
                dl.closeDrawer(GravityCompat.START);
            }
        });
        dashBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ManageBannersActivity.class));
            }
        });
        dashSoldlStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent1.putExtra("ReqType", "soldStock");
                intent1.putExtra("ExpiryDate", expiryDate);
                startActivity(intent1);
                dl.closeDrawer(GravityCompat.START);
            }
        });
        dashPurchasedStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(DashboardActivity.this, ItemsListActivity.class);
                intent2.putExtra("ReqType", "purStock");
                intent2.putExtra("ExpiryDate", expiryDate);
                startActivity(intent2);
                dl.closeDrawer(GravityCompat.START);
            }
        });
        dashPurchaseNew.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                if (expiryDate.equals("null")){
                    Toast.makeText(DashboardActivity.this, "You don't have any package yet", Toast.LENGTH_LONG).show();

                }else {
                    String inputDateStr = expiryDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

                    try {
                        // Parse input date
                        Date inputDate = dateFormat.parse(inputDateStr);
                        // Get the current date
                        Date currentDate = new Date();

                        // Compare dates
                        if (inputDate.after(currentDate)) {
                            Intent intent = new Intent(DashboardActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                            dl.closeDrawer(GravityCompat.START);
                        } else if (inputDate.before(currentDate)) {
                            Toast.makeText(DashboardActivity.this, "Your package has been expired", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(DashboardActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                            dl.closeDrawer(GravityCompat.START);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }






            }
        });
        dashChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EditPasswordActivity.class);
                intent.putExtra("Values", data);
                startActivity(intent);
                dl.closeDrawer(GravityCompat.START);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DashboardActivity.this)
                        .setMessage("Do you really want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.clear().apply();
                                Intent intent3 = new Intent(DashboardActivity.this, MainActivity.class);
                                startActivity(intent3);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dl.closeDrawer(GravityCompat.START);
                            }
                        })
                        .show();
            }
        });




        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dl.open();
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, NotificationsActivity.class));
            }
        });
    }

    void handleCarousel() {


        BannerApi api = ManageBannersActivity.RetrofitClient.getInstance(getResources().getString(R.string.BASE_URL)).create(BannerApi.class);

        api.getBanners().enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                if (response.isSuccessful()) {
                    List<Banner> banners = response.body();
                    list.clear();
                    for (Banner banner : banners) {
                        Log.d("Banner", "ID: " + banner.getId() + ", Path: " + banner.getImagePath());
                        list.add(
                                new CarouselItem(getResources().getString(R.string.BASE_URL)+banner.getImagePath())
                        );
                    }

                    carousel.setData(list);

                } else {
                    Log.d("Banner", "onResponse: "+"Failed to fetch banners");
                    //Toast.makeText(ManageBannersActivity.this, "Failed to fetch banners", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.d("Banner", "Error: " + t.getMessage());
                //Toast.makeText(ManageBannersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {



        if (dl.isOpen()) {
            dl.close();
        } else {
            new AlertDialog.Builder(DashboardActivity.this)
                    .setMessage("Do you really want to quit this application?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DashboardActivity.super.onBackPressed();
                        }
                    })
                    .show();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
      return true;
    }

    private void fetchProfileDetails(String mobile) {
        ApiService apiService = RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);
        Call<ProfileResponse> call = apiService.getProfile(mobile);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profileResponse = response.body();
                    if ("success".equals(profileResponse.getStatus())) {
                        data = profileResponse.getData();
                        shop_name.setText(data.getShopName() + "\n" + data.getFullName());
                        shopNameVal.setText(data.getShopName() + "\n" + data.getFullName());

                        expiryDate = data.getExpiry_date() == null? "null": data.getExpiry_date();
                        devExp.setText("Expiry Date: "+expiryDate);

                        if (data.getShopName().equals("Administrator")){
                            linear1.setVisibility(View.GONE);
                            linear2.setVisibility(View.GONE);
                            viewUsers.setVisibility(View.VISIBLE);

                            dashAvlStock.setVisibility(View.GONE);
                            dashTransactions.setVisibility(View.GONE);
                            dashSoldlStock.setVisibility(View.GONE);
                            dashPurchasedStock.setVisibility(View.GONE);
                            dashPurchaseNew.setVisibility(View.GONE);
                            dashChangePass.setVisibility(View.GONE);
                            dashBanner.setVisibility(View.VISIBLE);
                        }
                        else {
                            linear1.setVisibility(View.VISIBLE);
                            linear2.setVisibility(View.VISIBLE);
                            viewUsers.setVisibility(View.GONE);
                            dashBanner.setVisibility(View.GONE);

                            dashAvlStock.setVisibility(View.VISIBLE);
                            dashTransactions.setVisibility(View.VISIBLE);
                            dashSoldlStock.setVisibility(View.VISIBLE);
                            dashPurchasedStock.setVisibility(View.VISIBLE);
                            dashPurchaseNew.setVisibility(View.VISIBLE);
                            dashChangePass.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(DashboardActivity.this, profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to fetch profile details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class RetrofitClient {
        private static Retrofit retrofit = null;

        public static Retrofit getClient(String BASE_URL) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    void askPermissions(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    private void getItems(String mobile) {
        ApiService apiService = DashboardActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);
        Call<ResponseBody> call = apiService.getItemsByMobile(mobile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Get the raw JSON response
                        String responseJson = response.body().string();

                        // Log the raw JSON response
                        Log.d("API Response", responseJson);

                        JSONObject jsonObject = new JSONObject(responseJson);
                        String status = jsonObject.getString("status");

                        if ("success".equals(status)) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");

                            // Create a list to store items

                            int numAvl = 0;
                            int numSold = 0;
                            int numPur = dataArray.length();

                            // Loop through the array and add items to the list
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObject = dataArray.getJSONObject(i);

                                String itemName = itemObject.getString("item_name");
                                String sellerName = itemObject.getString("seller_name");
                                String itemStatus = itemObject.getString("status");

                                if (itemStatus.equals("0")){
                                    numAvl=numAvl+1;
                                }
                                if (itemStatus.equals("1")){
                                    numSold=numSold+1;
                                }

                            }


                            txtAvlNum.setText(String.valueOf(numAvl));
                            txtSoldNum.setText(String.valueOf(numSold));
                            txtPurNum.setText(String.valueOf(numPur));


                            // Log or use the list

                        } else {
                            // Toast.makeText(DashboardActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(DashboardActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(DashboardActivity.this, "Error fetching items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(DashboardActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
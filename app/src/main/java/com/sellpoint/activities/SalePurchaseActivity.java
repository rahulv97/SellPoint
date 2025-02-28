package com.sellpoint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sellpoint.R;
import com.sellpoint.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalePurchaseActivity extends AppCompatActivity {

    CardView availStock, newPurchaseCard, soldItemsCard, purchasedItemsCard;
    TextView txtAvlNum, txtSoldNum, txtPurNum;

    String expiryDate = "null";

    ImageView refreshBtn, back_btn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sale_purchase);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClick();

        getItems(sharedPreferences.getString("Mobile", ""));

    }

    @Override
    protected void onResume() {
        super.onResume();
        getItems(sharedPreferences.getString("Mobile", ""));
    }

    void init(){
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        availStock = findViewById(R.id.availStock);
        newPurchaseCard = findViewById(R.id.newPurchaseCard);
        soldItemsCard = findViewById(R.id.soldItemsCard);
        purchasedItemsCard = findViewById(R.id.purchasedItemsCard);
        txtAvlNum = findViewById(R.id.txtAvlNum);
        txtSoldNum = findViewById(R.id.txtSoldNum);
        txtPurNum = findViewById(R.id.txtPurNum);
        refreshBtn = findViewById(R.id.refreshBtn);
        back_btn = findViewById(R.id.back_btn);
        expiryDate = getIntent().getStringExtra("ExpiryDate");
    }

    void onClick(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        availStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalePurchaseActivity.this, ItemsListActivity.class);
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
                    Toast.makeText(SalePurchaseActivity.this, "You don't have any package yet", Toast.LENGTH_LONG).show();

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
                            Intent intent = new Intent(SalePurchaseActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                        } else if (inputDate.before(currentDate)) {
                            Toast.makeText(SalePurchaseActivity.this, "Your package has been expired", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(SalePurchaseActivity.this, NewPurchaseActivity.class);
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
                Intent intent = new Intent(SalePurchaseActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "soldStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });
        purchasedItemsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalePurchaseActivity.this, ItemsListActivity.class);
                intent.putExtra("ReqType", "purStock");
                intent.putExtra("ExpiryDate", expiryDate);
                startActivity(intent);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems(sharedPreferences.getString("Mobile", ""));
            }
        });

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
                            // Toast.makeText(SalePurchaseActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(SalePurchaseActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(SalePurchaseActivity.this, "Error fetching items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(SalePurchaseActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
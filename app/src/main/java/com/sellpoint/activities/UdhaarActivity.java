package com.sellpoint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sellpoint.R;
import com.sellpoint.models.UdhaarModel;
import com.sellpoint.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UdhaarActivity extends AppCompatActivity {

    ImageView back_btn, refreshBtn;
    CardView entryCard, unpaidCard, paidCard;

    String expiryDate = "null";

    TextView txtUnpaidNum, txtPaidNum;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_udhaar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClick();
        getItems(sharedPreferences.getString("Mobile", ""));

    }

    void init(){
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        back_btn = findViewById(R.id.back_btn);
        refreshBtn = findViewById(R.id.refreshBtn);
        entryCard = findViewById(R.id.entryCard);
        unpaidCard = findViewById(R.id.unpaidCard);
        paidCard = findViewById(R.id.paidCard);
        txtUnpaidNum = findViewById(R.id.txtUnpaidNum);
        txtPaidNum = findViewById(R.id.txtPaidNum);
        expiryDate = getIntent().getStringExtra("ExpiryDate");
    }

    void onClick(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems(sharedPreferences.getString("Mobile", ""));
            }
        });

        entryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputDateStr = expiryDate;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

                try {
                    // Parse input date
                    Date inputDate = dateFormat.parse(inputDateStr);
                    // Get the current date
                    Date currentDate = new Date();

                    // Compare dates
                    if (inputDate.after(currentDate)) {
                        Intent intent = new Intent(UdhaarActivity.this, NewUdhaarEntryActivity.class);
                        intent.putExtra("NewOrEdit", "New");
                        startActivity(intent);
                    } else if (inputDate.before(currentDate)) {
                        Toast.makeText(UdhaarActivity.this, "Your package has been expired", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(UdhaarActivity.this, NewUdhaarEntryActivity.class);
                        intent.putExtra("NewOrEdit", "New");
                        startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        unpaidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UdhaarActivity.this, UdhaarListActivity.class);
                intent.putExtra("ReqType", "unpaid");
                startActivity(intent);
            }
        });

        paidCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UdhaarActivity.this, UdhaarListActivity.class);
                intent.putExtra("ReqType", "paid");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getItems(sharedPreferences.getString("Mobile", ""));
    }

    private void getItems(String mobile) {
        ApiService apiService = DashboardActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);
        Call<ResponseBody> call = apiService.getUdhaar(mobile);

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

                            int unpaids = 0;
                            int paids = 0;

                            // Loop through the array and add items to the list
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObject = dataArray.getJSONObject(i);

                                String itemStatus = itemObject.getString("status");

                                if(itemStatus.equals("0")){
                                    unpaids = unpaids+1;
                                }

                                if(itemStatus.equals("1")){
                                    paids = paids+1;
                                }


                            }

                            txtUnpaidNum.setText(String.valueOf(unpaids));
                            txtPaidNum.setText(String.valueOf(paids));

                            // Log or use the list

                        } else {
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(DashboardActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }

}
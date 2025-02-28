package com.sellpoint.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sellpoint.R;
import com.sellpoint.adapter.UdhaarAdapter;
import com.sellpoint.models.ItemsModel;
import com.sellpoint.models.UdhaarModel;
import com.sellpoint.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UdhaarListActivity extends AppCompatActivity {

    ImageView back_btn, refreshBtn;
    RecyclerView udhaarRecycler;

    UdhaarAdapter udhaarAdapter;

    TextView listHead, udhaarCount;
    EditText search_editText;

    SharedPreferences sharedPreferences;

    String reqType;

    ArrayList<UdhaarModel> itemsList = new ArrayList<>();
    ArrayList<UdhaarModel> permaItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_udhaar_list);
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
        back_btn = findViewById(R.id.back_btn);
        search_editText = findViewById(R.id.search_editText);
        listHead = findViewById(R.id.listHead);
        udhaarRecycler = findViewById(R.id.udhaarRecycler);
        udhaarCount = findViewById(R.id.udhaarCount);
        refreshBtn = findViewById(R.id.refreshBtn);

        reqType = getIntent().getStringExtra("ReqType");
        if (reqType.equals("unpaid")){
            listHead.setText("Unpaid");
        }

        if (reqType.equals("paid")){
            listHead.setText("Paid");
        }

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<UdhaarModel> filtered_list = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filtered_list.addAll(permaItemsList);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();
                    for (UdhaarModel userModel : permaItemsList) {
                        if (userModel.getPerson_name().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getAddress().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getDescription().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getPurchase_price().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getMobile().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getReturn_date().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getCreation_date().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filtered_list.add(userModel);
                        }
                    }


                }

                FilterResults results = new FilterResults();
                results.values = filtered_list;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsList.clear();
                itemsList.addAll((List) filterResults.values);
                //Log.d("UsersListt", "publishResults: "+filterResults.values);
                udhaarAdapter.notifyDataSetChanged();
            }
        };

        search_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter.filter(editable.toString());
            }
        });

        udhaarAdapter = new UdhaarAdapter(UdhaarListActivity.this, reqType, filter, itemsList);
        udhaarRecycler.setLayoutManager(new LinearLayoutManager(UdhaarListActivity.this));
        udhaarRecycler.setAdapter(udhaarAdapter);
        udhaarAdapter.notifyDataSetChanged();

        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);

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

                            // Create a list to store items

                            itemsList.clear();

                            // Loop through the array and add items to the list
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObject = dataArray.getJSONObject(i);

                                String id = itemObject.getString("id");
                                String person_name = itemObject.getString("person_name");
                                String purchase_price = itemObject.getString("purchase_price");
                                String description = itemObject.getString("description");
                                String address = itemObject.getString("address");
                                String mobile = itemObject.getString("mobile");
                                String return_date = itemObject.getString("return_date");
                                String creation_date = itemObject.getString("creation_date");
                                String status_val = itemObject.getString("status");
                                String person_image = itemObject.getString("person_image");
                                String id_proof = itemObject.getString("id_proof");



                                String itemStatus = itemObject.getString("status");

                                if (reqType.equals("unpaid")){
                                    if(itemStatus.equals("0")){
                                        itemsList.add(new UdhaarModel(id, person_name, purchase_price, description, address,
                                                mobile, return_date, creation_date, status_val, person_image,
                                                id_proof));
                                    }
                                }

                                if (reqType.equals("paid")){
                                    if(itemStatus.equals("1")){
                                        itemsList.add(new UdhaarModel(id, person_name, purchase_price, description, address,
                                                mobile, return_date, creation_date, status_val, person_image,
                                                id_proof));
                                    }
                                }


                            }

                            Collections.reverse(itemsList);

                            permaItemsList = new ArrayList<>(itemsList);


                            udhaarAdapter.notifyDataSetChanged();

                            udhaarCount.setText(String.valueOf(itemsList.size()));

                            // Log or use the list

                        } else {
                            Toast.makeText(UdhaarListActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(DashboardActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(UdhaarListActivity.this, "Error fetching items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(UdhaarListActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
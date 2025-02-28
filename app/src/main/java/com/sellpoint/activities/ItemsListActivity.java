package com.sellpoint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sellpoint.R;
import com.sellpoint.adapter.ProductListAdapter;
import com.sellpoint.models.ItemsModel;
import com.sellpoint.services.ApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemsListActivity extends AppCompatActivity {

    RecyclerView stockRecycler;
    ProductListAdapter productListAdapter;

    EditText search_editText;

    ImageView back_btn, refreshBtn;

    String reqType = "";

    TextView listHead, stockCount;

    CardView newProdCard;

    SharedPreferences sharedPreferences;

    String expiryDate = "null";

    ArrayList<ItemsModel> itemsList = new ArrayList<>();
    ArrayList<ItemsModel> permaItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<ItemsModel> filtered_list = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filtered_list.addAll(permaItemsList);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();
                    for (ItemsModel userModel : permaItemsList) {
                        if (userModel.getItemName().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getPurchasePrice().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getSellerMobile().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getSellerName().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getUid1().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getUid2().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getBuyerName().toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
                productListAdapter.notifyDataSetChanged();
            }
        };

        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);

        reqType = getIntent().getStringExtra("ReqType");
        expiryDate = getIntent().getStringExtra("ExpiryDate");

        stockRecycler = findViewById(R.id.stockRecycler);
        back_btn = findViewById(R.id.back_btn);
        listHead = findViewById(R.id.listHead);
        newProdCard = findViewById(R.id.newProdCard);
        stockCount = findViewById(R.id.stockCount);
        refreshBtn = findViewById(R.id.refreshBtn);
        search_editText = findViewById(R.id.search_editText);

        productListAdapter = new ProductListAdapter(ItemsListActivity.this, itemsList, filter);
        stockRecycler.setLayoutManager(new LinearLayoutManager(ItemsListActivity.this));
        stockRecycler.setAdapter(productListAdapter);
        productListAdapter.notifyDataSetChanged();

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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (reqType.equals("avlStock")){
            listHead.setText("Available Stock");
            newProdCard.setVisibility(View.VISIBLE);
        }

        if (reqType.equals("soldStock")){
            listHead.setText("Sold Items");
            newProdCard.setVisibility(View.GONE);
        }

        if (reqType.equals("purStock")){
            listHead.setText("Purchased Items");
            newProdCard.setVisibility(View.VISIBLE);
        }

        newProdCard.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if (expiryDate.equals("null")){
                    Toast.makeText(ItemsListActivity.this, "You don't have any package yet", Toast.LENGTH_LONG).show();

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
                            Intent intent = new Intent(ItemsListActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                        } else if (inputDate.before(currentDate)) {
                            Toast.makeText(ItemsListActivity.this, "Your package has been expired", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(ItemsListActivity.this, NewPurchaseActivity.class);
                            intent.putExtra("NewOrEdit", "New");
                            startActivity(intent);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            }
        });

        getItems(sharedPreferences.getString("Mobile", ""));

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

                            itemsList.clear();

                            // Loop through the array and add items to the list
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObject = dataArray.getJSONObject(i);

                                String id = itemObject.getString("id");
                                String category = itemObject.getString("category");
                                String item_name = itemObject.getString("item_name");
                                String purchase_price = itemObject.getString("purchase_price");
                                String description = itemObject.getString("description");
                                String seller_name = itemObject.getString("seller_name");
                                String seller_mobile = itemObject.getString("seller_mobile");
                                String alternate_seller_mobile = itemObject.getString("alternate_seller_mobile");
                                String seller_email = itemObject.getString("seller_email");
                                String remarks = itemObject.getString("remarks");
                                String warranty_date = itemObject.getString("warranty_date");
                                String bill_photo = itemObject.getString("bill_photo");
                                String id_proof_1 = itemObject.getString("id_proof_1");
                                String id_proof_2 = itemObject.getString("id_proof_2");
                                String seller_photo = itemObject.getString("seller_photo");
                                String signature_photo = itemObject.getString("signature_photo");
                                String creation_date = itemObject.getString("creation_date");
                                String buyer_name = itemObject.getString("buyer_name");
                                String buyer_mob = itemObject.getString("buyer_mob");
                                String buyer_alt_mob = itemObject.getString("buyer_alt_mob");
                                String buyer_email = itemObject.getString("buyer_email");
                                String buyer_address = itemObject.getString("buyer_address");
                                String buyer_note = itemObject.getString("buyer_note");
                                String buyer_proof1 = itemObject.getString("buyer_proof1");
                                String buyer_proof2 = itemObject.getString("buyer_proof2");
                                String buyer_photo = itemObject.getString("buyer_photo");
                                String selling_date = itemObject.getString("selling_date");
                                String sell_price = itemObject.getString("sell_price");
                                String uid1 = itemObject.getString("uid1");
                                String uid2 = itemObject.getString("uid2");

                                Log.d("SellPrice", "onResponse: "+sell_price);

                                String itemStatus = itemObject.getString("status");

                                if (reqType.equals("avlStock")){
                                    if(itemStatus.equals("0")){
                                        itemsList.add(new ItemsModel(id, category, item_name, creation_date, purchase_price,
                                                description, warranty_date, bill_photo, seller_name, seller_mobile,
                                                alternate_seller_mobile, seller_email, remarks, id_proof_1, id_proof_2,
                                                seller_photo, signature_photo, itemStatus, buyer_name, buyer_mob, buyer_alt_mob,
                                                buyer_email, buyer_address, buyer_note, buyer_proof1, buyer_proof2, buyer_photo,
                                                selling_date, sell_price, uid1, uid2));
                                    }
                                }

                                if (reqType.equals("soldStock")){
                                    if(itemStatus.equals("1")){
                                        itemsList.add(new ItemsModel(id, category, item_name, creation_date, purchase_price,
                                                description, warranty_date, bill_photo, seller_name, seller_mobile,
                                                alternate_seller_mobile, seller_email, remarks, id_proof_1, id_proof_2,
                                                seller_photo, signature_photo, itemStatus, buyer_name, buyer_mob, buyer_alt_mob,
                                                buyer_email, buyer_address, buyer_note, buyer_proof1, buyer_proof2, buyer_photo,
                                                selling_date, sell_price, uid1, uid2));
                                    }
                                }

                                if (reqType.equals("purStock")){
                                    itemsList.add(new ItemsModel(id, category, item_name, creation_date, purchase_price,
                                            description, warranty_date, bill_photo, seller_name, seller_mobile,
                                            alternate_seller_mobile, seller_email, remarks, id_proof_1, id_proof_2,
                                            seller_photo, signature_photo, itemStatus, buyer_name, buyer_mob, buyer_alt_mob,
                                            buyer_email, buyer_address, buyer_note, buyer_proof1, buyer_proof2, buyer_photo,
                                            selling_date, sell_price, uid1, uid2));
                                }

                            }

                            Collections.reverse(itemsList);

                            permaItemsList = new ArrayList<>(itemsList);


                            productListAdapter.notifyDataSetChanged();

                            stockCount.setText(String.valueOf(itemsList.size()));

                            // Log or use the list

                        } else {
                            Toast.makeText(ItemsListActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(DashboardActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(ItemsListActivity.this, "Error fetching items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(ItemsListActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
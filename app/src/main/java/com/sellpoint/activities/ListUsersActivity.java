package com.sellpoint.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sellpoint.R;
import com.sellpoint.adapter.UsersAdapter;
import com.sellpoint.models.ItemsModel;
import com.sellpoint.models.UsersModel;
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

public class ListUsersActivity extends AppCompatActivity {

    RecyclerView usersRc;

    ArrayList<UsersModel> usersList = new ArrayList<>();
    ArrayList<UsersModel> permaUsersList = new ArrayList<>();
    UsersAdapter usersAdapter;

    ImageView back_btn, refreshBtn;

    EditText search_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usersRc = findViewById(R.id.usersRc);
        back_btn = findViewById(R.id.back_btn);
        refreshBtn = findViewById(R.id.refreshBtn);
        search_editText = findViewById(R.id.search_editText);

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<UsersModel> filtered_list = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filtered_list.addAll(permaUsersList);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase(Locale.ROOT).trim();
                    for (UsersModel userModel : permaUsersList) {
                        if (userModel.getShopName().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getOwnerName().toLowerCase(Locale.ROOT).contains(filterPattern)
                                || userModel.getMobile().toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
                usersList.clear();
                usersList.addAll((List) filterResults.values);
                //Log.d("UsersListt", "publishResults: "+filterResults.values);
                usersAdapter.notifyDataSetChanged();
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

        usersAdapter = new UsersAdapter(usersList, ListUsersActivity.this, filter);
        usersRc.setLayoutManager(new LinearLayoutManager(ListUsersActivity.this));
        usersRc.setAdapter(usersAdapter);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems();
            }
        });

        getItems();



    }

    @Override
    protected void onResume() {
        super.onResume();
        getItems();
    }

    private void getItems() {
        ApiService apiService = DashboardActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);
        Call<ResponseBody> call = apiService.getUsers();

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

                            usersList.clear();

                            // Loop through the array and add items to the list
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObject = dataArray.getJSONObject(i);

                                String id = itemObject.getString("id");
                                String shopName = itemObject.getString("shop_name");
                                String ownerName = itemObject.getString("full_name");
                                String mobile = itemObject.getString("mobile");
                                String email = itemObject.getString("email");
                                String password = itemObject.getString("password");
                                String created_at = itemObject.getString("created_at");
                                String expiry_date = itemObject.getString("expiry_date");

                                if (!shopName.equals("Administrator")){
                                    usersList.add(new UsersModel(id, shopName, ownerName, mobile, email, password,
                                            created_at, expiry_date));
                                }

                            }

                            Collections.reverse(usersList);

                            permaUsersList = new ArrayList<>(usersList);


                            usersAdapter.notifyDataSetChanged();


                            // Log or use the list

                        } else {
                            Toast.makeText(ListUsersActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                        }

                        // You can also display it in a Toast or process it further
                        //Toast.makeText(DashboardActivity.this, "Items fetched successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    Toast.makeText(ListUsersActivity.this, "Error fetching items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(ListUsersActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
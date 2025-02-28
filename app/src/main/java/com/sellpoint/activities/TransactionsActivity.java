package com.sellpoint.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.sellpoint.adapter.TransacitionsAdapter;
import com.sellpoint.models.Transaction;
import com.sellpoint.models.TransactionResponse;
import com.sellpoint.services.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    ImageView back_btn, refreshBtn;
    RecyclerView transactionRV;
    TransacitionsAdapter transacitionsAdapter;

    List<Transaction> transactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClicks();
        fetchTransactions(sharedPreferences.getString("Mobile", ""));

    }

    void init(){
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        back_btn = findViewById(R.id.back_btn);
        refreshBtn = findViewById(R.id.refreshBtn);
        transactionRV = findViewById(R.id.transactionRV);

    }

    void onClicks(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTransactions(sharedPreferences.getString("Mobile", ""));
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

    private void fetchTransactions(String mobile) {
        Log.d("TransacMobile", "onClick: "+sharedPreferences.getString("Mobile", ""));
        ApiService apiService = RetrofitClient.getInstance(getResources().getString(R.string.BASE_URL)).create(ApiService.class);
        Call<TransactionResponse> call = apiService.getTransactionsByMobile(mobile);

        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                Log.d("TransacMobile", "onClick: "+response.code());
                if (response.isSuccessful() && response.body() != null) {
                    TransactionResponse transactionResponse = response.body();
                    if ("success".equals(transactionResponse.getStatus())) {
                        transactions = transactionResponse.getTransactions();
                        Log.d("TransacMobile", "onClick: "+transactions.size());
                        transacitionsAdapter = new TransacitionsAdapter(transactions, TransactionsActivity.this);
                        transactionRV.setLayoutManager(new LinearLayoutManager(TransactionsActivity.this));
                        transactionRV.setAdapter(transacitionsAdapter);
                        transacitionsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TransactionsActivity.this, transactionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TransactionsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                Toast.makeText(TransactionsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
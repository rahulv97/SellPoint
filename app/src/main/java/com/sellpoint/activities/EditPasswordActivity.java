package com.sellpoint.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.sellpoint.R;
import com.sellpoint.models.ProfileResponse;
import com.sellpoint.services.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPasswordActivity extends AppCompatActivity {

    ImageView back_btn;
    EditText oldPass, newPass, confPass;
    AppCompatButton changePassBtn;

    ProfileResponse.Data data;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClicks();

    }

    void init(){

        progressDialog = new ProgressDialog(EditPasswordActivity.this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);

        back_btn = findViewById(R.id.back_btn);
        oldPass = findViewById(R.id.oldPass);
        newPass = findViewById(R.id.newPass);
        confPass = findViewById(R.id.confPass);
        changePassBtn = findViewById(R.id.changePassBtn);

        data = getIntent().getParcelableExtra("Values");

    }

    void onClicks(){
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPass.getText().toString();
                String newPassword = newPass.getText().toString();
                String confPassword = confPass.getText().toString();

                String myOldPass = data.getPassword();

                if (myOldPass.equals(oldPassword)){
                    if (newPassword.equals(confPassword)){
                        updateProfile();
                    }else {
                        Toast.makeText(EditPasswordActivity.this, "New Password and Confirm New Password did not match"
                                , Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(EditPasswordActivity.this, "Old Password Did not match!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    void updateProfile(){

        progressDialog.show();

        String shopname = data.getShopName();
        String fullname = data.getFullName();
        String mobileno = data.getMobile();
        String emailacc = data.getEmail();
        String pass = newPass.getText().toString();

        // Retrofit Initialization
        ApiService apiService = SignupActivity.ApiClient.getRetrofitInstance(EditPasswordActivity.this).create(ApiService.class);

// Data to update
        JsonObject userData = new JsonObject();
        userData.addProperty("id", data.getId()); // Required ID of the user
        userData.addProperty("shop_name", shopname);
        userData.addProperty("full_name", fullname);
        userData.addProperty("mobile", mobileno);
        userData.addProperty("email", emailacc);
        userData.addProperty("password", pass);

// API Call
        Call<JsonObject> call = apiService.updateUser(userData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseData = response.body();
                    String status = responseData.get("status").getAsString();
                    String message = responseData.get("message").getAsString();
                    Log.d("API Response", "Status: " + status + ", Message: " + message);
                    Toast.makeText(EditPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Log.e("API Error", "Response Code: " + response.code());
                    JsonObject responseData = response.body();
                    assert responseData != null;
                    String message = responseData.get("message").getAsString();
                    Toast.makeText(EditPasswordActivity.this, "Failed "+ message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API Failure", "Error: " + t.getMessage());
                Toast.makeText(EditPasswordActivity.this, "Error "+ t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
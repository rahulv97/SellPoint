package com.sellpoint.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.sellpoint.R;
import com.sellpoint.models.ProfileResponse;
import com.sellpoint.models.User;
import com.sellpoint.models.UsersModel;
import com.sellpoint.services.ApiService;
import com.sellpoint.models.SignupResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    ImageView back_btn, profileImg;
    CheckBox passCheck;

    EditText etPass, shopName, fullName, mobile, email;

    Button signupBtn;

    TextView profileHead, expiryDate, expDateHead;

    String opration = "";

    ProgressDialog progressDialog;

    ProfileResponse.Data data;

    UsersModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        opration = getIntent().getStringExtra("NewOrEdit");
        init();
        onClick();

        if (opration.equals("Edit")){
            assignValues();
        }

        if (opration.equals("Admin")){
            profileHead.setText("Edit Profile");
            etPass.setVisibility(View.VISIBLE);
            passCheck.setVisibility(View.VISIBLE);
            expiryDate.setVisibility(View.VISIBLE);
            expDateHead.setVisibility(View.VISIBLE);
            signupBtn.setText("Submit");
            mobile.setEnabled(false);

            profileImg.setImageDrawable(getResources().getDrawable(R.drawable.edit_prof_img));

            user = getIntent().getParcelableExtra("UserData");

            shopName.setText(user.getShopName());
            fullName.setText(user.getOwnerName());
            mobile.setText(user.getMobile());
            email.setText(user.getEmail());
            etPass.setText(user.getPassword());
            expiryDate.setText(user.getExpiryDate());
        }else {
            expiryDate.setVisibility(View.GONE);
            expDateHead.setVisibility(View.GONE);
        }

    }

    void init(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        back_btn = findViewById(R.id.back_btn);
        expiryDate = findViewById(R.id.expiryDate);
        expDateHead = findViewById(R.id.expDateHead);
        passCheck = findViewById(R.id.passCheck);
        etPass = findViewById(R.id.etPass);
        signupBtn = findViewById(R.id.signupBtn);
        profileHead = findViewById(R.id.profileHead);
        profileImg = findViewById(R.id.profileImg);

        shopName = findViewById(R.id.shopName);
        fullName = findViewById(R.id.fullName);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);



    }

    private void showDatePickerDialog() {
        // Get current date to set as default in DatePicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                // Format the selected date
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(selectedDate.getTime());

                // Set the formatted date to the TextView
                expiryDate.setText(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    void onClick(){

        expiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        passCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPass.setSelection(etPass.getText().length());
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shopname = shopName.getText().toString().trim();
                String fullname = fullName.getText().toString().trim();
                String mobileno = mobile.getText().toString().trim();
                String emailacc = email.getText().toString().trim();
                String pass = etPass.getText().toString().trim();

                if (
                        shopname.isEmpty() ||
                        fullname.isEmpty() ||
                        mobileno.isEmpty() ||
                        emailacc.isEmpty() ||
                        pass.isEmpty()
                ){
                    Toast.makeText(SignupActivity.this, "All Fields are mandatory!!", Toast.LENGTH_LONG).show();
                } else {
                    if (opration.equals("Edit") || opration.equals("Admin")){
                        updateProfile();
                    } else {
                        signup();
                    }


                }

            }
        });
    }

    static class ApiClient {
        private static Retrofit retrofit;

        public static Retrofit getRetrofitInstance(Context context) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(context.getResources().getString(R.string.BASE_URL))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    void assignValues(){
        profileHead.setText("Edit Profile");
        etPass.setVisibility(View.GONE);
        passCheck.setVisibility(View.GONE);
        signupBtn.setText("Submit");
        mobile.setEnabled(false);

        profileImg.setImageDrawable(getResources().getDrawable(R.drawable.edit_prof_img));

        data = getIntent().getParcelableExtra("Values");

        shopName.setText(data.getShopName());
        fullName.setText(data.getFullName());
        mobile.setText(data.getMobile());
        email.setText(data.getEmail());
        etPass.setText(data.getPassword());
        expiryDate.setText(data.getExpiry_date());

    }

    void signup(){
        String shopname = shopName.getText().toString().trim();
        String fullname = fullName.getText().toString().trim();
        String mobileno = mobile.getText().toString().trim();
        String emailacc = email.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        progressDialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 2);

        // Get the updated date
        Date updatedDate = calendar.getTime();

        User user = new User(
                shopname,
                fullname,
                mobileno,
                emailacc,
                pass, currentDate, sdf.format(updatedDate)
        );

        // Get the API service instance
        ApiService apiService = ApiClient.getRetrofitInstance(SignupActivity.this).create(ApiService.class);

        // Call the API
        Call<SignupResponse> call = apiService.createUser(user);
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    String message = response.body().getMessage();

                    if ("success".equals(status)) {
                        Toast.makeText(SignupActivity.this, "Signup Successful: " + message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Signup Failed "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateProfile(){

        progressDialog.show();

        String shopname = shopName.getText().toString().trim();
        String fullname = fullName.getText().toString().trim();
        String mobileno = mobile.getText().toString().trim();
        String emailacc = email.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        // Retrofit Initialization
        ApiService apiService = ApiClient.getRetrofitInstance(SignupActivity.this).create(ApiService.class);

// Data to update
        JsonObject userData = new JsonObject();

        if (opration.equals("Admin")){
            userData.addProperty("id", user.getId());
        }else {
            userData.addProperty("id", data.getId());
        }

        userData.addProperty("shop_name", shopname);
        userData.addProperty("full_name", fullname);
        userData.addProperty("mobile", mobileno);
        userData.addProperty("email", emailacc);
        userData.addProperty("password", pass);
        userData.addProperty("expiry_date", expiryDate.getText().toString());

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
                    Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Log.e("API Error", "Response Code: " + response.code());
                    JsonObject responseData = response.body();
                    assert responseData != null;
                    String message = responseData.get("message").getAsString();
                    Toast.makeText(SignupActivity.this, "Failed "+ message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("API Failure", "Error: " + t.getMessage());
                Toast.makeText(SignupActivity.this, "Error "+ t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
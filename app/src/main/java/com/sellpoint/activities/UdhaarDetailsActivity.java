package com.sellpoint.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.sellpoint.R;
import com.sellpoint.models.UdhaarModel;
import com.sellpoint.services.ApiService;
import com.sellpoint.services.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UdhaarDetailsActivity extends AppCompatActivity {

    UdhaarModel udhaarModel;

    String reqType;
    ImageView back_btn, person_image, id_proof;

    SharedPreferences sharedPreferences;

    int PERSON_IMG_REQ = 11;
    int ID_PROOF_REQ = 12;

    File person_img_file, id_proof_file;

    EditText etPersonName, etAmount, etDescription, etAddress, etMobile;

    TextView returnDate;

    Button submitBtn;

    ProgressDialog progressDialog;

    Switch paidSwtich;

    String isPaid = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_udhaar_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        udhaarModel = getIntent().getParcelableExtra("UdhaarData");

        init();
        onClick();
        assignValues();

    }

    void init(){
        progressDialog = new ProgressDialog(UdhaarDetailsActivity.this);
        progressDialog.setMessage("Uploading...");
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        progressDialog.setCancelable(false);
        back_btn = findViewById(R.id.back_btn);
        paidSwtich = findViewById(R.id.paidSwtich);
        person_image = findViewById(R.id.person_image);
        id_proof = findViewById(R.id.id_proof);
        returnDate = findViewById(R.id.returnDate);
        etPersonName = findViewById(R.id.etPersonName);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        etMobile = findViewById(R.id.etMobile);
        submitBtn = findViewById(R.id.submitBtn);
        reqType = getIntent().getStringExtra("ReqType");

        if (reqType.equals("paid")){
            paidSwtich.setChecked(true);
            paidSwtich.setEnabled(false);
        }

    }

    void onClick(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        person_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PERSON_IMG_REQ);
            }
        });

        id_proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(ID_PROOF_REQ);
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        paidSwtich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isPaid = "1";
                }else {
                    isPaid = "0";
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPersonName.getText().toString().isEmpty() ||
                        etAmount.getText().toString().isEmpty() ||
                        etDescription.getText().toString().isEmpty() ||
                        etAddress.getText().toString().isEmpty() ||
                        etMobile.getText().toString().isEmpty() || returnDate.getText().toString().equals("Return Date")){
                    Toast.makeText(UdhaarDetailsActivity.this, "All Fields are required!", Toast.LENGTH_LONG).show();
                }
                else {
                    updateData();
                }
            }
        });

    }

    private void selectImage(int request) {
        CharSequence[] options = {"View Full Size Image", "Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Option");

        builder.setItems(options, (dialog, which) -> {
            if (options[which].equals("Capture Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    Log.e("MyCameraError", "captureImage: FileCreated");
                } catch (IOException e) {
                    Log.e("MyCameraError", "captureImage: "+e.toString());
                    e.printStackTrace();

                }

                if (photoFile != null) {
                    try {
                        capturedImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        startActivityForResult(intent, request);
                    }catch (Exception e){
                        Log.e("MyCameraError1", "captureImage: "+e.toString());
                        e.printStackTrace();
                    }

                }
            } else if (options[which].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, request);
            }else if (options[which].equals("View Full Size Image")) {
                if (request == PERSON_IMG_REQ){
                    Intent intent = new Intent(UdhaarDetailsActivity.this, ViewPhotoActivity.class);
                    intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+udhaarModel.getPerson_image());
                    startActivity(intent);
                }
                if (request == ID_PROOF_REQ){
                    Intent intent = new Intent(UdhaarDetailsActivity.this, ViewPhotoActivity.class);
                    intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+udhaarModel.getId_proof());
                    startActivity(intent);
                }
            } else {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private Uri capturedImageUri;




    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // App-specific storage
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri fileUri = null;

            // If from Camera
            if (data == null && capturedImageUri != null) {
                fileUri = capturedImageUri;
            }

            // If from Gallery
            if (data != null && data.getData() != null) {
                fileUri = data.getData();
            }

            if (fileUri != null) {
                String imagePath = FileUtils.getPath(this, fileUri); // Custom method to resolve path
                Log.d("CheckImgPath", "onActivityResult: "+imagePath);

                if (requestCode == PERSON_IMG_REQ) {
                    person_img_file = new File(imagePath);
                    Glide.with(this).load(person_img_file).into(person_image);
                } else if (requestCode == ID_PROOF_REQ) {
                    id_proof_file = new File(imagePath);
                    Glide.with(this).load(id_proof_file).into(id_proof);
                }
            }
        }
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
                returnDate.setText(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    void assignValues(){
        etPersonName.setText(udhaarModel.getPerson_name());
        etAmount.setText(udhaarModel.getPurchase_price());
        etDescription.setText(udhaarModel.getDescription());
        etAddress.setText(udhaarModel.getAddress());
        etMobile.setText(udhaarModel.getMobile());
        returnDate.setText(udhaarModel.getReturn_date());
        Glide.with(UdhaarDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+udhaarModel.getPerson_image()).into(person_image);
        Glide.with(UdhaarDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+udhaarModel.getId_proof()).into(id_proof);
        isPaid = udhaarModel.getStatus();
        if (isPaid.equals("0")){
            paidSwtich.setChecked(false);
        }
        if (isPaid.equals("1")){
            paidSwtich.setChecked(true);
        }
    }

    void updateData(){

        progressDialog.show();

        ApiService apiService = NewPurchaseActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), udhaarModel.getId());
        RequestBody person_name = RequestBody.create(MediaType.parse("text/plain"), etPersonName.getText().toString());
        RequestBody purchase_price = RequestBody.create(MediaType.parse("text/plain"), etAmount.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etDescription.getText().toString());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), etMobile.getText().toString());
        RequestBody return_date = RequestBody.create(MediaType.parse("text/plain"), returnDate.getText().toString());
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), isPaid);


        MultipartBody.Part person_image = null;
        if (person_img_file != null) {
            File file = new File(person_img_file.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            person_image = MultipartBody.Part.createFormData("person_image", file.getName(), requestFile);
        }

        MultipartBody.Part id_proof = null;
        if (id_proof_file != null) {
            File file = new File(id_proof_file.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            id_proof = MultipartBody.Part.createFormData("id_proof", file.getName(), requestFile);
        }



        Call<ResponseBody> call = apiService.updateUdhaar(
                id, person_name, purchase_price, description, address, mobile, return_date,
                status, person_image, id_proof
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    finish();
                    Toast.makeText(UdhaarDetailsActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UdhaarDetailsActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UdhaarDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
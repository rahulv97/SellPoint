package com.sellpoint.activities;

import static com.sellpoint.services.Utils.createPartFromString;
import static com.sellpoint.services.Utils.prepareFilePart;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sellpoint.services.ApiResponse;
import com.sellpoint.services.ApiService;
import com.sellpoint.services.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUdhaarEntryActivity extends AppCompatActivity {

    ImageView back_btn, person_image, id_proof;

    SharedPreferences sharedPreferences;

    int PERSON_IMG_REQ = 11;
    int ID_PROOF_REQ = 12;

    File person_img_file, id_proof_file;

    EditText etPersonName, etAmount, etDescription, etAddress, etMobile;

    TextView returnDate;

    Button submitBtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_udhaar_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        onClick();
    }

    void init(){
        progressDialog = new ProgressDialog(NewUdhaarEntryActivity.this);
        progressDialog.setMessage("Uploading...");
        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);
        progressDialog.setCancelable(false);
        back_btn = findViewById(R.id.back_btn);
        person_image = findViewById(R.id.person_image);
        id_proof = findViewById(R.id.id_proof);
        returnDate = findViewById(R.id.returnDate);
        etPersonName = findViewById(R.id.etPersonName);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        etMobile = findViewById(R.id.etMobile);
        submitBtn = findViewById(R.id.submitBtn);
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPersonName.getText().toString().isEmpty() ||
                etAmount.getText().toString().isEmpty() ||
                etDescription.getText().toString().isEmpty() ||
                etAddress.getText().toString().isEmpty() ||
                etMobile.getText().toString().isEmpty() || returnDate.getText().toString().equals("Return Date")){
                    Toast.makeText(NewUdhaarEntryActivity.this, "All Fields are required!", Toast.LENGTH_LONG).show();
                }
                else {
                    uploadData();
                }
            }
        });

    }

    private void selectImage(int request) {
        CharSequence[] options = {"Capture Photo", "Choose from Gallery", "Cancel"};

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

    private void uploadData() {

        progressDialog.show();

        ApiService apiService = NewPurchaseActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        MultipartBody.Part person_image_part = null;

        if (person_img_file!=null){
            person_image_part = prepareFilePart("person_image", person_img_file);
        }

        MultipartBody.Part id_proof_part = null;
        if (id_proof_file!=null){
            id_proof_part = prepareFilePart("id_proof", id_proof_file);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Call<ApiResponse> call = apiService.createUdhaar(
                createPartFromString(sharedPreferences.getString("Mobile", "")),
                createPartFromString(etPersonName.getText().toString()),
                createPartFromString(etAmount.getText().toString()),
                createPartFromString(etDescription.getText().toString()),
                createPartFromString(etAddress.getText().toString()),
                createPartFromString(etMobile.getText().toString()),
                createPartFromString(returnDate.getText().toString()),
                createPartFromString(currentDate),
                createPartFromString("0"),
                person_image_part,
                id_proof_part
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NewUdhaarEntryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewUdhaarEntryActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    Log.d("UploadResp", "onResponse: "+response.code());
                    Log.d("UploadResp", "onResponse: "+response.message());
                    Log.d("UploadResp", "onResponse: "+response.body());
                    Log.d("UploadResp", "onResponse: "+response.errorBody());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(NewUdhaarEntryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

}
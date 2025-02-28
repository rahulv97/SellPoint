package com.sellpoint.activities;

import static com.sellpoint.services.Utils.createPartFromString;
import static com.sellpoint.services.Utils.prepareFilePart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
//import com.github.gcacace.signaturepad.views.SignaturePad;
import com.sellpoint.R;
import com.sellpoint.models.ItemRequest;
import com.sellpoint.models.ItemsModel;
import com.sellpoint.services.ApiResponse;
import com.sellpoint.services.ApiService;
import com.sellpoint.services.FileUtils;
import com.sellpoint.services.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.warting.signatureview.views.SignaturePad;

public class NewPurchaseActivity extends AppCompatActivity {

    Spinner categoriesSpinner;
    ImageView back_btn, imgBillPic, imgProof1, imgProof2, imgSeller;

    //SignaturePad signaturePad;
    TextView resetSign, warrantyDate, signText, purchaseHead;

    RelativeLayout signRL;

    SharedPreferences sharedPreferences;

    private static final int BILL_IMG_REQ = 1;
    private static final int PROOF_1_IMG_REQ = 2;
    private static final int PROOF_2_IMG_REQ = 3;
    private static final int SELLER_IMG_REQ = 4;
    private File selectedBillPhoto, selectedIdProof1, selectedIdProof2, selectedSellerPhoto, selectedSignaturePhoto;

    SignaturePad signaturePad;


    EditText etItemname, etProductPrice, etDescription, etSellerName, etSellerMobile, etSellerAltMobile, etSellerEmail,
            sellerRemarks, uid1, uid2;

    AppCompatButton submitBtn;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_purchase);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String newOrEdit = getIntent().getStringExtra("NewOrEdit");

        sharedPreferences = getSharedPreferences("SellPointPrefs", MODE_PRIVATE);

        progressDialog = new ProgressDialog(NewPurchaseActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);

        categoriesSpinner = findViewById(R.id.categoriesSpinner);
        back_btn = findViewById(R.id.back_btn);

        warrantyDate = findViewById(R.id.warrantyDate);
        etItemname = findViewById(R.id.etItemname);
        etProductPrice = findViewById(R.id.etProductPrice);
        etDescription = findViewById(R.id.etDescription);
        etSellerName = findViewById(R.id.etSellerName);
        etSellerMobile = findViewById(R.id.etSellerMobile);
        etSellerAltMobile = findViewById(R.id.etSellerAltMobile);
        etSellerEmail = findViewById(R.id.etSellerEmail);
        sellerRemarks = findViewById(R.id.sellerRemarks);
        imgBillPic = findViewById(R.id.imgBillPic);
        imgProof1 = findViewById(R.id.imgProof1);
        imgProof2 = findViewById(R.id.imgProof2);
        imgSeller = findViewById(R.id.imgSeller);
        submitBtn = findViewById(R.id.submitBtn);
        signText = findViewById(R.id.signText);
        signRL = findViewById(R.id.signRL);
        purchaseHead = findViewById(R.id.purchaseHead);
        uid1 = findViewById(R.id.uid1);
        uid2 = findViewById(R.id.uid2);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(adapter);

        //signaturePad = findViewById(R.id.signaturePad);
        resetSign = findViewById(R.id.resetSign);



        signaturePad = findViewById(R.id.signaturePad);


        resetSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        imgBillPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(BILL_IMG_REQ);
            }
        });

        imgProof1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PROOF_1_IMG_REQ);
            }
        });

        imgProof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PROOF_2_IMG_REQ);
            }
        });

        imgSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(SELLER_IMG_REQ);
            }
        });

        warrantyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveSignatureAsImage();
                if (
                        etItemname.getText().toString().isEmpty() ||
                                etProductPrice.getText().toString().isEmpty() ||
                                etSellerName.getText().toString().isEmpty() ||
                                etSellerMobile.getText().toString().isEmpty() ||
                                warrantyDate.getText().toString().equals("Warranty Date") ||
                                categoriesSpinner.getSelectedItem().toString().equals("Select Category")
                ){
                    Toast.makeText(NewPurchaseActivity.this, "All Fields are mandatory!!", Toast.LENGTH_LONG).show();
                } else {

                    if (newOrEdit.equals("Edit")){
                        updateData();
                    }else {
                        saveSignatureAsImage();
                        uploadData();
                    }

                }

            }
        });




        if (newOrEdit.equals("Edit")){
            assignValues();
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
                warrantyDate.setText(formattedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    public void saveSignatureAsImage() {
        long currentTimeMillis = System.currentTimeMillis();
        /*File file = new File(Environment.getExternalStorageDirectory()+"/Download", currentTimeMillis+"signature.png");
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("SignatureFilePath", "saveSignatureAsImage: "+file.getAbsolutePath());
            selectedSignaturePhoto = file;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), currentTimeMillis+"signature.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            selectedSignaturePhoto = file;
            Log.d("SignPath", "Signature saved to: " + file.getAbsolutePath());
            //Toast.makeText(this, "Signature saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving signature", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void selectImage(int request) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select an Image"), request);
    }*/

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


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri fileUri = data.getData();

        Log.d("SelectedFilePath", requestCode+" onActivityResult: "+FileUtils.getPath(this, data.getData()));

        if (requestCode == BILL_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedBillPhoto = new File(imagePath); // Assign the resolved file path
            Glide.with(NewPurchaseActivity.this).load(selectedBillPhoto).into(imgBillPic);
            billPhotoUri = fileUri;
        }

        if (requestCode == PROOF_1_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedIdProof1 = new File(imagePath); // Assign the resolved file path
            Glide.with(NewPurchaseActivity.this).load(fileUri).into(imgProof1);
            idProof1Uri = fileUri;
        }

        if (requestCode == PROOF_2_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedIdProof2 = new File(imagePath); // Assign the resolved file path
            Glide.with(NewPurchaseActivity.this).load(fileUri).into(imgProof2);
            idProof2Uri = fileUri;
        }

        if (requestCode == SELLER_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedSellerPhoto = new File(imagePath); // Assign the resolved file path
            Glide.with(NewPurchaseActivity.this).load(fileUri).into(imgSeller);
            sellerPhotoUri = fileUri;
        }
    }*/

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

                if (requestCode == BILL_IMG_REQ) {
                    selectedBillPhoto = new File(imagePath);
                    Glide.with(this).load(selectedBillPhoto).into(imgBillPic);
                } else if (requestCode == PROOF_1_IMG_REQ) {
                    selectedIdProof1 = new File(imagePath);
                    Glide.with(this).load(selectedIdProof1).into(imgProof1);
                } else if (requestCode == PROOF_2_IMG_REQ) {
                    selectedIdProof2 = new File(imagePath);
                    Glide.with(this).load(selectedIdProof2).into(imgProof2);
                } else if (requestCode == SELLER_IMG_REQ) {
                    selectedSellerPhoto = new File(imagePath);
                    Glide.with(this).load(selectedSellerPhoto).into(imgSeller);
                }
            }
        }
    }



    static class RetrofitClient {
        private static Retrofit retrofit = null;

        public static Retrofit getClient(String BASE_URL) {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    private void uploadData() {

        progressDialog.show();

        ApiService apiService = RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        MultipartBody.Part billPhotoPart = null;

        if (selectedBillPhoto!=null){
            billPhotoPart = prepareFilePart("bill_photo", selectedBillPhoto);
        }

        MultipartBody.Part idProof1Part = null;
        if (selectedIdProof1!=null){
            idProof1Part = prepareFilePart("id_proof_1", selectedIdProof1);
        }


        MultipartBody.Part idProof2Part = null;
        if (selectedIdProof2!=null){
            idProof2Part = prepareFilePart("id_proof_2", selectedIdProof2);
        }


        MultipartBody.Part sellerPhotoPart = null;
        if (selectedSellerPhoto!=null){
            sellerPhotoPart = prepareFilePart("seller_photo", selectedSellerPhoto);
        }


        MultipartBody.Part signaturePhotoPart = null;
        if (selectedSignaturePhoto!=null){
            signaturePhotoPart = prepareFilePart("signature_photo", selectedSignaturePhoto);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Call<ApiResponse> call = apiService.addItem(
                createPartFromString(sharedPreferences.getString("Mobile", "")),
                createPartFromString(categoriesSpinner.getSelectedItem().toString()),
                createPartFromString(etItemname.getText().toString()),
                createPartFromString(etProductPrice.getText().toString()),
                createPartFromString(etDescription.getText().toString()),
                createPartFromString(etSellerName.getText().toString()),
                createPartFromString(etSellerMobile.getText().toString()),
                createPartFromString(etSellerAltMobile.getText().toString()),
                createPartFromString(etSellerEmail.getText().toString()),
                createPartFromString(sellerRemarks.getText().toString()),
                createPartFromString(warrantyDate.getText().toString()),
                createPartFromString(currentDate),
                createPartFromString(uid1.getText().toString()),
                createPartFromString(uid2.getText().toString()),
                createPartFromString("0"),
                billPhotoPart,
                idProof1Part,
                idProof2Part,
                sellerPhotoPart,
                signaturePhotoPart
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NewPurchaseActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewPurchaseActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NewPurchaseActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    ItemsModel itemsModel;

    void assignValues(){

        signText.setVisibility(View.GONE);
        signRL.setVisibility(View.GONE);
        purchaseHead.setText("Edit Details");

        itemsModel = getIntent().getParcelableExtra("Values");

        ArrayList<String> catTemp = new ArrayList<>();
        Collections.addAll(catTemp, getResources().getStringArray(R.array.categories));

        categoriesSpinner.setSelection(catTemp.indexOf(itemsModel.getCategory()));
        warrantyDate.setText(itemsModel.getWarrantyDate());
        etItemname.setText(itemsModel.getItemName());
        etProductPrice.setText(itemsModel.getPurchasePrice());
        etDescription.setText(itemsModel.getItemDescription());
        etSellerName.setText(itemsModel.getSellerName());
        etSellerMobile.setText(itemsModel.getSellerMobile());
        etSellerAltMobile.setText(itemsModel.getSellerAltMobile());
        etSellerEmail.setText(itemsModel.getSellerEmail());
        sellerRemarks.setText(itemsModel.getSellerRemarks());
        uid1.setText(itemsModel.getUid1());
        uid2.setText(itemsModel.getUid2());


        Glide.with(NewPurchaseActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getBillPhoto())
                        .into(imgBillPic);

        Glide.with(NewPurchaseActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof1())
                .into(imgProof1);

        Glide.with(NewPurchaseActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof2())
                .into(imgProof2);

        Glide.with(NewPurchaseActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getSellerPic())
                .into(imgSeller);



    }

    void updateData(){

        progressDialog.show();

        ApiService apiService = RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), itemsModel.getId());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString("Mobile", ""));
// Repeat for other fields as needed.
        RequestBody category = RequestBody.create(MediaType.parse("text/plain"), categoriesSpinner.getSelectedItem().toString());
        RequestBody itemname = RequestBody.create(MediaType.parse("text/plain"), etItemname.getText().toString());
        RequestBody purprice = RequestBody.create(MediaType.parse("text/plain"), etProductPrice.getText().toString());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etDescription.getText().toString());
        RequestBody sellername = RequestBody.create(MediaType.parse("text/plain"), etSellerName.getText().toString());
        RequestBody sellermob = RequestBody.create(MediaType.parse("text/plain"), etSellerMobile.getText().toString());
        RequestBody selleraltmob = RequestBody.create(MediaType.parse("text/plain"), etSellerAltMobile.getText().toString());
        RequestBody selleremail = RequestBody.create(MediaType.parse("text/plain"), etSellerEmail.getText().toString());
        RequestBody remarks = RequestBody.create(MediaType.parse("text/plain"), sellerRemarks.getText().toString());
        RequestBody wardate = RequestBody.create(MediaType.parse("text/plain"), warrantyDate.getText().toString());
        RequestBody uid1Body = RequestBody.create(MediaType.parse("text/plain"), uid1.getText().toString());
        RequestBody uid2Body = RequestBody.create(MediaType.parse("text/plain"), uid2.getText().toString());

        MultipartBody.Part billPhoto = null;
        if (selectedBillPhoto != null) {
            File file = new File(selectedBillPhoto.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            billPhoto = MultipartBody.Part.createFormData("bill_photo", file.getName(), requestFile);
        }

        MultipartBody.Part idproof1 = null;
        if (selectedIdProof1 != null) {
            File file = new File(selectedIdProof1.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            idproof1 = MultipartBody.Part.createFormData("id_proof_1", file.getName(), requestFile);
        }

        MultipartBody.Part idproof2 = null;
        if (selectedIdProof2 != null) {
            File file = new File(selectedIdProof2.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            idproof2 = MultipartBody.Part.createFormData("id_proof_2", file.getName(), requestFile);
        }

        MultipartBody.Part sellerPic = null;
        if (selectedSellerPhoto != null) {
            File file = new File(selectedSellerPhoto.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            sellerPic = MultipartBody.Part.createFormData("seller_photo", file.getName(), requestFile);
        }

        MultipartBody.Part signpic = null;
        if (selectedSignaturePhoto != null) {
            File file = new File(selectedSignaturePhoto.getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            signpic = MultipartBody.Part.createFormData("signature_photo", file.getName(), requestFile);
        }

// Repeat for other files.

        Call<ResponseBody> call = apiService.updateItem(
                id, mobile, category, itemname, purprice, description, sellername,
                sellermob, selleraltmob, selleremail, remarks, wardate, uid1Body,
                uid2Body, billPhoto, idproof1, idproof2, sellerPic, signpic
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    finish();
                    Toast.makeText(NewPurchaseActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(NewPurchaseActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(NewPurchaseActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
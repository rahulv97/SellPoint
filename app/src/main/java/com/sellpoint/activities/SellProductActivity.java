package com.sellpoint.activities;

import static com.sellpoint.services.Utils.createPartFromString;
import static com.sellpoint.services.Utils.prepareFilePart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sellpoint.R;
import com.sellpoint.models.ItemsModel;
import com.sellpoint.services.ApiResponse;
import com.sellpoint.services.ApiService;
import com.sellpoint.services.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellProductActivity extends AppCompatActivity {

    ImageView back_btn;
    ProgressDialog progressDialog;

    EditText etBuyerName, etBuyerMob, etBuyerAltName, etBuyerEmail, etSellingPrice, etBuyerNote;
    ImageView etBuyerID1, etBuyerID2, buyerImg;
    AppCompatButton submitBtn;

    private File selectedBillPhoto, selectedIdProof1, selectedIdProof2, selectedSellerPhoto, selectedSignaturePhoto;


    private static final int BILL_IMG_REQ = 1;
    private static final int PROOF_1_IMG_REQ = 2;
    private static final int PROOF_2_IMG_REQ = 3;

    TextView saleHead;

    String opration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sell_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        onClick();

        if (opration.equals("Edit")){
            assignValues();
        }

    }

    void init(){
        opration = getIntent().getStringExtra("NewOrEdit");
        back_btn = findViewById(R.id.back_btn);

        saleHead = findViewById(R.id.saleHead);
        etBuyerName = findViewById(R.id.etBuyerName);
        etBuyerMob = findViewById(R.id.etBuyerMob);
        etBuyerAltName = findViewById(R.id.etBuyerAltName);
        etBuyerEmail = findViewById(R.id.etBuyerEmail);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        etBuyerNote = findViewById(R.id.etBuyerNote);
        etBuyerID1 = findViewById(R.id.etBuyerID1);
        etBuyerID2 = findViewById(R.id.etBuyerID2);
        buyerImg = findViewById(R.id.buyerImg);
        submitBtn = findViewById(R.id.submitBtn);

        progressDialog = new ProgressDialog(SellProductActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    void onClick(){
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etBuyerID1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PROOF_1_IMG_REQ);
            }
        });

        etBuyerID2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(PROOF_2_IMG_REQ);
            }
        });

        buyerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(BILL_IMG_REQ);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBuyerName.getText().toString().isEmpty() || etBuyerMob.getText().toString().isEmpty() ||
                        etSellingPrice.getText().toString().isEmpty()
                ){
                    Toast.makeText(SellProductActivity.this, "All fields are mandatory!!", Toast.LENGTH_LONG).show();
                }else {
                    if (opration.equals("Edit")){
                        updateItem();
                    }else {
                        uploadData();
                    }

                }
            }
        });



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
                    Glide.with(this).load(selectedBillPhoto).into(buyerImg);
                } else if (requestCode == PROOF_1_IMG_REQ) {
                    selectedIdProof1 = new File(imagePath);
                    Glide.with(this).load(selectedIdProof1).into(etBuyerID1);
                } else if (requestCode == PROOF_2_IMG_REQ) {
                    selectedIdProof2 = new File(imagePath);
                    Glide.with(this).load(selectedIdProof2).into(etBuyerID2);
                }
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri fileUri = data.getData();

        Log.d("SelectedFilePath", requestCode+" onActivityResult: "+ FileUtils.getPath(this, data.getData()));

        if (requestCode == BILL_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedBillPhoto = new File(imagePath); // Assign the resolved file path
            Glide.with(SellProductActivity.this).load(fileUri).into(buyerImg);
            billPhotoUri = fileUri;
        }

        if (requestCode == PROOF_1_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedIdProof1 = new File(imagePath); // Assign the resolved file path
            Glide.with(SellProductActivity.this).load(fileUri).into(etBuyerID1);
            idProof1Uri = fileUri;
        }

        if (requestCode == PROOF_2_IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String imagePath = FileUtils.getPath(this, data.getData()); // Now this will work
            selectedIdProof2 = new File(imagePath); // Assign the resolved file path
            Glide.with(SellProductActivity.this).load(fileUri).into(etBuyerID2);
            idProof2Uri = fileUri;
        }


    }*/


    private void uploadData() {

        progressDialog.show();

        ApiService apiService = NewPurchaseActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        MultipartBody.Part billPhotoPart = null;

        if (selectedBillPhoto!=null){
            billPhotoPart = prepareFilePart("buyer_photo", selectedBillPhoto);
        }

        MultipartBody.Part idProof1Part = null;
        if (selectedIdProof1!=null){
            idProof1Part = prepareFilePart("buyer_proof1", selectedIdProof1);
        }


        MultipartBody.Part idProof2Part = null;
        if (selectedIdProof2!=null){
            idProof2Part = prepareFilePart("buyer_proof2", selectedIdProof2);
        }




        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Call<ResponseBody> call = apiService.updateItem(
                createPartFromString(getIntent().getStringExtra("ID")),
                createPartFromString(etBuyerName.getText().toString()),
                createPartFromString(etBuyerMob.getText().toString()),
                createPartFromString(etBuyerAltName.getText().toString()),
                createPartFromString(etBuyerEmail.getText().toString()),
                createPartFromString(""),
                createPartFromString(etBuyerNote.getText().toString()),
                createPartFromString(currentDate),
                createPartFromString(etSellingPrice.getText().toString()),
                createPartFromString("1"),
                idProof1Part,
                idProof2Part,
                billPhotoPart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SellProductActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SellProductActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    Log.d("UploadResp", "onResponse: "+response.code());
                    Log.d("UploadResp", "onResponse: "+response.message());
                    Log.d("UploadResp", "onResponse: "+response.body());
                    Log.d("UploadResp", "onResponse: "+response.errorBody());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SellProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public void updateItem() {

        progressDialog.show();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());


        RequestBody itemIdBody = RequestBody.create(MediaType.parse("text/plain"), getIntent().getStringExtra("ID"));
        RequestBody buyerNameBody = RequestBody.create(MediaType.parse("text/plain"), etBuyerName.getText().toString());
        RequestBody buyerMobBody = RequestBody.create(MediaType.parse("text/plain"), etBuyerMob.getText().toString());
        RequestBody buyerAltMobBody = RequestBody.create(MediaType.parse("text/plain"), etBuyerAltName.getText().toString());
        RequestBody buyerEmailBody = RequestBody.create(MediaType.parse("text/plain"), etBuyerEmail.getText().toString());
        RequestBody buyerAddressBody = RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody buyerNoteBody = RequestBody.create(MediaType.parse("text/plain"), etBuyerNote.getText().toString());
        RequestBody sellingDateBody = RequestBody.create(MediaType.parse("text/plain"), currentDate);
        RequestBody sellPriceBody = RequestBody.create(MediaType.parse("text/plain"), etSellingPrice.getText().toString());
        RequestBody expiry_date = RequestBody.create(MediaType.parse("text/plain"), etSellingPrice.getText().toString());

        MultipartBody.Part proof1Part = selectedIdProof1 != null ? MultipartBody.Part.createFormData(
                "buyer_proof1", selectedIdProof1.getName(), RequestBody.create(MediaType.parse("image/*"), selectedIdProof1)) : null;

        MultipartBody.Part proof2Part = selectedIdProof2 != null ? MultipartBody.Part.createFormData(
                "buyer_proof2", selectedIdProof2.getName(), RequestBody.create(MediaType.parse("image/*"), selectedIdProof2)) : null;

        MultipartBody.Part photoPart = selectedBillPhoto != null ? MultipartBody.Part.createFormData(
                "buyer_photo", selectedBillPhoto.getName(), RequestBody.create(MediaType.parse("image/*"), selectedBillPhoto)) : null;

        ApiService apiService = NewPurchaseActivity.RetrofitClient.getClient(getResources().getString(R.string.BASE_URL)).create(ApiService.class);

        Call<ResponseBody> call = apiService.updateSeller(
                itemIdBody, buyerNameBody, buyerMobBody, buyerAltMobBody,
                buyerEmailBody, buyerAddressBody, buyerNoteBody, sellingDateBody,
                sellPriceBody, expiry_date, proof1Part, proof2Part, photoPart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Update Successful");
                    Toast.makeText(SellProductActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("API", "Update Failed: " + response.message());
                    progressDialog.dismiss();
                    Toast.makeText(SellProductActivity.this, "Update Failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                Toast.makeText(SellProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    ItemsModel itemsModel;

    void assignValues(){

        itemsModel = getIntent().getParcelableExtra("Values");
        saleHead.setText("Edit Seller");

        etBuyerName.setText(itemsModel.getBuyerName());
        etBuyerMob.setText(itemsModel.getBuyerMobile());
        etBuyerAltName.setText(itemsModel.getBuyerAltMobile());
        etBuyerEmail.setText(itemsModel.getSellerEmail());
        etSellingPrice.setText(itemsModel.getSell_price());
        etBuyerNote.setText(itemsModel.getBuyerNote());

        Glide.with(SellProductActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof1())
                .into(etBuyerID1);

        Glide.with(SellProductActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof2())
                .into(etBuyerID2);

        Glide.with(SellProductActivity.this)
                .load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerPic())
                .into(buyerImg);
    }


}
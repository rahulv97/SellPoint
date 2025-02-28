package com.sellpoint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.sellpoint.R;
import com.sellpoint.models.ItemsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemDetailsActivity extends AppCompatActivity {

    ImageView back_btn, wrImg, sellerPic, idproof1, idproof2, sign, buyerPic, buyer_idproof1, buyer_idproof2, edit_btn, bill;

    TextView sellerTab, buyerTab, itemName, purDate, warDate, purPrice, sellerName, sellerMobile,
            sellerAltMobile, sellerMail, itemDesc, sellerNote, buyerName, buyerPhone, buyerAltPhone,
            buyerEmail, sellingDate, salePrice, buyerNote, uid1, uid2;

    ItemsModel itemsModel;
    LinearLayout sellerInfoLay, buyerInfoLay;

    Button saleBtn1, editSellerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        init();
        setValues();
        onClicks();

    }

    void init(){
        itemsModel = getIntent().getParcelableExtra("ItemData");

        back_btn = findViewById(R.id.back_btn);
        editSellerBtn = findViewById(R.id.editSellerBtn);
        bill = findViewById(R.id.bill);
        edit_btn = findViewById(R.id.edit_btn);
        sellerTab = findViewById(R.id.sellerTab);
        buyerTab = findViewById(R.id.buyerTab);
        sellerInfoLay = findViewById(R.id.sellerInfoLay);
        buyerInfoLay = findViewById(R.id.buyerInfoLay);
        wrImg = findViewById(R.id.wrImg);
        itemName = findViewById(R.id.itemName);
        purDate = findViewById(R.id.purDate);
        warDate = findViewById(R.id.warDate);
        purPrice = findViewById(R.id.purPrice);
        sellerName = findViewById(R.id.sellerName);
        sellerMobile = findViewById(R.id.sellerMobile);
        sellerAltMobile = findViewById(R.id.sellerAltMobile);
        sellerMail = findViewById(R.id.sellerMail);
        itemDesc = findViewById(R.id.itemDesc);
        sellerNote = findViewById(R.id.sellerNote);
        buyerName = findViewById(R.id.buyerName);
        buyerPhone = findViewById(R.id.buyerPhone);
        buyerAltPhone = findViewById(R.id.buyerAltPhone);
        buyerEmail = findViewById(R.id.buyerEmail);
        sellingDate = findViewById(R.id.sellingDate);
        salePrice = findViewById(R.id.salePrice);
        buyerNote = findViewById(R.id.buyerNote);
        saleBtn1 = findViewById(R.id.saleBtn1);
        uid1 = findViewById(R.id.uid1);
        uid2 = findViewById(R.id.uid2);

        sellerPic = findViewById(R.id.sellerPic);
        idproof1 = findViewById(R.id.idproof1);
        idproof2 = findViewById(R.id.idproof2);
        sign = findViewById(R.id.sign);
        buyerPic = findViewById(R.id.buyerPic);
        buyer_idproof1 = findViewById(R.id.buyer_idproof1);
        buyer_idproof2 = findViewById(R.id.buyer_idproof2);
    }

    void setValues(){
        itemName.setText(itemsModel.getItemName());
        purDate.setText("Purchase Date: "+itemsModel.getCreationDate());
        warDate.setText("Warranty Date: "+itemsModel.getWarrantyDate());
        purPrice.setText("₹"+itemsModel.getPurchasePrice());
        sellerName.setText(itemsModel.getSellerName());
        sellerMobile.setText(itemsModel.getSellerMobile());
        sellerAltMobile.setText(itemsModel.getSellerAltMobile());
        sellerMail.setText(itemsModel.getSellerEmail());
        itemDesc.setText(itemsModel.getItemDescription());
        sellerNote.setText(itemsModel.getSellerRemarks());
        buyerName.setText(itemsModel.getBuyerName());
        buyerPhone.setText(itemsModel.getBuyerMobile());
        buyerAltPhone.setText(itemsModel.getBuyerAltMobile());
        buyerEmail.setText(itemsModel.getBuyerEmail());
        sellingDate.setText(itemsModel.getSelling_date());
        salePrice.setText(itemsModel.getSell_price());
        buyerNote.setText(itemsModel.getBuyerNote());
        uid1.setText(itemsModel.getUid1());
        uid2.setText(itemsModel.getUid2());

        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getSellerPic()).into(sellerPic);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof1()).into(idproof1);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof2()).into(idproof2);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getSellerSign()).into(sign);

        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerPic()).into(buyerPic);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof1()).into(buyer_idproof1);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof2()).into(buyer_idproof2);
        Glide.with(ItemDetailsActivity.this).load(getResources().getString(R.string.BASE_URL)+itemsModel.getBillPhoto()).into(bill);

        String inputDateStr = itemsModel.getWarrantyDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        try {
            // Parse input date
            Date inputDate = dateFormat.parse(inputDateStr);
            // Get the current date
            Date currentDate = new Date();

            // Compare dates
            if (inputDate.after(currentDate)) {
                wrImg.setImageDrawable(getResources().getDrawable(R.drawable.warranty_ic));
            } else if (inputDate.before(currentDate)) {
                wrImg.setImageDrawable(getResources().getDrawable(R.drawable.no_warranty_ic));
            } else {
                wrImg.setImageDrawable(getResources().getDrawable(R.drawable.warranty_ic));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (itemsModel.getStatus().equals("0")){
            saleBtn1.setVisibility(View.VISIBLE);
        }else {
            saleBtn1.setVisibility(View.GONE);
        }

    }

    void onClicks(){

        editSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, SellProductActivity.class);
                intent.putExtra("ID", itemsModel.getId());
                intent.putExtra("NewOrEdit", "Edit");
                intent.putExtra("Values", itemsModel);
                startActivity(intent);
                finish();
            }
        });

        saleBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, SellProductActivity.class);
                intent.putExtra("ID", itemsModel.getId());
                intent.putExtra("NewOrEdit", "New");
                startActivity(intent);
                finish();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sellerTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerTab.setBackground(getResources().getDrawable(R.drawable.filled_et_bg));
                sellerTab.setTextColor(getResources().getColor(R.color.white));

                buyerTab.setBackground(getResources().getDrawable(R.drawable.et_bg));
                buyerTab.setTextColor(getResources().getColor(R.color.secondaryColor));

                sellerInfoLay.setVisibility(View.VISIBLE);
                buyerInfoLay.setVisibility(View.GONE);
            }
        });

        buyerTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemsModel.getStatus().equals("0")){
                    Toast.makeText(ItemDetailsActivity.this, "Item is not sold yet!", Toast.LENGTH_LONG).show();
                }else {
                    sellerTab.setBackground(getResources().getDrawable(R.drawable.et_bg));
                    sellerTab.setTextColor(getResources().getColor(R.color.secondaryColor));

                    buyerTab.setBackground(getResources().getDrawable(R.drawable.filled_et_bg));
                    buyerTab.setTextColor(getResources().getColor(R.color.white));

                    sellerInfoLay.setVisibility(View.GONE);
                    buyerInfoLay.setVisibility(View.VISIBLE);
                }


            }
        });

        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getBillPhoto());
                startActivity(intent);
            }
        });
        sellerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getSellerPic());
                startActivity(intent);
            }
        });
        idproof1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof1());
                startActivity(intent);
            }
        });
        idproof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getIdProof2());
                startActivity(intent);
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getSellerSign());
                startActivity(intent);
            }
        });

        buyerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerPic());
                startActivity(intent);
            }
        });
        buyer_idproof1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof1());
                startActivity(intent);
            }
        });
        buyer_idproof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, ViewPhotoActivity.class);
                intent.putExtra("PhotoUrl", getResources().getString(R.string.BASE_URL)+itemsModel.getBuyerProof2());
                startActivity(intent);
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailsActivity.this, NewPurchaseActivity.class);
                intent.putExtra("NewOrEdit", "Edit");
                intent.putExtra("Values", itemsModel);
                startActivity(intent);
                finish();
            }
        });

    }

}
package com.sellpoint.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ItemsModel implements Parcelable {

    String id;
    String category;
    String itemName;
    String creationDate;
    String purchasePrice;
    String itemDescription;
    String warrantyDate;
    String billPhoto;
    String sellerName;
    String sellerMobile;
    String sellerAltMobile;
    String sellerEmail;
    String sellerRemarks;
    String idProof1;
    String idProof2;
    String sellerPic;
    String sellerSign;
    String status;

    String buyerName;
    String buyerMobile;
    String buyerAltMobile;
    String buyerEmail;
    String buyerAddress;
    String buyerNote;
    String buyerProof1;
    String buyerProof2;
    String buyerPic;
    String selling_date;
    String sell_price;

    String uid1;

    String uid2;


    public ItemsModel(String id, String category, String itemName, String creationDate, String purchasePrice,
                      String itemDescription, String warrantyDate, String billPhoto, String sellerName,
                      String sellerMobile, String sellerAltMobile, String sellerEmail, String sellerRemarks,
                      String idProof1, String idProof2, String sellerPic, String sellerSign, String status,
                      String buyerName, String buyerMobile, String buyerAltMobile, String buyerEmail,
                      String buyerAddress, String buyerNote, String buyerProof1, String buyerProof2, String buyerPic,
                      String selling_date, String sell_price, String uid1, String uid2){
        this.id = id;
        this.category = category;
        this.itemName = itemName;
        this.creationDate = creationDate;
        this.purchasePrice = purchasePrice;
        this.itemDescription = itemDescription;
        this.warrantyDate = warrantyDate;
        this.billPhoto = billPhoto;
        this.sellerName = sellerName;
        this.sellerMobile = sellerMobile;
        this.sellerAltMobile = sellerAltMobile;
        this.sellerEmail = sellerEmail;
        this.sellerRemarks = sellerRemarks;
        this.idProof1 = idProof1;
        this.idProof2 = idProof2;
        this.sellerPic = sellerPic;
        this.sellerSign = sellerSign;
        this.status = status;
        this.buyerName = buyerName;
        this.buyerMobile = buyerMobile;
        this.buyerAltMobile = buyerAltMobile;
        this.buyerEmail = buyerEmail;
        this.buyerAddress = buyerAddress;
        this.buyerNote = buyerNote;
        this.buyerProof1 = buyerProof1;
        this.buyerProof2 = buyerProof2;
        this.buyerPic = buyerPic;
        this.selling_date = selling_date;
        this.sell_price = sell_price;
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    protected ItemsModel(Parcel in) {
        id = in.readString();
        category = in.readString();
        itemName = in.readString();
        creationDate = in.readString();
        purchasePrice = in.readString();
        itemDescription = in.readString();
        warrantyDate = in.readString();
        billPhoto = in.readString();
        sellerName = in.readString();
        sellerMobile = in.readString();
        sellerAltMobile = in.readString();
        sellerEmail = in.readString();
        sellerRemarks = in.readString();
        idProof1 = in.readString();
        idProof2 = in.readString();
        sellerPic = in.readString();
        sellerSign = in.readString();
        status = in.readString();
        buyerName = in.readString();
        buyerMobile = in.readString();
        buyerAltMobile = in.readString();
        buyerEmail = in.readString();
        buyerAddress = in.readString();
        buyerNote = in.readString();
        buyerProof1 = in.readString();
        buyerProof2 = in.readString();
        buyerPic = in.readString();
        selling_date = in.readString();
        sell_price = in.readString();
        uid1 = in.readString();
        uid2 = in.readString();
    }

    public static final Creator<ItemsModel> CREATOR = new Creator<ItemsModel>() {
        @Override
        public ItemsModel createFromParcel(Parcel in) {
            return new ItemsModel(in);
        }

        @Override
        public ItemsModel[] newArray(int size) {
            return new ItemsModel[size];
        }
    };

    public String getSelling_date() {
        return selling_date;
    }

    public void setSelling_date(String selling_date) {
        this.selling_date = selling_date;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String sell_price) {
        this.sell_price = sell_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(String warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public String getBillPhoto() {
        return billPhoto;
    }

    public void setBillPhoto(String billPhoto) {
        this.billPhoto = billPhoto;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerMobile() {
        return sellerMobile;
    }

    public void setSellerMobile(String sellerMobile) {
        this.sellerMobile = sellerMobile;
    }

    public String getSellerAltMobile() {
        return sellerAltMobile;
    }

    public void setSellerAltMobile(String sellerAltMobile) {
        this.sellerAltMobile = sellerAltMobile;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerRemarks() {
        return sellerRemarks;
    }

    public void setSellerRemarks(String sellerRemarks) {
        this.sellerRemarks = sellerRemarks;
    }

    public String getIdProof1() {
        return idProof1;
    }

    public void setIdProof1(String idProof1) {
        this.idProof1 = idProof1;
    }

    public String getIdProof2() {
        return idProof2;
    }

    public void setIdProof2(String idProof2) {
        this.idProof2 = idProof2;
    }

    public String getSellerPic() {
        return sellerPic;
    }

    public void setSellerPic(String sellerPic) {
        this.sellerPic = sellerPic;
    }

    public String getSellerSign() {
        return sellerSign;
    }

    public void setSellerSign(String sellerSign) {
        this.sellerSign = sellerSign;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerMobile() {
        return buyerMobile;
    }

    public void setBuyerMobile(String buyerMobile) {
        this.buyerMobile = buyerMobile;
    }

    public String getBuyerAltMobile() {
        return buyerAltMobile;
    }

    public void setBuyerAltMobile(String buyerAltMobile) {
        this.buyerAltMobile = buyerAltMobile;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerNote() {
        return buyerNote;
    }

    public void setBuyerNote(String buyerNote) {
        this.buyerNote = buyerNote;
    }

    public String getBuyerProof1() {
        return buyerProof1;
    }

    public void setBuyerProof1(String buyerProof1) {
        this.buyerProof1 = buyerProof1;
    }

    public String getBuyerProof2() {
        return buyerProof2;
    }

    public void setBuyerProof2(String buyerProof2) {
        this.buyerProof2 = buyerProof2;
    }

    public String getBuyerPic() {
        return buyerPic;
    }

    public void setBuyerPic(String buyerPic) {
        this.buyerPic = buyerPic;
    }

    public String getUid1() {
        return uid1;
    }

    public void setUid1(String uid1) {
        this.uid1 = uid1;
    }

    public String getUid2() {
        return uid2;
    }

    public void setUid2(String uid2) {
        this.uid2 = uid2;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(itemName);
        dest.writeString(creationDate);
        dest.writeString(purchasePrice);
        dest.writeString(itemDescription);
        dest.writeString(warrantyDate);
        dest.writeString(billPhoto);
        dest.writeString(sellerName);
        dest.writeString(sellerMobile);
        dest.writeString(sellerAltMobile);
        dest.writeString(sellerEmail);
        dest.writeString(sellerRemarks);
        dest.writeString(idProof1);
        dest.writeString(idProof2);
        dest.writeString(sellerPic);
        dest.writeString(sellerSign);
        dest.writeString(status);
        dest.writeString(buyerName);
        dest.writeString(buyerMobile);
        dest.writeString(buyerAltMobile);
        dest.writeString(buyerEmail);
        dest.writeString(buyerAddress);
        dest.writeString(buyerNote);
        dest.writeString(buyerProof1);
        dest.writeString(buyerProof2);
        dest.writeString(buyerPic);
        dest.writeString(selling_date);
        dest.writeString(sell_price);
        dest.writeString(uid1);
        dest.writeString(uid2);
    }
}

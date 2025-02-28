package com.sellpoint.models;

public class ItemRequest {
    private String mobile;
    private String category;
    private String item_name;
    private String purchase_price;
    private String description;
    private String bill_photo;
    private String seller_name;
    private String seller_mobile;
    private String alternate_seller_mobile;
    private String seller_email;
    private String remarks;
    private String warranty_date;
    private String id_proof_1_pic;
    private String id_proof_2_pic;
    private String seller_photo;
    private String signature_photo;
    private String creation_date;
    private int status;

    public ItemRequest(String mobile, String category, String item_name, String purchase_price, String description,
                       String bill_photo, String seller_name, String seller_mobile, String alternate_seller_mobile,
                       String seller_email, String remarks, String warranty_date, String id_proof_1_pic,
                       String id_proof_2_pic, String seller_photo, String signature_photo, String creation_date, int status) {
        this.mobile = mobile;
        this.category = category;
        this.item_name = item_name;
        this.purchase_price = purchase_price;
        this.description = description;
        this.bill_photo = bill_photo;
        this.seller_name = seller_name;
        this.seller_mobile = seller_mobile;
        this.alternate_seller_mobile = alternate_seller_mobile;
        this.seller_email = seller_email;
        this.remarks = remarks;
        this.warranty_date = warranty_date;
        this.id_proof_1_pic = id_proof_1_pic;
        this.id_proof_2_pic = id_proof_2_pic;
        this.seller_photo = seller_photo;
        this.signature_photo = signature_photo;
        this.creation_date = creation_date;
        this.status = status;
    }

    // Getters and Setters (if required)

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(String purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBill_photo() {
        return bill_photo;
    }

    public void setBill_photo(String bill_photo) {
        this.bill_photo = bill_photo;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_mobile() {
        return seller_mobile;
    }

    public void setSeller_mobile(String seller_mobile) {
        this.seller_mobile = seller_mobile;
    }

    public String getAlternate_seller_mobile() {
        return alternate_seller_mobile;
    }

    public void setAlternate_seller_mobile(String alternate_seller_mobile) {
        this.alternate_seller_mobile = alternate_seller_mobile;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getWarranty_date() {
        return warranty_date;
    }

    public void setWarranty_date(String warranty_date) {
        this.warranty_date = warranty_date;
    }

    public String getId_proof_1_pic() {
        return id_proof_1_pic;
    }

    public void setId_proof_1_pic(String id_proof_1_pic) {
        this.id_proof_1_pic = id_proof_1_pic;
    }

    public String getId_proof_2_pic() {
        return id_proof_2_pic;
    }

    public void setId_proof_2_pic(String id_proof_2_pic) {
        this.id_proof_2_pic = id_proof_2_pic;
    }

    public String getSeller_photo() {
        return seller_photo;
    }

    public void setSeller_photo(String seller_photo) {
        this.seller_photo = seller_photo;
    }

    public String getSignature_photo() {
        return signature_photo;
    }

    public void setSignature_photo(String signature_photo) {
        this.signature_photo = signature_photo;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


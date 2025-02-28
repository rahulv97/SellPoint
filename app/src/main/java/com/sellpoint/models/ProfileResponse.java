package com.sellpoint.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ProfileResponse {
    private String status;
    private String message;
    private Data data;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data implements Parcelable {
        private int id;
        private String shop_name;
        private String full_name;
        private String mobile;
        private String email;
        private String password;
        private String created_at;
        private String expiry_date;

        protected Data(Parcel in) {
            id = in.readInt();
            shop_name = in.readString();
            full_name = in.readString();
            mobile = in.readString();
            email = in.readString();
            created_at = in.readString();
            password = in.readString();
            expiry_date = in.readString();
        }

        public static Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public int getId() {
            return id;
        }

        public String getShopName() {
            return shop_name;
        }

        public String getFullName() {
            return full_name;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getCreatedAt() {
            return created_at;
        }

        public String getExpiry_date() {
            return expiry_date;
        }

        public void setExpiry_date(String expiry_date) {
            this.expiry_date = expiry_date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(shop_name);
            dest.writeString(full_name);
            dest.writeString(mobile);
            dest.writeString(email);
            dest.writeString(created_at);
            dest.writeString(password);
            dest.writeString(expiry_date);
        }
    }
}


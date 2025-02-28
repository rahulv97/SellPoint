package com.sellpoint.models;

public class LoginResponse {
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

    public class Data {
        private int id;
        private String shop_name;
        private String full_name;
        private String mobile;
        private String email;
        private String created_at;

        // Getters for each field
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

        public String getCreatedAt() {
            return created_at;
        }
    }
}

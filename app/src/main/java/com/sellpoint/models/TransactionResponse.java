package com.sellpoint.models;

import java.util.List;

public class TransactionResponse {
    private String status;
    private String message;
    private List<Transaction> transactions;

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Transaction> getTransactions() { return transactions; }
}
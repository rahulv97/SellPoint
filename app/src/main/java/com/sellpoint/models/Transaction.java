package com.sellpoint.models;

public class Transaction {
    private String old_date;
    private String new_date;
    private int days_difference;
    private String insertion_date;

    // Getters
    public String getOldDate() { return old_date; }
    public String getNewDate() { return new_date; }
    public int getDaysDifference() { return days_difference; }
    public String getInsertionDate() { return insertion_date; }
}

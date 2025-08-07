package com.yuzhengchua.cinema.enums;

public enum Actions {
    ACTION1 ("[1] Book tickets for "),
    ACTION2 ("[2] Check bookings"),
    ACTION3 ("[3] Exit");
    
    private final String action;
    private Actions(String action) {
        this.action = action;
    }
    @Override
    public String toString() {
        return this.action;
     }
}
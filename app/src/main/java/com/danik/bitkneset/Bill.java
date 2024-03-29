package com.danik.bitkneset;

public class Bill {
    private String type;
    private String desc;
    private String user;
    private String amount;
    private boolean paid;
    private String date;

    public Bill(String type, String desc, String amount, boolean paid,String date) {
        this.type = type;
        this.desc = desc;
        this.user = "הנהלה";
        this.amount = amount;
        this.paid = paid;
        this.date = date;
    }
    public Bill(String type, String desc, String user, String amount, boolean paid, String date) {
        this.type = type;
        this.desc = desc;
        this.user = user;
        this.amount = amount;
        this.paid = paid;
        this.date = date;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

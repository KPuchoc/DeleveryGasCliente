package com.example.sportwearstoreolivos.Model;

public class Rating {
    private String userPhone;
    private String Id;
    private String rateValue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String id, String rateValue, String comment) {
        this.userPhone = userPhone;
        Id = id;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

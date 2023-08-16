package com.example.sportwearstoreolivos.Model;

public class Sportswear {
    private String Name;
    private String Image;
    private String Description;
    private String Discount;
    private String Price;
    private String id;

    public Sportswear() {
    }

    public Sportswear(String name, String image, String description, String discount, String id,String price) {
        Name = name;
        Image = image;
        Description = description;
        Discount = discount;
        id = id;
        Price =price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}

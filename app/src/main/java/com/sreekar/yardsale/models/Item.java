package com.sreekar.yardsale.models;

/*
This is the basic layout for an item object.
 */

public class Item {
    private String sellerName;
    private String title;
    private String description;
    private Float price;
    private Float condition;
    private String image;
    private boolean purchased;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getCondition() {
        return condition;
    }

    public void setCondition(Float condition) {
        this.condition = condition;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public boolean isPurchased() {
        return purchased;
    }
}

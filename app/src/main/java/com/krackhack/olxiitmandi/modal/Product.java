package com.krackhack.olxiitmandi.modal;

public class Product {
    private String productId;
    private String name;
    private String category;
    private double price;
    private double discount;
    private String details;
    private int years;
    private String imageUrl1;
    private String imageUrl2;
    private String uploadDate;
    private String buyDate;
    private String sellerId;
    private String buyerId;
    private String status;

    // Required empty constructor for Firestore
    public Product() {
    }


    public Product(String name, String category, double price, double discount, String details, String status, int years,
                   String imageUrl1, String imageUrl2, String uploadDate, String buyDate, String productId, String sellerId, String buyerId) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.details = details;
        this.status = status;
        this.years = years;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.uploadDate = uploadDate;
        this.buyDate = buyDate;
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public String getDetails() {
        return details;
    }

    public int getYears() {
        return years;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

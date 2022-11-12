package com.example.pinventory;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductRVModel implements Parcelable {
    private String productName;
    private String productDesc;
    private String productQty;
    private String expiryDate;
    private String productImg;
    private String productID;
    //private String productExpiry Date/QR Code

    public ProductRVModel() {

    }

    public ProductRVModel(String productName, String productDesc, String productQty, String expiryDate, String productImg, String productID) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productQty = productQty;
        this.expiryDate = expiryDate;
        this.productImg = productImg;
        this.productID = productID;
    }

    protected ProductRVModel(Parcel in) {
        productName = in.readString();
        productDesc = in.readString();
        productQty = in.readString();
        expiryDate = in.readString();
        productImg = in.readString();
        productID = in.readString();
    }

    public static final Creator<ProductRVModel> CREATOR = new Creator<ProductRVModel>() {
        @Override
        public ProductRVModel createFromParcel(Parcel in) {
            return new ProductRVModel(in);
        }

        @Override
        public ProductRVModel[] newArray(int size) {
            return new ProductRVModel[size];
        }
    };

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productDesc);
        dest.writeString(productQty);
        dest.writeString(expiryDate);
        dest.writeString(productImg);
        dest.writeString(productID);
    }
}

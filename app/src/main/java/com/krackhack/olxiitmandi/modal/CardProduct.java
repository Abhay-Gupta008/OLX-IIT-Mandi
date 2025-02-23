package com.krackhack.olxiitmandi.modal;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;


@Entity(tableName = "cart_items")
public class CardProduct implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "product_name")
    private String name;

    @ColumnInfo(name = "product_image")
    private String imageUrl;

    @ColumnInfo(name = "product_price")
    private long price;

    // Default Constructor for Room
    public CardProduct() {
    }

    // Parameterized Constructor
    public CardProduct(String name, String imageUrl, long price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    // Parcelable Implementation
    protected CardProduct(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUrl = in.readString();
        price = in.readLong();
    }

    public static final Creator<CardProduct> CREATOR = new Creator<CardProduct>() {
        @Override
        public CardProduct createFromParcel(Parcel in) {
            return new CardProduct(in);
        }

        @Override
        public CardProduct[] newArray(int size) {
            return new CardProduct[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeLong(price);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

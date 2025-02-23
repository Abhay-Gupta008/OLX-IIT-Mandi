package com.krackhack.olxiitmandi.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.databinding.ActivityProductDetailBinding;

public class ProductDetail extends AppCompatActivity {

    private ActivityProductDetailBinding binding;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve product details from Intent
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        long price = getIntent().getLongExtra("price", 0);
        long discount = getIntent().getLongExtra("discount", 0);
        String details = getIntent().getStringExtra("details");
        String status = getIntent().getStringExtra("status");
        int years = getIntent().getIntExtra("years", 0);
        String uploadDate = getIntent().getStringExtra("uploadDate");
        String buyDate = getIntent().getStringExtra("buyDate");
        String imageUrl1 = getIntent().getStringExtra("imageUrl1");
        String imageUrl2 = getIntent().getStringExtra("imageUrl2");
        String sellerId = getIntent().getStringExtra("sellerId");

        binding.productNameTextView.setText(name != null ? name : "No Name Available");
        binding.productDiscountTextView.setText(discount > 0 ? String.valueOf(discount) : "No Discount");
        binding.productPriceTextView.setText( String.format("₹%d", price) );
        binding.productDetailsTextView.setText(details != null ? details : "No Details Available");
        binding.productCategoryTextView.setText(category != null ? category : "Category Not Available");
        binding.productStatusTextView.setText(status != null ? status : "Status Unknown");
        binding.uploadDateTextView.setText(uploadDate != null ? uploadDate : "No Upload Date");
        binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productPrice = (int) price;
                Intent intent = new Intent(ProductDetail.this, BuyNowActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("category", category);
                intent.putExtra("price", productPrice);
                intent.putExtra("discount", discount);
                intent.putExtra("details", details);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("years", years);
                intent.putExtra("uploadDate", uploadDate);
                intent.putExtra("buyDate", buyDate);
                intent.putExtra("imageUrl1", imageUrl1);
                intent.putExtra("imageUrl2", imageUrl2);

                startActivity(intent);
            }
        });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (discount > 0) {
            setStyledText(binding.productDiscountTextView, "Discount: ", discount + "%", Color.RED, Color.BLACK);
        } else {
            binding.productDiscountTextView.setVisibility(View.GONE);
        }

        Glide.with(this).load(imageUrl1).into(binding.productImageView1);
        binding.productImageView1.setOnClickListener(v -> showImageInDialog(imageUrl1));

        if (imageUrl2 != null && !imageUrl2.isEmpty()) {
            Glide.with(this).load(imageUrl2).into(binding.productImageView2);
            binding.productImageView2.setOnClickListener(v -> showImageInDialog(imageUrl2));
        } else {
            binding.productImageView2.setVisibility(View.GONE);
        }
    }

    private void showImageInDialog(String imageUrl) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_zoom_image);

        PhotoView photoView = dialog.findViewById(R.id.zoomImageView);
        Glide.with(this).load(imageUrl).into(photoView);

        photoView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Helper method to style TextView with different colors for label and value
    private void setStyledText(TextView textView, String label, String value, int labelColor, int valueColor) {
        String styledText = "<font color='" + labelColor + "'>" + label + "</font>" +
                "<font color='" + valueColor + "'>" + value + "</font>";
        textView.setText(Html.fromHtml(styledText));
        textView.setTextSize(16);
    }
}


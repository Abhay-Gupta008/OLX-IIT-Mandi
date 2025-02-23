package com.krackhack.olxiitmandi.activity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.databinding.ActivityBuyNowBinding;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BuyNowActivity extends AppCompatActivity {
    ActivityBuyNowBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String name, category, details, sellerId, uploadDate, buyDate, imageUrl1, imageUrl2;
    long discount;
    int price, years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyNowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Retrieve product details from Intent
        name = getIntent().getStringExtra("name");
        category = getIntent().getStringExtra("category");
        price = getIntent().getIntExtra("price", 0);
        discount = getIntent().getLongExtra("discount", 0);
        details = getIntent().getStringExtra("details");
        sellerId = getIntent().getStringExtra("sellerId");
        years = getIntent().getIntExtra("years", 0);
        uploadDate = getIntent().getStringExtra("uploadDate");
        buyDate = getIntent().getStringExtra("buyDate");
        imageUrl1 = getIntent().getStringExtra("imageUrl1");
        imageUrl2 = getIntent().getStringExtra("imageUrl2");

        // ✅ Fixed: Convert price to String before setting
        binding.totalPrice.setText(String.valueOf(price));

        binding.payNowButton.setOnClickListener(v -> uploadOrder());
    }

    private void uploadOrder() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String buyerId = auth.getCurrentUser().getUid();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create order object
        Product order = new Product(
                name, category, (double) price, (double) discount, details, "Pending", years,
                imageUrl1, imageUrl2, uploadDate, buyDate, null, sellerId, buyerId
        );

        // Save order in Firestore
        db.collection("Orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    String orderId = documentReference.getId();
                    Log.d("Order", "Order placed with ID: " + orderId);

                    // Update order with order ID
                    updateOrderWithId(documentReference, orderId);

                    Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Order", "Error placing order", e);
                    Toast.makeText(this, "Order failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateOrderWithId(DocumentReference documentReference, String orderId) {
        documentReference.update("productId", orderId)
                .addOnSuccessListener(aVoid -> Log.d("Order", "Order ID updated"))
                .addOnFailureListener(e -> Log.e("Order", "Failed to update order ID", e));
    }

    private void fetchSellerOrders() {
        if (auth.getCurrentUser() == null) {
            Log.e("Seller Orders", "User not logged in!");
            return;
        }
        String sellerId = auth.getCurrentUser().getUid();
        db.collection("Orders")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Product order = document.toObject(Product.class);
                        if (order != null) {
                            Log.d("Seller Orders", "Buyer ID: " + order.getBuyerId() + ", Product: " + order.getName() + ", Status: " + order.getStatus());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Seller Orders", "Error fetching orders", e));
    }
}

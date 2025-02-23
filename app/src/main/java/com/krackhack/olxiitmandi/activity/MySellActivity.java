package com.krackhack.olxiitmandi.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krackhack.olxiitmandi.adapter.OrderAdapter;
import com.krackhack.olxiitmandi.databinding.ActivityMySellBinding;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.ArrayList;
import java.util.List;

public class MySellActivity extends AppCompatActivity {
    private OrderAdapter orderAdapter;
    private List<Product> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ActivityMySellBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMySellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(orderAdapter);

        // Fetch User Sold Items
        fetchUserSoldItems();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUserSoldItems() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid(); // Get the current user's unique ID
        Log.d("MySellActivity", "Fetching all products for filtering...");

        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("products") // Fetch all products
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    orderList.clear(); // Clear list before adding new data

                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("MySellActivity", "Document Data: " + document.getData());

                            Product product = document.toObject(Product.class);

                            // Check if sellerId matches current user
                            if (product.getSellerId() != null && product.getSellerId().equals(userId)) {
                                orderList.add(product);
                                Log.d("MySellActivity", "Matched order added: " + product);
                            } else {
                                Log.d("MySellActivity", "Skipping order. Seller ID mismatch or null.");
                            }
                        }

                        // Update RecyclerView
                        if (!orderList.isEmpty()) {
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.emptyView.setVisibility(View.GONE);
                            orderAdapter.notifyDataSetChanged();
                        } else {
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.emptyView.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "No sold items found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching products", task.getException());
                        Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Log.e("FirestoreError", "Error fetching sold items: ", e);
                    Toast.makeText(this, "Failed to load your sold items!", Toast.LENGTH_SHORT).show();
                });
    }
}

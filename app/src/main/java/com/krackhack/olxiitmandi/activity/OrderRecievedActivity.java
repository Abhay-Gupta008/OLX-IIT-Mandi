package com.krackhack.olxiitmandi.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krackhack.olxiitmandi.adapter.OrderAdapter;
import com.krackhack.olxiitmandi.databinding.ActivityOrderRecievedBinding;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderRecievedActivity extends AppCompatActivity {

    ActivityOrderRecievedBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private List<Product> receivedOrdersList;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderRecievedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        receivedOrdersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getApplicationContext(),receivedOrdersList);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(orderAdapter);

        fetchReceivedOrders();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchReceivedOrders() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String sellerId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("Orders")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    receivedOrdersList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product order = document.toObject(Product.class);
                        receivedOrdersList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Fetch Orders", "Error fetching received orders", e);
                    Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                });
    }
}

package com.krackhack.olxiitmandi.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.adapter.ProductAdapter;
import com.krackhack.olxiitmandi.databinding.ActivitySearchResultBinding;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<Product> allProductsList; // Store all products
    private List<Product> filteredProductList; // Store filtered products
    private ProductAdapter adapter;
    private ActivitySearchResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and lists
        db = FirebaseFirestore.getInstance();
        allProductsList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        // Set up RecyclerView
        adapter = new ProductAdapter(this, filteredProductList);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerView.setAdapter(adapter);

        // Load products from Firestore
        loadProducts();

        // Initialize Search Bar
        binding.searchBar.setHint("Search products...");
        binding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterProductsByName(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // Load products from Firestore
    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE); // Show loading indicator

        db.collectionGroup("products")
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE); // Hide progress bar
                    if (task.isSuccessful() && task.getResult() != null) {
                        allProductsList.clear();
                        for (var document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            allProductsList.add(product);
                        }
                        showAllProducts();
                    } else {
                        Toast.makeText(this, "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterProductsByName(String query) {
        binding.progressBar.setVisibility(View.VISIBLE); // Show loading while filtering
        filteredProductList.clear();

        if (query.isEmpty()) {
            showAllProducts();
        } else {
            for (Product product : allProductsList) {
                if (containsIgnoreCase(product.getName(), query)) { // Proper substring search
                    filteredProductList.add(product);
                }
            }
        }

        binding.progressBar.setVisibility(View.GONE); // Hide loading after filtering

        // Show empty view if no matching products are found
        if (filteredProductList.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }

    // Helper method to check if the product name contains the query (case insensitive)
    private boolean containsIgnoreCase(String source, String query) {
        if (source == null || query == null) return false;
        return source.toLowerCase().contains(query.toLowerCase());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAllProducts() {
        binding.progressBar.setVisibility(View.GONE); // Hide loading
        filteredProductList.clear();
        filteredProductList.addAll(allProductsList);

        // Show/hide UI elements
        if (filteredProductList.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }
}

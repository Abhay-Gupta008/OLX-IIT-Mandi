package com.krackhack.olxiitmandi.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.activity.SearchResultActivity;
import com.krackhack.olxiitmandi.adapter.ProductAdapter;
import com.krackhack.olxiitmandi.databinding.FragmentHomeBinding;
import com.krackhack.olxiitmandi.modal.Product;



public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private List<Product> allProductsList; // Store all products
    private List<Product> filteredProductList; // Store filtered products
    private ProductAdapter adapter;
    ImageCarousel carousel;
    List<CarouselItem> list;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        // Initialize lists
        allProductsList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        adapter = new ProductAdapter(requireContext(), filteredProductList);

        // Initialize carousel correctly
        binding.carousel.registerLifecycle(getLifecycle());

        // Initialize the product list and add data to the carousel
        list = new ArrayList<>();
        addItemIntoList();
        binding.carousel.setData(list); // Set data after adding items

        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerViewProducts.setAdapter(adapter);

        // Load all products initially
        loadProducts();
        binding.searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchResultActivity.class));
            }
        });
        // Set category click listeners
        binding.categoryElectronics.setOnClickListener(view -> filterProducts("Electronics"));
        binding.categoryFashion.setOnClickListener(view -> filterProducts("Fashion"));
        binding.categoryBooks.setOnClickListener(view -> filterProducts("Books"));
        binding.categoryAppliances.setOnClickListener(view -> filterProducts("Appliances"));
        binding.categoryFurniture.setOnClickListener(view -> filterProducts("Furniture"));
        binding.categoryToys.setOnClickListener(view -> filterProducts("Toys"));
        binding.categorySports.setOnClickListener(view -> filterProducts("Sports"));
        binding.categoryAll.setOnClickListener(view -> showAllProducts()); // Show all products

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar
        allProductsList.clear();

        db.collectionGroup("products") // Fetch all products from Firestore
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE); // Hide progress bar
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            allProductsList.add(product);
                        }
                        Log.d("FirestoreSuccess", "Fetched " + allProductsList.size() + " products");
                        showAllProducts(); // Display all products initially
                    } else {
                        Log.e("FirestoreError", "Error fetching products", task.getException());
                        Toast.makeText(requireContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterProducts(String category) {
        binding.progressBar.setVisibility(View.VISIBLE); // Show progress bar
        filteredProductList.clear();

        // Filter products locally
        for (Product product : allProductsList) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                filteredProductList.add(product);
            }
        }

        binding.progressBar.setVisibility(View.GONE); // Hide progress bar
        Log.d("FilterSuccess", "Filtered " + filteredProductList.size() + " products for category: " + category);

        updateUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAllProducts() {
        filteredProductList.clear();
        filteredProductList.addAll(allProductsList); // Show all products
        updateUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        adapter.notifyDataSetChanged();
        if (filteredProductList.isEmpty()) {
            binding.recyclerViewProducts.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE); // Show empty view
        } else {
            binding.recyclerViewProducts.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }
    private void addItemIntoList(){
        list.add(new CarouselItem("https://i.pinimg.com/736x/e2/ac/85/e2ac8573878a0dc2e30b22b0674ce13c.jpg"));
        list.add(new CarouselItem("https://media.istockphoto.com/id/1355687112/photo/various-sport-equipment-gear.jpg?s=612x612&w=0&k=20&c=JOizKZg68gs_7lxjM3YLrngeS-7dGhBXL8b-wDBrYUE="));
        list.add(new CarouselItem("https://mommybabytimes.com/wp-content/uploads/2021/03/toys-e1614601147155.jpg"));
        list.add(new CarouselItem("https://image.makewebeasy.net/makeweb/m_1920x0/UoFkSNozd/KANO/pic1.jpg"));
    }
}

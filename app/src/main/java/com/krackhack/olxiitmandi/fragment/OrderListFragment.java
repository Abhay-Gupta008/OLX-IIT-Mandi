package com.krackhack.olxiitmandi.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.adapter.OrderAdapter;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderListFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Product> orderList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(),orderList);
        recyclerView.setAdapter(orderAdapter);

        fetchUserOrders();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUserOrders() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("Orders")
                .whereEqualTo("buyerId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product order = document.toObject(Product.class);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Order Fetch", "Error fetching orders", e);
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                });
    }
}

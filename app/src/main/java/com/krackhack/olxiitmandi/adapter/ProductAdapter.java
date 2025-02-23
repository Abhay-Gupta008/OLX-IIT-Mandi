package com.krackhack.olxiitmandi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.activity.ProductDetail;
import com.krackhack.olxiitmandi.databinding.ItemProductBinding;
import com.krackhack.olxiitmandi.modal.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product details
        holder.binding.productName.setText(product.getName());
        holder.binding.productDescription.setText(product.getDetails());
        holder.binding.productPrice.setText("₹" + product.getPrice());

        // Calculate and display the original price before discount
        if (product.getDiscount() > 0) {
            long originalPrice = (long) ((product.getPrice() * 100) / (100 - product.getDiscount()));
            holder.binding.productOriginalPrice.setText("₹" + originalPrice + " (" + product.getDiscount() + "% Off)");
        } else {
            holder.binding.productOriginalPrice.setVisibility(View.GONE);
        }

        // Open Product Detail Activity on click
        holder.binding.main.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("sellerId", product.getSellerId());
            intent.putExtra("name", product.getName());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("price", (long) product.getPrice());
            Log.d("PriceCheck", "Price being passed: " + product.getPrice());

            intent.putExtra("discount",(long) product.getDiscount());
            intent.putExtra("details", product.getDetails());
            intent.putExtra("status", product.getStatus());
            intent.putExtra("years", product.getYears());
            intent.putExtra("uploadDate", product.getUploadDate());
            intent.putExtra("buyDate", product.getBuyDate());
            intent.putExtra("imageUrl1", product.getImageUrl1());
            intent.putExtra("imageUrl2", product.getImageUrl2());
            context.startActivity(intent);
        });

        // Load product image with Picasso
        if (product.getImageUrl1() != null && !product.getImageUrl1().isEmpty()) {
            Picasso.get().load(product.getImageUrl1()).placeholder(R.drawable.traveller).into(holder.binding.productImage);
        } else {
            holder.binding.productImage.setImageResource(R.drawable.traveller);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;

        public ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

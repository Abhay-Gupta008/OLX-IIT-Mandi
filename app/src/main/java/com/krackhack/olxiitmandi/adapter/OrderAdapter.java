package com.krackhack.olxiitmandi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.modal.Product;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Product> orderList;

    public OrderAdapter(Context context, List<Product> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_history_item, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Product order = orderList.get(position);

        // Use String.format() for better readability
        holder.orderIdTextView.setText(String.format("Order ID: #%s", order.getProductId()));
        holder.orderDateTextView.setText(String.format("Date: %s", order.getBuyDate()));
        holder.orderTotalTextView.setText(String.format("Total: ₹%s", order.getPrice()));

        // Load order image using Glide, with a placeholder
        Glide.with(context)
                .load(order.getImageUrl1() != null ? order.getImageUrl1() : "")
                .placeholder(R.drawable.traveller) // Use a placeholder if URL is empty
                .error(R.drawable.traveller) // In case of a failed load
                .into(holder.orderImageView);

        // Handle "View More" button click
        holder.viewMoreButton.setOnClickListener(v -> {
            boolean isExpanded = holder.orderDetailsTextView.getVisibility() == View.VISIBLE;
            holder.orderDetailsTextView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            holder.viewMoreButton.setText(isExpanded ? "View More" : "View Less");

            // Ensure details are not null
            holder.orderDetailsTextView.setText(order.getDetails() != null ? order.getDetails() : "No additional details available.");
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdTextView, orderDateTextView, viewMoreButton, orderTotalTextView, orderDetailsTextView;
        private final ImageView orderImageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalTextView);
            orderDetailsTextView = itemView.findViewById(R.id.detailsTextView);
            orderImageView = itemView.findViewById(R.id.orderImageView);
            viewMoreButton = itemView.findViewById(R.id.viewMoreText);

            orderDetailsTextView.setVisibility(View.GONE);
        }
    }
}

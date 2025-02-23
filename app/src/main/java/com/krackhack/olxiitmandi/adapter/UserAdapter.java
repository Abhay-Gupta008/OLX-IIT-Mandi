package com.krackhack.olxiitmandi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.krackhack.olxiitmandi.R;
//import com.krackhack.olxiitmandi.activity.ChattingRoomAcitivity;
import com.krackhack.olxiitmandi.databinding.ItemUserBinding;
import com.krackhack.olxiitmandi.modal.UserDetail;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserDetail> userList;
    private final Context context;

    // Constructor
    public UserAdapter(Context context, List<UserDetail> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDetail user = userList.get(position);
        holder.binding.userName.setText(user.getName());
        holder.binding.userEmail.setText(user.getEmail());

        // Click listener to open chat
//        holder.binding.userMain.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ChattingRoomAcitivity.class);
//            intent.putExtra("receiverId", user.getUserId()); // Ensure UserDetail has getUserId() method
//            context.startActivity(intent);
//        });

        // Load profile image using Glide
        Glide.with(holder.binding.getRoot().getContext())
                .load(user.getProfileImage() != null && !user.getProfileImage().isEmpty() ? user.getProfileImage() : R.drawable.account)
                .placeholder(R.drawable.account)
                .error(R.drawable.account)
                .into(holder.binding.profileImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        public UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

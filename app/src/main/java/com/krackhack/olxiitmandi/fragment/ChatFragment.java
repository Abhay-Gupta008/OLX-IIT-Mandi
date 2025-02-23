package com.krackhack.olxiitmandi.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.adapter.UserAdapter;
import com.krackhack.olxiitmandi.modal.UserDetail;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserDetail> userList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    public ChatFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        progressBar = view.findViewById(R.id.progressBar);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(requireContext(),userList);
        recyclerView.setAdapter(userAdapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchUsers();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchUsers() {
        // Show progress bar before fetching data
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            UserDetail user = document.toObject(UserDetail.class);
                            if (user != null && !document.getId().equals(auth.getUid())) {
                                user.setUserId(document.getId()); // Store Firebase UID
                                userList.add(user);
                            }
                        }
                        if (userList.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            userAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Failed to load users", Toast.LENGTH_SHORT).show();
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

}

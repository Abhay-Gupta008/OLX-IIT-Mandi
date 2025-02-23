package com.krackhack.olxiitmandi.fragment;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.activity.LoginActivity;
import com.krackhack.olxiitmandi.activity.MySellActivity;
import com.krackhack.olxiitmandi.activity.OrderRecievedActivity;
import com.krackhack.olxiitmandi.databinding.FragmentProfileBinding;
import com.krackhack.olxiitmandi.modal.CloudinaryResponse;
import com.krackhack.olxiitmandi.utils.CloudinaryService;
import com.krackhack.olxiitmandi.utils.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.krackhack.olxiitmandi.R;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String imageUrl;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        fetchUserData();

        binding.profileImage.setOnClickListener(v -> pickImage());

        binding.btnUpdateProfile.setOnClickListener(v -> updateProfile());

        binding.logOut.setOnClickListener(v -> logoutUser());
        binding.ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListFragment orderFragment = new OrderListFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, orderFragment); // Replace with your actual container ID
                transaction.addToBackStack(null); // Enables back navigation
                transaction.commit();

                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.order);
            }
        });
        binding.ordReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OrderRecievedActivity.class));
            }
        });
        binding.sells.setOnClickListener(v -> startActivity(new Intent(requireContext(), MySellActivity.class)));
        return binding.getRoot();
    }


    private void fetchUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Display Email & Disable Editing
            binding.etEmail.setText(user.getEmail());
            binding.etEmail.setEnabled(false);

            // Fetch User Data from Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Use "name" instead of "displayName"
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String profileUrl = documentSnapshot.getString("profileImage");

                            Log.d(TAG, "Name: " + name + ", Email: " + email + ", Profile URL: " + profileUrl);

                            if (name != null && !name.isEmpty()) {
                                binding.etUserName.setText(name);
                            } else {
                                Log.e(TAG, "Name field is null or empty in Firestore");
                            }

                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                Picasso.get().load(profileUrl).into(binding.profileImage);
                            } else {
                                Log.e(TAG, "Profile image URL is null or empty");
                            }
                        } else {
                            Log.e(TAG, "User document does not exist in Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch user data: " + e.getMessage());
                        Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "FirebaseUser is null");
        }
    }


private void pickImage() {
    ImagePicker.Companion.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start();
}

@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && data != null) {
        selectedImageUri = data.getData();
        binding.profileImage.setImageURI(selectedImageUri);
    }
}

private void updateProfile() {
    String newName = Objects.requireNonNull(binding.etUserName.getText()).toString();

    if (selectedImageUri != null) {
        uploadImageToCloudinary(selectedImageUri, newName);
    } else {
        saveUserData(newName, imageUrl);
    }
}

private void uploadImageToCloudinary(Uri imageUri, String name) {
    File file = new File(Objects.requireNonNull(imageUri.getPath()));
    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    RequestBody uploadPreset = RequestBody.create(MediaType.parse("multipart/form-data"), "olx_upload");

    CloudinaryService service = RetrofitClient.getRetrofitInstance().create(CloudinaryService.class);
    Call<CloudinaryResponse> call = service.uploadImage(body, uploadPreset);

    call.enqueue(new Callback<CloudinaryResponse>() {
        @Override
        public void onResponse(@NonNull Call<CloudinaryResponse> call, @NonNull Response<CloudinaryResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                imageUrl = response.body().getSecureUrl();
                saveUserData(name, imageUrl);
            }
        }

        @Override
        public void onFailure(@NonNull Call<CloudinaryResponse> call, @NonNull Throwable t) {
            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
        }
    });
}

private void saveUserData(String name, String profileImageUrl) {
    Map<String, Object> userData = new HashMap<>();
    userData.put("name", name);
    userData.put("profileImage", profileImageUrl);

    db.collection("users").document(user.getUid()).update(userData)
            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show());
}

private void logoutUser() {
    auth.signOut(); // Logout from Firebase
    Intent intent = new Intent(getActivity(), LoginActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
    startActivity(intent);
}
}

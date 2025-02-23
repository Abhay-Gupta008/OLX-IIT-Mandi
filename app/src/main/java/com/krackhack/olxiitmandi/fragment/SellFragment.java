package com.krackhack.olxiitmandi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.modal.CloudinaryResponse;
import com.krackhack.olxiitmandi.modal.Product;
import com.krackhack.olxiitmandi.utils.CloudinaryService;
import com.krackhack.olxiitmandi.utils.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellFragment extends Fragment {
    private ImageView imageViewPhoto1, imageViewPhoto2;
    private TextInputEditText productName, productPrice, productDiscount, productDetail, productCurrentSituation, productYears;
    private Spinner spinnerCategory;
    private ProgressBar progressBar;
    private Uri imageUri1, imageUri2;
    private String imageUrl1, imageUrl2;
    private int selectedImage = 0; // 1 for imageViewPhoto1, 2 for imageViewPhoto2
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public SellFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize Views
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        imageViewPhoto1 = view.findViewById(R.id.imageViewPhoto1);
        imageViewPhoto2 = view.findViewById(R.id.imageViewPhoto2);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productDiscount = view.findViewById(R.id.productDiscount);
        productDetail = view.findViewById(R.id.productDetail);
        productCurrentSituation = view.findViewById(R.id.productCurrentSituation);
        productYears = view.findViewById(R.id.productYears);
        progressBar = view.findViewById(R.id.progressBar);
        Button buttonSubmit = view.findViewById(R.id.buttonSubmit);

        // Set Spinner Adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.category_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set Click Listeners for Image Selection
        imageViewPhoto1.setOnClickListener(v -> {
            selectedImage = 1;
            pickImage();
        });
        imageViewPhoto2.setOnClickListener(v -> {
            selectedImage = 2;
            pickImage();
        });

        // Submit Button Click
        buttonSubmit.setOnClickListener(v -> uploadBothImages());

        return view;
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
            Uri imageUri = data.getData();
            if (selectedImage == 1) {
                imageUri1 = imageUri;
                imageViewPhoto1.setImageURI(imageUri1);
            } else if (selectedImage == 2) {
                imageUri2 = imageUri;
                imageViewPhoto2.setImageURI(imageUri2);
            }
        }
    }

    private File getFileFromUri(Uri uri) {
        if (uri == null) {
            Log.e("FileError", "Uri is null, cannot convert to File.");
            return null;
        }

        File file = new File(requireContext().getCacheDir(), "upload_image_" + System.currentTimeMillis() + ".jpg");

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            if (inputStream == null) {
                Log.e("FileError", "InputStream is null for URI: " + uri.toString());
                return null;
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            Log.d("FileSuccess", "File created successfully: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            Log.e("FileError", "Failed to get file from URI: " + e.getMessage(), e);
            return null;
        }
    }

    private void uploadBothImages() {
        if (imageUri1 == null || imageUri2 == null) {
            Toast.makeText(getContext(), "Please select both images!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        uploadImageToCloudinary(imageUri1, 1);
    }

    private void uploadImageToCloudinary(Uri imageUri, int imageNumber) {
        File file = getFileFromUri(imageUri);
        if (file == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody uploadPreset = RequestBody.create(MediaType.parse("multipart/form-data"), "olx_upload");

        CloudinaryService service = RetrofitClient.getRetrofitInstance().create(CloudinaryService.class);
        Call<CloudinaryResponse> call = service.uploadImage(body, uploadPreset);

        call.enqueue(new Callback<CloudinaryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CloudinaryResponse> call, @NonNull Response<CloudinaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (imageNumber == 1) {
                        imageUrl1 = response.body().getSecureUrl();
                        uploadImageToCloudinary(imageUri2, 2);
                    } else {
                        imageUrl2 = response.body().getSecureUrl();
                        saveProductData();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CloudinaryResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Image upload failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProductData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("FirestoreError", "User is null, cannot save product.");
            return;
        }

        if (imageUrl1 == null || imageUrl2 == null) {
            Log.e("FirestoreError", "Image URLs are null, cannot save product.");
            return;
        }

        String userId = user.getUid();
        String productId = db.collection("products").document().getId();
        String uploadDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String name = Objects.requireNonNull(productName.getText()).toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String detail = Objects.requireNonNull(productDetail.getText()).toString().trim();
        String condition = Objects.requireNonNull(productCurrentSituation.getText()).toString().trim();
        String years = Objects.requireNonNull(productYears.getText()).toString().trim();
        String price = Objects.requireNonNull(productPrice.getText()).toString().trim();
        String discount = Objects.requireNonNull(productDiscount.getText()).toString().trim();

        if (name.isEmpty() || category.isEmpty() || detail.isEmpty() || condition.isEmpty() || years.isEmpty() || price.isEmpty() || discount.isEmpty()) {
            Log.e("FirestoreError", "One or more required fields are empty!");
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        long priceValue;
        long discountValue;
        int yearsValue;

        try {
            priceValue = Long.parseLong(price);
            discountValue = Long.parseLong(discount);
            yearsValue = Integer.parseInt(years);
        } catch (NumberFormatException e) {
            Log.e("FirestoreError", "Invalid number format: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Invalid numeric values!", Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product(
                name, category, priceValue, discountValue, detail, condition, yearsValue,
                imageUrl1, imageUrl2, uploadDate, "", productId,userId,""
        );

        db.collection("products").document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Product uploaded successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("FirestoreSuccess", "Product saved successfully: " + productId);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("FirestoreError", "Failed to save product: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Failed to upload product!", Toast.LENGTH_SHORT).show();
                });
    }


}

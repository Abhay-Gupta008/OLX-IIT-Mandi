package com.krackhack.olxiitmandi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.krackhack.olxiitmandi.databinding.ActivitySignUpBinding;
import com.krackhack.olxiitmandi.modal.UserDetail;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Navigate to Login Screen
        binding.loginLink.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

        // Set up the Sign-Up button click listener
        binding.signUpButton.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String name = binding.name.getText() != null ? binding.name.getText().toString().trim() : "";
        String email = binding.email.getText() != null ? binding.email.getText().toString().trim() : "";
        String password = binding.password.getText() != null ? binding.password.getText().toString().trim() : "";
        String confirmPassword = binding.confirmPassword.getText() != null ? binding.confirmPassword.getText().toString().trim() : "";

        // Validate the fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {  // Firebase requires at least 6 characters
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress Bar and Disable Button to prevent multiple clicks
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.signUpButton.setEnabled(false);

        // Sign up with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send email verification (optional but recommended)
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            storeUserData(user.getUid(), name, email);
                                        } else {
                                            showError(emailTask.getException(), "Failed to send verification email");
                                        }
                                    });
                        }
                    } else {
                        showError(task.getException(), "Authentication Failed");
                    }
                });
    }

    private void storeUserData(String userId, String name, String email) {
        // Do NOT store passwords in Firestore (security risk)
        UserDetail user = new UserDetail(userId, name, email, ""); // No password stored

        db.collection("users")
                .document(userId) // Use Firebase UID as the document ID
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User Registered Successfully. Please verify your email.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class)); // Redirect to login
                    finish();
                })
                .addOnFailureListener(e -> showError(e, "Error Saving Data"));
    }

    private void showError(Exception e, String message) {
        // Hide Progress Bar and Enable Button
        binding.progressBar.setVisibility(View.GONE);
        binding.signUpButton.setEnabled(true);

        if (e != null) {
            Toast.makeText(this, message + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}

package com.krackhack.olxiitmandi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.krackhack.olxiitmandi.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // ✅ Fixed super.onCreate() syntax
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Navigate to Sign-Up Activity
        binding.signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });

        // Login Button Click Listener
        binding.loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = Objects.requireNonNull(binding.username.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.password.getText()).toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress Bar and Disable Button
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);

        // Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide Progress Bar and Enable Button
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // Close LoginActivity so user can't go back
                        }
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

package com.krackhack.olxiitmandi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.databinding.ActivityForgotPasswordBinding;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword() {
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Enter your registered email");
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Password reset email sent!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}

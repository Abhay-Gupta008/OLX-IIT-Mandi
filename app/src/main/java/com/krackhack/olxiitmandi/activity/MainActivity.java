package com.krackhack.olxiitmandi.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import com.krackhack.olxiitmandi.R;
import com.krackhack.olxiitmandi.fragment.ChatFragment;
import com.krackhack.olxiitmandi.fragment.HomeFragment;
import com.krackhack.olxiitmandi.fragment.OrderListFragment;
import com.krackhack.olxiitmandi.fragment.ProfileFragment;
import com.krackhack.olxiitmandi.fragment.SellFragment;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ArrayList<String> permissionsList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM Token", token);
                });
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;  // Start with null to avoid predefined fragment.

                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.chat) {
                    selectedFragment = new ChatFragment();
                } else if (itemId == R.id.add) {
                    selectedFragment = new SellFragment();
                } else if (itemId == R.id.order) {
                    selectedFragment = new OrderListFragment();
                } else if (itemId == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                }

                // Replace the fragment only if it's not null.
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selectedFragment)
                            .addToBackStack(null)  // Optional: adds to the back stack
                            .commit();
                }
                return true;
            }
        });

        // If savedInstanceState is null, replace with HomeFragment by default.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }
    }
    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() != R.id.home) {
            // Switch to Home tab but also clear the back stack properly
            bottomNavigationView.setSelectedItemId(R.id.home);

            // Remove all previous fragments from the back stack
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // If already on Home, exit the app
            super.onBackPressed();
        }
    }
    private void requestPermissions() {
        permissionsList = new ArrayList<>();
        permissionsList.add(Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissionsList.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissionsList.add(Manifest.permission.READ_MEDIA_AUDIO);
        } else {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] permissions = permissionsList.toArray(new String[0]);

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Toast.makeText(MainActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permissions denied: " + deniedPermissions, Toast.LENGTH_LONG).show();
            }
        });
    }
}

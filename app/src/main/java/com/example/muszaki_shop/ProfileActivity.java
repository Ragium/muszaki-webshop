package com.example.muszaki_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszaki_shop.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            binding.emailTextView.setText(user.getEmail());
            binding.nameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "Névtelen felhasználó");
        }

        binding.cartButton.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Sikeres kijelentkezés", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 
package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.muszaki_shop.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends MainActivity {
    private TextView emailTextView;
    private TextView nameTextView;
    private MaterialButton cartButton;
    private MaterialButton logoutButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile);

        emailTextView = findViewById(R.id.emailTextView);
        nameTextView = findViewById(R.id.nameTextView);
        cartButton = findViewById(R.id.cartButton);
        logoutButton = findViewById(R.id.logoutButton);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());
            nameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
        }

        cartButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
} 
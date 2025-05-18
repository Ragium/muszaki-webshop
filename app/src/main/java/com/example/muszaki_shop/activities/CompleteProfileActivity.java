package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszaki_shop.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {
    private TextInputEditText nameEditText;
    private TextInputEditText phoneEditText;
    private TextInputEditText addressEditText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);

        // Ha Google bejelentkezésről jöttünk, beállítjuk az email címet
        if (getIntent().getBooleanExtra("isGoogleSignIn", false) && auth.getCurrentUser() != null) {
            nameEditText.setText(auth.getCurrentUser().getDisplayName());
        }

        findViewById(R.id.saveButton).setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Kérjük, töltse ki az összes mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("phone", phone);
            userData.put("address", address);
            userData.put("email", auth.getCurrentUser().getEmail());
            userData.put("createdAt", System.currentTimeMillis());

            db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CompleteProfileActivity.this, "Profil sikeresen mentve!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CompleteProfileActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CompleteProfileActivity.this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }
} 
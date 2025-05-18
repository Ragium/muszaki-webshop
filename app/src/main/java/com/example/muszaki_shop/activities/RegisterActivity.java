package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszaki_shop.databinding.ActivityRegisterBinding;
import com.example.muszaki_shop.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.backToLoginButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();
        String name = binding.nameInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();
        String address = binding.addressInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A jelszónak legalább 6 karakter hosszúnak kell lennie!", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Felhasználó létrehozása a Firestore-ban
                            User user = new User(
                                firebaseUser.getUid(),
                                email,
                                name,
                                phone,
                                address
                            );

                            // Felhasználó mentése a Firestore-ba
                            db.collection("users").document(firebaseUser.getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, 
                                        "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivity.this,
                                        "Hiba történt: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                                });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                            "Regisztráció sikertelen: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        binding.loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.backToLoginButton.setEnabled(!show);
        binding.registerButton.setEnabled(!show);
    }
} 
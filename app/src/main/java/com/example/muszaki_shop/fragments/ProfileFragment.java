package com.example.muszaki_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    private TextInputEditText nameEditText, emailEditText, phoneEditText, addressEditText;
    private MaterialButton saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        saveButton = view.findViewById(R.id.saveButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadUserData();

        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            nameEditText.setText(user.getName());
                            emailEditText.setText(user.getEmail());
                            phoneEditText.setText(user.getPhone());
                            addressEditText.setText(user.getAddress());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hiba az adatok betöltésekor: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(getContext(), "Minden mező kitöltése kötelező!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(
                currentUser.getUid(),
                currentUser.getEmail(),
                name,
                phone,
                address
            );

            db.collection("users").document(currentUser.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Adatok sikeresen mentve!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hiba a mentés során: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
        }
    }
} 
package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.muszaki_shop.R;
import com.example.muszaki_shop.adapters.CartAdapter;
import com.example.muszaki_shop.models.CartItem;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemChangeListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private MaterialButton checkoutButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Bejelentkezés ellenőrzése
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();

        recyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Inicializáljuk az adaptert a listával
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();
        setupCheckoutButton();
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
            .collection("cart")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                cartItems.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    CartItem item = document.toObject(CartItem.class);
                    cartItems.add(item);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Hiba a kosár betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void setupCheckoutButton() {
        checkoutButton.setOnClickListener(v -> {
            // TODO: Fizetési folyamat implementálása
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.0f Ft", total));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        String userId = auth.getCurrentUser().getUid();
        String productId = String.valueOf(item.getProductId());
        
        db.collection("users").document(userId)
            .collection("cart")
            .document(productId)
            .set(item)
            .addOnSuccessListener(aVoid -> {
                updateTotalPrice();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Hiba a mennyiség frissítésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onItemRemoved(CartItem item) {
        updateTotalPrice();
    }
} 
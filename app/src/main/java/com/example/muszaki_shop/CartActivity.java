package com.example.muszaki_shop;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemChangeListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private MaterialButton checkoutButton;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();

        setupRecyclerView();
        loadCartItems();
        setupCheckoutButton();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                cartItems.clear();
                for (var doc : queryDocumentSnapshots) {
                    CartItem item = doc.toObject(CartItem.class);
                    cartItems.add(item);
                }
                cartAdapter.setCartItems(cartItems);
                updateTotalPrice();
            })
            .addOnFailureListener(e -> {
                // TODO: Hiba kezelése
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
            total += item.getTotalPrice();
        }
        totalPriceTextView.setText(String.format("Összesen: %.0f Ft", total));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        updateTotalPrice();
        saveCartItems();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        cartItems.remove(item);
        cartAdapter.setCartItems(cartItems);
        updateTotalPrice();
        saveCartItems();
    }

    private void saveCartItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("cart")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Töröljük a régi elemeket
                for (var doc : queryDocumentSnapshots) {
                    doc.getReference().delete();
                }
                // Mentjük az új elemeket
                for (CartItem item : cartItems) {
                    db.collection("users").document(userId).collection("cart")
                        .add(item);
                }
            });
    }
} 
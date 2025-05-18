package com.example.muszaki_shop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.adapters.CartAdapter;
import com.example.muszaki_shop.models.CartItem;
import com.example.muszaki_shop.models.Order;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartFragment extends Fragment implements CartAdapter.CartItemChangeListener {
    private static final String TAG = "CartFragment";
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private MaterialButton checkoutButton;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();

        setupRecyclerView();
        loadCartItems();
        setupCheckoutButton();

        return view;
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Kérjük, jelentkezzen be a kosár megtekintéséhez", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    Toast.makeText(getContext(), "Hiba a kosár betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupCheckoutButton() {
        checkoutButton.setOnClickListener(v -> processCheckout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems(); // Frissítjük a kosarat amikor visszatérünk erre a képernyőre
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

    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText("Összesen: " + total + " Ft");
    }

    private void saveCartItems() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        String userId = user.getUid();
        
        for (CartItem item : cartItems) {
            db.collection("users")
                .document(userId)
                .collection("cart")
                .document(String.valueOf(item.getProductId()))
                .set(item)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hiba történt a mentés során: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void processCheckout() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Kérjük, jelentkezzen be a fizetéshez", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "A kosár üres", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail();
        String orderId = UUID.randomUUID().toString();
        int totalPrice = 0;

        // Összesítjük a teljes árat
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        // Létrehozzuk az Order objektumot
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setEmail(userEmail);
        order.setCreatedAt((int) (System.currentTimeMillis() / 1000));
        order.setStatus("pending");
        order.setItems(new ArrayList<>(cartItems));
        order.setTotalPrice(totalPrice);

        // Elmentjük az order-t
        db.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    // Sikeres mentés után töröljük a kosarat
                    clearCart(userId, orderId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba az order mentése közben", e);
                    Toast.makeText(getContext(), "Hiba a fizetés feldolgozása közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart(String userId, String orderId) {
        // Töröljük a kosár minden elemét
        for (CartItem item : cartItems) {
            db.collection("users").document(userId)
                    .collection("cart")
                    .document(String.valueOf(item.getProductId()))
                    .delete()
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Hiba a kosár törlése közben", e);
                    });
        }

        // Frissítjük a UI-t
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        
        Toast.makeText(getContext(), "Sikeres fizetés! Rendelés azonosító: " + orderId, Toast.LENGTH_LONG).show();
    }
} 
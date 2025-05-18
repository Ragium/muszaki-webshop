package com.example.muszaki_shop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.CartItem;
import com.example.muszaki_shop.models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productName.setText(product.getTitle());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(product.getPrice() + " Ft");

        // Kép betöltése Glide-dzsel
        Glide.with(context)
            .load(product.getImageUrl())
            .into(holder.productImage);

        holder.addToCartButton.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                CartItem cartItem = new CartItem(
                        product.getProductId(),
                        product.getTitle(),
                        product.getImageUrl(),
                        product.getPrice(),
                        1
                );

                // Ellenőrizzük, hogy a termék már van-e a kosárban
                db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("cart")
                    .document(String.valueOf(product.getId()))
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Ha már van a kosárban, növeljük a mennyiséget
                            CartItem existingItem = documentSnapshot.toObject(CartItem.class);
                            if (existingItem != null) {
                                existingItem.setQuantity(existingItem.getQuantity() + 1);
                                db.collection("users")
                                    .document(auth.getCurrentUser().getUid())
                                    .collection("cart")
                                    .document(String.valueOf(product.getId()))
                                    .set(existingItem)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Termék mennyisége frissítve!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            }
                        } else {
                            // Ha még nincs a kosárban, hozzáadjuk
                            db.collection("users")
                                .document(auth.getCurrentUser().getUid())
                                .collection("cart")
                                .document(String.valueOf(product.getId()))
                                .set(cartItem)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Termék hozzáadva a kosárhoz!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            } else {
                Toast.makeText(context, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productDescription;
        TextView productPrice;
        MaterialButton addToCartButton;

        ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
} 
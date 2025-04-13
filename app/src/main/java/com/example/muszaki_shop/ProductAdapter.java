package com.example.muszaki_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText(product.getPrice() + " Ft");
        holder.imageView.setImageResource(product.getImageResourceId());

        holder.addToCartButton.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                CartItem cartItem = new CartItem(
                    product.getName(),
                    product.getName(),
                    String.valueOf(product.getImageResourceId()),
                    product.getPrice(),
                    1
                );

                String userId = auth.getCurrentUser().getUid();
                db.collection("users").document(userId).collection("cart")
                    .add(cartItem)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(context, "Termék hozzáadva a kosárhoz", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Hiba történt a kosárba rakás során", Toast.LENGTH_SHORT).show();
                    });
            } else {
                Toast.makeText(context, "Kérjük, jelentkezzen be a kosárba rakásához", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        MaterialButton addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameTextView = itemView.findViewById(R.id.productName);
            descriptionTextView = itemView.findViewById(R.id.productDescription);
            priceTextView = itemView.findViewById(R.id.productPrice);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
} 
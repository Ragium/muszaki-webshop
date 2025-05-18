package com.example.muszaki_shop.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";
    private List<CartItem> cartItems;
    private Context context;
    private CartItemChangeListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    public interface CartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.priceTextView.setText(item.getPrice() + " Ft");
        holder.totalPriceTextView.setText((item.getPrice() * item.getQuantity()) + " Ft");

        // Kép betöltése Glide-dzsel
        String imagePath = item.getImageUrl();
        Log.d(TAG, "Loading image from path: " + imagePath);

        if (imagePath != null && !imagePath.isEmpty()) {
            // Firebase Storage referenciájának létrehozása
            StorageReference imageRef = storage.getReference().child(imagePath);
            
            // URL lekérése a Storage referenciából
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "Successfully got download URL: " + uri.toString());
                
                RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image);

                Glide.with(context)
                    .load(uri)
                    .apply(requestOptions)
                    .into(holder.productImageView);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting download URL", e);
                holder.productImageView.setImageResource(R.drawable.error_image);
            });
        } else {
            Log.w(TAG, "Image path is null or empty for item: " + item.getName());
            holder.productImageView.setImageResource(R.drawable.placeholder_image);
        }

        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.quantityTextView.setText(String.valueOf(newQuantity));
            holder.totalPriceTextView.setText((item.getPrice() * newQuantity) + " Ft");
            listener.onQuantityChanged(item, newQuantity);
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                holder.totalPriceTextView.setText((item.getPrice() * newQuantity) + " Ft");
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            String userId = auth.getCurrentUser().getUid();
            String productId = String.valueOf(item.getProductId());
            
            // Törlés a Firestore-ból
            db.collection("users").document(userId)
                .collection("cart")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Sikeres törlés után frissítjük a listát
                    int position1 = holder.getAdapterPosition();
                    if (position1 != RecyclerView.NO_POSITION) {
                        cartItems.remove(position1);
                        notifyItemRemoved(position1);
                        listener.onItemRemoved(item);
                        Toast.makeText(context, "Termék eltávolítva a kosárból", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Hiba a termék törlésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView totalPriceTextView;
        ImageButton increaseButton;
        ImageButton decreaseButton;
        ImageButton removeButton;

        CartViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.cartItemImage);
            productNameTextView = itemView.findViewById(R.id.cartItemName);
            quantityTextView = itemView.findViewById(R.id.quantityText);
            priceTextView = itemView.findViewById(R.id.cartItemPrice);
            totalPriceTextView = itemView.findViewById(R.id.cartItemTotalPrice);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
} 
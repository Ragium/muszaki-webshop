package com.example.muszaki_shop.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.Product;
import com.example.muszaki_shop.models.Review;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private ProductAdapter productAdapter;
    private EditText searchEditText;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    // Glide kép betöltési opciók
    private static final RequestOptions requestOptions = new RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.placeholder_image)
        .error(R.drawable.error_image)
        .centerCrop()
        .override(300, 300); // Kép méretezése 300x300 pixelre

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        searchEditText = view.findViewById(R.id.searchEditText);
        RecyclerView recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        
        // Beállítjuk a RecyclerView-t
        productAdapter = new ProductAdapter(filteredProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productAdapter);
        
        // Termékek betöltése a Firestore-ból
        loadProducts();
        
        // Keresés figyelése
        setupSearchListener();
        
        return view;
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allProducts.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    product.setProductId(document.getId());  // Beállítjuk a dokumentum ID-t
                    allProducts.add(product);
                }
                // Kezdetben minden termék megjelenik
                filteredProducts.clear();
                filteredProducts.addAll(allProducts);
                productAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Hiba a termékek betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            query = query.toLowerCase();
            for (Product product : allProducts) {
                if (product.getTitle().toLowerCase().contains(query) ||
                    product.getDescription().toLowerCase().contains(query) ||
                    product.getCategory().toLowerCase().contains(query)) {
                    filteredProducts.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
        private List<Product> products;

        public ProductAdapter(List<Product> products) {
            this.products = products;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = products.get(position);
            holder.titleTextView.setText(product.getTitle());
            holder.descriptionTextView.setText(product.getDescription());
            holder.priceTextView.setText(product.getPrice() + " Ft");

            // Értékelések betöltése
            db.collection("products")
                .document(product.getProductId())
                .collection("reviews")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        float totalRating = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Review review = document.toObject(Review.class);
                            totalRating += review.getRating();
                        }
                        float averageRating = totalRating / queryDocumentSnapshots.size();
                        holder.ratingBar.setRating(averageRating);
                        holder.ratingCount.setText("(" + queryDocumentSnapshots.size() + ")");
                    } else {
                        holder.ratingBar.setRating(0);
                        holder.ratingCount.setText("(0)");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba az értékelések betöltésekor: " + e.getMessage());
                    holder.ratingBar.setRating(0);
                    holder.ratingCount.setText("(0)");
                });

            // Kép betöltése Glide-dzsel
            String imagePath = product.getImageUrl();
            Log.d(TAG, "Loading image from path: " + imagePath);

            if (imagePath != null && !imagePath.isEmpty()) {
                // Firebase Storage referenciájának létrehozása
                StorageReference imageRef = storage.getReference().child(imagePath);
                
                // URL lekérése a Storage referenciából
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Successfully got download URL: " + uri.toString());
                    
                    Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade(300)) // Áttűnés effekt
                        .into(holder.imageView);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting download URL", e);
                    holder.imageView.setImageResource(R.drawable.error_image);
                });
            } else {
                Log.w(TAG, "Image path is null or empty for product: " + product.getTitle());
                holder.imageView.setImageResource(R.drawable.placeholder_image);
            }

            // Kosárba gomb kezelése
            holder.addToCartButton.setOnClickListener(v -> {
                if (auth.getCurrentUser() != null) {
                    addToCart(product);
                } else {
                    Toast.makeText(getContext(), "Kérjük, jelentkezzen be a kosár használatához", Toast.LENGTH_SHORT).show();
                }
            });

            // Értékelés gomb kezelése
            holder.writeReviewButton.setOnClickListener(v -> {
                if (auth.getCurrentUser() != null) {
                    Bundle bundle = new Bundle();
                    String productId = product.getProductId();
                    String productName = product.getTitle();
                    Log.d(TAG, "Opening review for productId: " + productId);
                    bundle.putString("productId", productId);
                    bundle.putString("productName", productName);

                    ProductReviewFragment reviewFragment = new ProductReviewFragment();
                    reviewFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contentFrame, reviewFragment)
                        .addToBackStack(null)
                        .commit();
                } else {
                    Toast.makeText(getContext(), "Kérjük, jelentkezzen be az értékelés írásához", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addToCart(Product product) {
            String userId = auth.getCurrentUser().getUid();
            String productId = product.getProductId();  // A dokumentum ID-t használjuk

            // Ellenőrizzük, hogy a termék már van-e a kosárban
            db.collection("users").document(userId)
                .collection("cart")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ha már van a kosárban, növeljük a mennyiséget
                        long currentQuantity = documentSnapshot.getLong("quantity");
                        updateCartItemQuantity(userId, productId, currentQuantity + 1);
                    } else {
                        // Ha nincs a kosárban, hozzáadjuk
                        Map<String, Object> cartItem = new HashMap<>();
                        cartItem.put("productId", productId);  // A dokumentum ID-t használjuk
                        cartItem.put("name", product.getTitle());
                        cartItem.put("price", product.getPrice());
                        cartItem.put("imageUrl", product.getImageUrl());
                        cartItem.put("quantity", 1);

                        db.collection("users").document(userId)
                            .collection("cart")
                            .document(productId)
                            .set(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Termék hozzáadva a kosárhoz", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Hiba a termék kosárba helyezésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hiba a kosár ellenőrzésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }

        private void updateCartItemQuantity(String userId, String productId, long newQuantity) {
            db.collection("users").document(userId)
                .collection("cart")
                .document(productId)
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Kosár frissítve", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hiba a kosár frissítésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView titleTextView;
            TextView descriptionTextView;
            TextView priceTextView;
            RatingBar ratingBar;
            TextView ratingCount;
            MaterialButton addToCartButton;
            MaterialButton writeReviewButton;

            ProductViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.productImage);
                titleTextView = itemView.findViewById(R.id.productName);
                descriptionTextView = itemView.findViewById(R.id.productDescription);
                priceTextView = itemView.findViewById(R.id.productPrice);
                ratingBar = itemView.findViewById(R.id.productRating);
                ratingCount = itemView.findViewById(R.id.ratingCount);
                addToCartButton = itemView.findViewById(R.id.addToCartButton);
                writeReviewButton = itemView.findViewById(R.id.writeReviewButton);
            }
        }
    }
} 
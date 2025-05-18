package com.example.muszaki_shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.adapters.ProductAdapter;
import com.example.muszaki_shop.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {
    private ProductAdapter productAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.productsRecyclerView);
        db = FirebaseFirestore.getInstance();
        setupProductsList(recyclerView);
        return view;
    }

    private void setupProductsList(RecyclerView recyclerView) {
        List<Product> products = new ArrayList<>();
        productAdapter = new ProductAdapter(products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(productAdapter);

        // Termékek betöltése a Firestore-ból
        db.collection("products")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                products.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Product product = document.toObject(Product.class);
                    products.add(product);
                }
                productAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Hiba a termékek betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
} 
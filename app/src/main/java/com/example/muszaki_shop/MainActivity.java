package com.example.muszaki_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Bejelentkezési állapot ellenőrzése
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Ha be van jelentkezve, akkor megjelenítjük a kijelentkezési gombot
            binding.loginButton.setVisibility(View.GONE);
            binding.registerButton.setVisibility(View.GONE);
            binding.logoutButton.setVisibility(View.VISIBLE);
            binding.profileButton.setVisibility(View.VISIBLE);
            binding.cartButton.setVisibility(View.VISIBLE);
            binding.welcomeText.setText("Üdvözöljük a Műszaki Webshopban, " + currentUser.getEmail() + "!");
        } else {
            // Ha nincs bejelentkezve, akkor megjelenítjük a bejelentkezési és regisztrációs gombokat
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
            binding.logoutButton.setVisibility(View.GONE);
            binding.profileButton.setVisibility(View.GONE);
            binding.cartButton.setVisibility(View.GONE);
            binding.welcomeText.setText("Üdvözöljük a Műszaki Webshopban!");
        }

        // Gombok eseménykezelői
        binding.loginButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        binding.registerButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        binding.profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        binding.cartButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            showLoading(true);
            binding.loginButton.setVisibility(View.VISIBLE);
            binding.registerButton.setVisibility(View.VISIBLE);
            binding.logoutButton.setVisibility(View.GONE);
            binding.profileButton.setVisibility(View.GONE);
            binding.cartButton.setVisibility(View.GONE);
            binding.welcomeText.setText("Üdvözöljük a Műszaki Webshopban!");
        });

        // Termékek listázása
        setupProductsList();
    }

    private void showLoading(boolean show) {
        binding.loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!show);
        binding.registerButton.setEnabled(!show);
        binding.profileButton.setEnabled(!show);
        binding.cartButton.setEnabled(!show);
    }

    private void setupProductsList() {
        // Termékek létrehozása
        List<Product> products = new ArrayList<>();
        products.add(new Product("Bosch GSR 18V-EC", "Akkumulátoros csavarbehajtó", 45000, R.drawable.placeholder));
        products.add(new Product("Makita DHP481Z", "Akkumulátoros kalapácsfúró", 55000, R.drawable.placeholder));
        products.add(new Product("DeWalt DCD777C2", "Akkumulátoros csavarbehajtó", 48000, R.drawable.placeholder));
        products.add(new Product("Milwaukee M18", "Akkumulátoros fúrócsavarozó", 52000, R.drawable.placeholder));
        products.add(new Product("Hitachi DV18DBL", "Akkumulátoros csavarbehajtó", 42000, R.drawable.placeholder));
        products.add(new Product("Ryobi R18PD3", "Akkumulátoros csavarbehajtó", 38000, R.drawable.placeholder));
        products.add(new Product("Einhell TE-CD 18/50", "Akkumulátoros csavarbehajtó", 35000, R.drawable.placeholder));
        products.add(new Product("Metabo BS 18 LTX", "Akkumulátoros csavarbehajtó", 40000, R.drawable.placeholder));

        // RecyclerView beállítása
        productAdapter = new ProductAdapter(products);
        binding.productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.productsRecyclerView.setAdapter(productAdapter);
    }
}
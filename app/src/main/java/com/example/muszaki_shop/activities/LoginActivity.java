package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Google bejelentkezés konfigurálása
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        binding.registerButton.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        // Google bejelentkezés kezelése
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(LoginActivity.this, "Google bejelentkezés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    private void showLoading(boolean show) {
        binding.loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!show);
        binding.googleSignInButton.setEnabled(!show);
        binding.registerButton.setEnabled(!show);
    }

    private void loginUser() {
        String email = binding.emailLayout.getEditText().getText().toString().trim();
        String password = binding.passwordLayout.getEditText().getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Kérlek töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Hibás email vagy jelszó!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        showLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        // Ellenőrizzük, hogy a felhasználó már regisztrált-e
                        checkUserRegistration();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRegistration() {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Ha még nincs regisztrálva, akkor továbbítjuk a profil kiegészítő oldalra
                        Intent intent = new Intent(LoginActivity.this, CompleteProfileActivity.class);
                        intent.putExtra("isGoogleSignIn", true);
                        startActivity(intent);
                    } else {
                        // Ha már regisztrált, akkor továbbítjuk a főoldalra
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Hiba történt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
} 
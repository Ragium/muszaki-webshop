package com.example.muszaki_shop.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.adapters.OrderAdapter;
import com.example.muszaki_shop.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyOrdersText;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private static final String TAG = "OrdersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        
        recyclerView = view.findViewById(R.id.ordersRecyclerView);
        emptyOrdersText = view.findViewById(R.id.emptyOrdersText);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(adapter);
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        loadOrders();
        
        return view;
    }

    private void loadOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Nincs bejelentkezett felhasználó");
            return;
        }

        Log.d(TAG, "Rendelések betöltése kezdődik");
        Log.d(TAG, "Felhasználó ID: " + currentUser.getUid());

        db.collection("orders")
            .whereEqualTo("userId", currentUser.getUid())
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.d(TAG, "Rendelések lekérdezése sikeres");
                Log.d(TAG, "Talált rendelések száma: " + queryDocumentSnapshots.size());
                
                orderList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        Order order = document.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(document.getId());
                            orderList.add(order);
                            Log.d(TAG, "Rendelés sikeresen betöltve: " + document.getId());
                        } else {
                            Log.e(TAG, "A rendelés null: " + document.getId());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Hiba a rendelés konvertálásakor: " + document.getId(), e);
                    }
                }
                
                if (orderList.isEmpty()) {
                    Log.d(TAG, "Nincsenek rendelések");
                    emptyOrdersText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "Rendelések megjelenítése");
                    emptyOrdersText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                
                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Hiba a rendelések betöltésekor", e);
                Log.e(TAG, "Hiba típusa: " + e.getClass().getName());
                Log.e(TAG, "Hiba üzenet: " + e.getMessage());
                if (e instanceof FirebaseFirestoreException) {
                    FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
                    Log.e(TAG, "Firestore hiba kód: " + firestoreException.getCode());
                }
                Toast.makeText(getContext(), "Hiba a rendelések betöltésekor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
} 
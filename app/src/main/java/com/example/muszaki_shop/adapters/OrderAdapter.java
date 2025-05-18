package com.example.muszaki_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.orderIdText.setText("Rendelés #" + order.getOrderId());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("hu"));
        holder.orderDateText.setText("Dátum: " + dateFormat.format(order.getCreatedAt()));
        
        holder.orderStatusText.setText("Státusz: " + getStatusText(order.getStatus()));
        holder.orderTotalText.setText("Összesen: " + order.getTotalPrice() + " Ft");
        
        // Rendelés tételek megjelenítése
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getItems());
        holder.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.orderItemsRecyclerView.setAdapter(itemAdapter);
    }

    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "Feldolgozás alatt";
            case "processing":
                return "Feldolgozva";
            case "shipped":
                return "Szállítás alatt";
            case "delivered":
                return "Kiszállítva";
            case "cancelled":
                return "Visszamondva";
            default:
                return status;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText;
        TextView orderDateText;
        TextView orderStatusText;
        TextView orderTotalText;
        RecyclerView orderItemsRecyclerView;

        OrderViewHolder(View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            orderItemsRecyclerView = itemView.findViewById(R.id.orderItemsRecyclerView);
        }
    }
} 
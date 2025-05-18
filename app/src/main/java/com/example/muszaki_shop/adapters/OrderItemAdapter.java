package com.example.muszaki_shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.CartItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<CartItem> items;

    public OrderItemAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        CartItem item = items.get(position);
        
        holder.itemNameText.setText(item.getName());
        holder.itemQuantityText.setText("Mennyiség: " + item.getQuantity());
        holder.itemPriceText.setText(item.getPrice() + " Ft");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameText;
        TextView itemQuantityText;
        TextView itemPriceText;

        OrderItemViewHolder(View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.itemNameText);
            itemQuantityText = itemView.findViewById(R.id.itemQuantityText);
            itemPriceText = itemView.findViewById(R.id.itemPriceText);
        }
    }
} 
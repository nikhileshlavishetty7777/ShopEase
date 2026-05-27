package com.example.newEcom.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newEcom.R;
import com.example.newEcom.model.CartItemModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CartAdapter extends FirestoreRecyclerAdapter<CartItemModel, CartAdapter.CartViewHolder> {

    Context context;

    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartItemModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_adapter, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartItemModel model) {

        holder.productName.setText(model.getName());
        holder.singleProductPrice.setText("₹ " + model.getPrice());
        holder.productPrice.setText("₹ " + (model.getPrice() * model.getQuantity()));
        holder.productQuantity.setText(String.valueOf(model.getQuantity()));
        holder.originalPrice.setText("₹ " + model.getOriginalPrice());

        holder.originalPrice.setPaintFlags(
                holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );

        Picasso.get().load(model.getImage()).into(holder.productCartImage);

        holder.plusBtn.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                changeQuantity(getItem(pos), true);
            }
        });

        holder.minusBtn.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                changeQuantity(getItem(pos), false);
            }
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        int total = 0;

        for (int i = 0; i < getItemCount(); i++) {
            CartItemModel item = getItem(i);
            if (item != null) {
                total += item.getPrice() * item.getQuantity();
            }
        }

        // send total
        Intent intent = new Intent("price");
        intent.putExtra("totalPrice", total);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        // hide shimmer
        Activity activity = (Activity) context;

        ShimmerFrameLayout shimmer = activity.findViewById(R.id.shimmerLayout);
        LinearLayout mainLayout = activity.findViewById(R.id.mainLinearLayout);
        ImageView empty = activity.findViewById(R.id.emptyCartImageView);

        if (shimmer != null) {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }

        if (mainLayout != null) {
            mainLayout.setVisibility(View.VISIBLE);
        }

        if (empty != null) {
            empty.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void changeQuantity(CartItemModel model, boolean plus) {

        FirebaseUtil.getCartItems()
                .whereEqualTo("productId", model.getProductId())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {

                            String docId = doc.getId();
                            int quantity = ((Long) doc.get("quantity")).intValue();

                            if (plus) {
                                FirebaseUtil.getCartItems()
                                        .document(docId)
                                        .update("quantity", quantity + 1);
                            } else {
                                if (quantity > 1) {
                                    FirebaseUtil.getCartItems()
                                            .document(docId)
                                            .update("quantity", quantity - 1);
                                } else {
                                    FirebaseUtil.getCartItems()
                                            .document(docId)
                                            .delete();
                                }
                            }
                        }
                    }
                });
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productName, productPrice, singleProductPrice, productQuantity, originalPrice;
        TextView plusBtn, minusBtn;
        ImageView productCartImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.nameTextView);
            singleProductPrice = itemView.findViewById(R.id.priceTextView1);
            productPrice = itemView.findViewById(R.id.priceTextView);
            productQuantity = itemView.findViewById(R.id.quantityTextView);
            originalPrice = itemView.findViewById(R.id.originalPrice);

            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            productCartImage = itemView.findViewById(R.id.productImageCart);
        }
    }
}
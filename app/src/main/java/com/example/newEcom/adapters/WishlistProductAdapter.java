package com.example.newEcom.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newEcom.R;
import com.example.newEcom.activities.MainActivity;
import com.example.newEcom.fragments.ProductFragment;
import com.example.newEcom.model.CartItemModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.*;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;
import com.squareup.picasso.Picasso;

public class WishlistProductAdapter extends FirestoreRecyclerAdapter<CartItemModel, WishlistProductAdapter.WishlistProductViewHolder> {

    private Context context;
    private AppCompatActivity activity;

    public WishlistProductAdapter(@NonNull FirestoreRecyclerOptions<CartItemModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public WishlistProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist_adapter, parent, false);
        activity = (AppCompatActivity) view.getContext();
        return new WishlistProductViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull WishlistProductViewHolder holder, int position, @NonNull CartItemModel product) {

        holder.productNameTextView.setText(product.getName());
        Picasso.get().load(product.getImage()).into(holder.productImageView);

        int price = product.getPrice();
        int originalPrice = product.getOriginalPrice();

        holder.productPriceTextView.setText("₹ " + price);
        holder.originalPrice.setText("₹ " + originalPrice);
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // ✅ FIXED DISCOUNT CALCULATION
        if (originalPrice > 0 && price <= originalPrice) {
            int discountPerc = ((originalPrice - price) * 100) / originalPrice;
            holder.discountPercentage.setText(discountPerc + "% OFF");
        } else {
            holder.discountPercentage.setText("");
        }

        // 🔹 Open product
        holder.productLinearLayout.setOnClickListener(v -> {
            Fragment fragment = ProductFragment.newInstance(product.getProductId());
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // 🔹 Add to cart
        holder.addToCartBtn.setOnClickListener(v -> {
            addToCart(product, stock -> {

                FirebaseUtil.getCartItems().whereEqualTo("productId", product.getProductId())
                        .get().addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                boolean exists = false;

                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    exists = true;

                                    String docId = doc.getId();
                                    int quantity = (int) (long) doc.get("quantity");

                                    if (quantity < stock) {
                                        FirebaseUtil.getCartItems().document(docId)
                                                .update("quantity", quantity + 1);
                                        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Max stock: " + stock, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                if (!exists) {
                                    CartItemModel item = new CartItemModel(
                                            product.getProductId(),
                                            product.getName(),
                                            product.getImage(),
                                            1,
                                            product.getPrice(),
                                            product.getOriginalPrice(),
                                            Timestamp.now()
                                    );

                                    FirebaseUtil.getCartItems().add(item);
                                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                }

                                ((MainActivity) context).addOrRemoveBadge();
                            }
                        });
            });
        });

        // 🔹 Remove from wishlist
        holder.removeWishlistBtn.setOnClickListener(v -> {
            FirebaseUtil.getWishlistItems()
                    .whereEqualTo("productId", product.getProductId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                FirebaseUtil.getWishlistItems().document(doc.getId()).delete();
                            }
                        }
                    });
        });
    }

    // 🔹 Get stock
    private void addToCart(CartItemModel product, MyCallback callback) {

        FirebaseUtil.getProducts()
                .whereEqualTo("productId", product.getProductId())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            int stock = (int) (long) doc.get("stock");
                            callback.onCallback(stock);
                        }
                    }
                });
    }

    // 🔹 ViewHolder
    public static class WishlistProductViewHolder extends RecyclerView.ViewHolder {

        TextView productNameTextView, productPriceTextView, originalPrice, discountPercentage;
        ImageView productImageView;
        LinearLayout productLinearLayout;
        Button addToCartBtn, removeWishlistBtn;

        public WishlistProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.productImage);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountPercentage = itemView.findViewById(R.id.discountPercentage);
            productLinearLayout = itemView.findViewById(R.id.productLinearLayout);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            removeWishlistBtn = itemView.findViewById(R.id.removeWishlistBtn);
        }
    }

    // 🔹 Callback
    public interface MyCallback {
        void onCallback(int stock);
    }
}
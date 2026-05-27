package com.example.newEcom.adapters;

import static com.example.newEcom.fragments.ProductFragment.*;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newEcom.R;
import com.example.newEcom.fragments.ProductFragment;
import com.example.newEcom.model.ProductModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProductAdapter extends FirestoreRecyclerAdapter<ProductModel, ProductAdapter.ProductViewHolder> {

    private Context context;
    private AppCompatActivity activity;

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<ProductModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_product_adapter, parent, false);
        activity = (AppCompatActivity) view.getContext();

        return new ProductViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductModel product) {

        // ✅ DEBUG (check data)
        Log.d("PRODUCT_DEBUG", product.getName());

        // ✅ Image with fallback
        Picasso.get()
                .load(product.getImage())
                .into(holder.productImage, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError(Exception e) {
                        holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                    }
                });

        // Product name
        holder.productLabel.setText(product.getName());

        // Price
        holder.productPrice.setText("₹ " + product.getPrice());
        holder.originalPrice.setText("₹ " + product.getOriginalPrice());

        // Strike price
        holder.originalPrice.setPaintFlags(
                holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
        );

        // ✅ Safe discount calculation
        int discountPerc = 0;

        if (product.getOriginalPrice() > 0 && product.getDiscount() > 0) {
            discountPerc = (product.getDiscount() * 100) / product.getOriginalPrice();
        }

        holder.discountPercentage.setText(discountPerc + "% OFF");

        // Click
        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("productObj", product);

            ProductFragment fragment = new ProductFragment();
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productLabel, productPrice, originalPrice, discountPercentage;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productListImage);
            productLabel = itemView.findViewById(R.id.productLabel);
            productPrice = itemView.findViewById(R.id.productPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountPercentage = itemView.findViewById(R.id.discountPercentage);
        }
    }
}
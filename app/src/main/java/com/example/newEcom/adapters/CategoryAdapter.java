package com.example.newEcom.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newEcom.R;
import com.example.newEcom.fragments.CategoryFragment;
import com.example.newEcom.model.CategoryModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class CategoryAdapter extends FirestoreRecyclerAdapter<CategoryModel, CategoryAdapter.ViewHolder> {

    Context context;
    AppCompatActivity activity;

    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<CategoryModel> options, Context context) {
        super(options);
        this.context = context;

        // RecyclerView crash fix
        setHasStableIds(true);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CategoryModel model) {

        holder.categoryLabel.setText(model.getName());

        // Load image
        Picasso.get()
                .load(model.getIcon())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.categoryImage);

        // Safe color parsing
        try {
            holder.categoryImage.setBackgroundColor(Color.parseColor(model.getColor()));
        } catch (Exception e) {
            holder.categoryImage.setBackgroundColor(Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("category", model.getName());

            CategoryFragment fragment = new CategoryFragment();
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public long getItemId(int position) {
        return getSnapshots().getSnapshot(position).getId().hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category_adapter, parent, false);

        activity = (AppCompatActivity) view.getContext();

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryLabel;
        ImageView categoryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryLabel = itemView.findViewById(R.id.categoryLabel);
            categoryImage = itemView.findViewById(R.id.categoryImage);
        }
    }
}
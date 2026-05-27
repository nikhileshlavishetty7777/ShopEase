package com.example.newEcom.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newEcom.R;
import com.example.newEcom.adapters.ProductAdapter;
import com.example.newEcom.model.ProductModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class CategoryFragment extends Fragment {

    RecyclerView recyclerView;
    ProductAdapter adapter;
    String categoryName;

    public CategoryFragment() {}

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.categoryProductRecycler);

        if(getArguments()!=null){
            categoryName = getArguments().getString("category");
        }

        loadProducts();

        return view;
    }

    private void loadProducts(){

        Query query = FirebaseUtil
                .getProducts()
                .whereEqualTo("category", categoryName);

        FirestoreRecyclerOptions<ProductModel> options =
                new FirestoreRecyclerOptions.Builder<ProductModel>()
                        .setQuery(query, ProductModel.class)
                        .build();

        adapter = new ProductAdapter(options, getContext());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
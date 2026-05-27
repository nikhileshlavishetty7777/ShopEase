package com.example.newEcom.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.newEcom.R;
import com.example.newEcom.activities.MainActivity;
import com.example.newEcom.adapters.CategoryAdapter;
import com.example.newEcom.adapters.ProductAdapter;
import com.example.newEcom.model.CategoryModel;
import com.example.newEcom.model.ProductModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

public class HomeFragment extends Fragment {

    RecyclerView categoryRecyclerView, productRecyclerView;
    ImageCarousel carousel;

    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout mainLinearLayout;

    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        carousel = view.findViewById(R.id.carousel);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        mainLinearLayout = view.findViewById(R.id.mainLinearLayout);

        MainActivity activity = (MainActivity) getActivity();
        if(activity!=null){
            activity.showSearchBar();
        }

        shimmerFrameLayout.startShimmer();

        initCarousel();
        initCategories();
        initProducts();

        return view;
    }

    // ------------------ Banner ------------------

    private void initCarousel(){

        FirebaseUtil.getBanner()
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful() && task.getResult() != null){

                        for (var document : task.getResult()){

                            carousel.addData(
                                    new CarouselItem(
                                            document.getString("bannerImage")
                                    )
                            );
                        }
                    }

                    // ✅ ALWAYS RUN (no stuck UI)
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    mainLinearLayout.setVisibility(View.VISIBLE);
                });
    }

    // ------------------ Categories ------------------

    private void initCategories(){

        Query query = FirebaseUtil
                .getCategories()
                .orderBy("categoryId");

        FirestoreRecyclerOptions<CategoryModel> options =
                new FirestoreRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(query, CategoryModel.class)
                        .build();

        categoryAdapter = new CategoryAdapter(options,getContext());

        categoryRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(),4)
        );

        categoryRecyclerView.setItemAnimator(null);

        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    // ------------------ Products ------------------

    private void initProducts(){

        Query query = FirebaseUtil
                .getProducts()
                .orderBy("name");

        FirestoreRecyclerOptions<ProductModel> options =
                new FirestoreRecyclerOptions.Builder<ProductModel>()
                        .setQuery(query, ProductModel.class)
                        .build();

        productAdapter = new ProductAdapter(options, requireContext());

        productRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2)
        );

        productRecyclerView.setItemAnimator(null);
        productRecyclerView.setAdapter(productAdapter);

        // ✅ SHIMMER FIX (MAIN SOLUTION)
        productAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                mainLinearLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    // ------------------ Lifecycle ------------------

    @Override
    public void onStart() {
        super.onStart();

        if(categoryAdapter!=null){
            categoryAdapter.startListening();
        }

        if(productAdapter!=null){
            productAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(categoryAdapter!=null){
            categoryAdapter.stopListening();
        }

        if(productAdapter!=null){
            productAdapter.stopListening();
        }
    }
}
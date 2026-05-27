package com.example.newEcom.fragments;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.newEcom.R;
import com.example.newEcom.activities.MainActivity;
import com.example.newEcom.adapters.OrderListAdapter;
import com.example.newEcom.adapters.ProductAdapter;
import com.example.newEcom.adapters.ReviewAdapter;
import com.example.newEcom.model.CartItemModel;
import com.example.newEcom.model.OrderItemModel;
import com.example.newEcom.model.ProductModel;
import com.example.newEcom.model.ReviewModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

public class ProductFragment extends Fragment {
    ImageView productImage, backBtn, shareBtn;
    TextView productName, productDescription, productSpec;
    TextView productPrice, originalPrice, discountPercentage;
    TextView ratingTextView, noOfRatingTextView;
    Button addToCartBtn;
    MaterialCardView wishlistBtn;
    ImageView wishlistImageView;
    RatingBar ratingBar;

    LottieAnimationView wishlistLottie, cartLottie;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView reviewRecyclerView, similarProductRecyclerView;
    ReviewAdapter reviewAdapter;
    ProductAdapter similarProductAdapter;
    LinearLayout mainLinearlayout;

    int productId;
    boolean wishlisted = false;

    ProductModel currentProduct = new ProductModel();

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        productDescription = view.findViewById(R.id.productDescription);
        productSpec = view.findViewById(R.id.productSpecification);
        addToCartBtn = view.findViewById(R.id.addToCartBtn);
        wishlistBtn = view.findViewById(R.id.wishlistBtn);
        wishlistImageView = view.findViewById(R.id.wishlistImageView);
        backBtn = view.findViewById(R.id.backBtn);
        shareBtn = view.findViewById(R.id.shareBtn);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingTextView = view.findViewById(R.id.ratingTextView);
        noOfRatingTextView = view.findViewById(R.id.noOfRatingTextView);

        wishlistLottie = view.findViewById(R.id.wishlistLottie);
        cartLottie = view.findViewById(R.id.cartLottie);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        similarProductRecyclerView = view.findViewById(R.id.similarProductRecyclerView);
        mainLinearlayout = view.findViewById(R.id.mainLinearLayout);

        MainActivity activity = (MainActivity) getActivity();
        activity.hideSearchBar();
        shimmerFrameLayout.startShimmer();
        cartLottie.setVisibility(View.GONE);

        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
        }
        if (getArguments().getSerializable("productObj") != null) {
            currentProduct = (ProductModel) getArguments().getSerializable("productObj");
            getProduct(currentProduct);
        } else {
            initProduct(new FirestoreCallback() {
                @Override
                public void onCallback(ProductModel productModel) {
                    currentProduct = productModel;
                    getProduct(productModel);
                }

                @Override
                public void onCallback(int stock) {

                }
            });

        }

        backBtn.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        return view;
    }

    private void initProduct(FirestoreCallback callback) {
        FirebaseUtil.getProducts().whereEqualTo("productId", productId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                currentProduct = document.toObject(ProductModel.class);
//                                currentProduct = new ProductModel(document.get("name").toString(), (List<String>) document.get("searchKey"), document.get("image").toString(), document.get("category").toString(),
//                                        (int) (long) document.get("price"), (int) (long) document.get("discount"), productId, (int) (long) document.get("stock"), document.get("shareLink").toString());

                                callback.onCallback(currentProduct);
                            }
                        }
                    }
                });
    }

    private void getProduct(ProductModel currentProduct) {

        shareBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, currentProduct.getName());
            intent.putExtra(Intent.EXTRA_TEXT, currentProduct.getName() + "\n" + currentProduct.getShareLink());
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        mainLinearlayout.setVisibility(View.VISIBLE);

        // SAFE IMAGE LOAD
        if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
            Picasso.get().load(currentProduct.getImage()).into(productImage);
        }

        // Product name
        productName.setText(currentProduct.getName());

// Safe image load
        if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
            Picasso.get().load(currentProduct.getImage()).into(productImage);
        }

// Get values
        int price = currentProduct.getPrice();
        int discount = currentProduct.getDiscount();

// Calculate original price
        int originalPriceValue = price + discount;

// Calculate discount percentage
        int discountPerc = 0;

        if (originalPriceValue > 0) {
            discountPerc = (discount * 100) / originalPriceValue;
        }

// Set UI
        productPrice.setText("₹ " + price);
        originalPrice.setText("₹ " + originalPriceValue);

// Strike line
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        discountPercentage.setText(discountPerc + "% OFF");

        // SAFE RATING
        float rating = 0f;
        try {
            DecimalFormat df = new DecimalFormat("#.#");
            rating = Float.parseFloat(df.format(currentProduct.getRating()));
        } catch (Exception e) {
            rating = 0f;
        }

        ratingBar.setRating(rating);
        ratingTextView.setText(String.valueOf(rating));
        noOfRatingTextView.setText("(" + currentProduct.getNoOfRating() + ")");

        productDescription.setText(currentProduct.getDescription());
        productSpec.setText(currentProduct.getSpecification());

        // WISHLIST CHECK
        FirebaseUtil.getWishlistItems()
                .whereEqualTo("productId", currentProduct.getProductId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            wishlistImageView.setImageResource(R.drawable.ic_wishlist);
                            wishlisted = true;
                        } else {
                            wishlistImageView.setImageResource(R.drawable.ic_not_wishlisted);
                        }
                    }
                });

        getReviews();
        getSimilarProducts();

        // ADD TO CART BUTTON
        addToCartBtn.setOnClickListener(v -> {

            FirebaseUtil.getCartItems()
                    .whereEqualTo("productId", currentProduct.getProductId())
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            // ✅ Product already exists → update quantity
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                int quantity = ((Long) doc.get("quantity")).intValue();

                                FirebaseUtil.getCartItems()
                                        .document(doc.getId())
                                        .update("quantity", quantity + 1);
                            }

                            Toast.makeText(getActivity(), "Quantity Updated", Toast.LENGTH_SHORT).show();

                        } else {

                            // ✅ New product → add fresh
                            CartItemModel cartItem = new CartItemModel(
                                    currentProduct.getProductId(),
                                    currentProduct.getName(),
                                    currentProduct.getImage(),
                                    1,
                                    currentProduct.getPrice(),
                                    currentProduct.getPrice() + currentProduct.getDiscount(),
                                    Timestamp.now()
                            );

                            FirebaseUtil.getCartItems()
                                    .add(cartItem)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }

                        // update badge
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null) {
                            activity.addOrRemoveBadge();
                        }
                    });
        });

        // WISHLIST BUTTON
        wishlistBtn.setOnClickListener(v -> {

            if (!wishlisted) {

                wishlistImageView.setVisibility(View.GONE);

                CartItemModel wishlistItem = new CartItemModel(
                        currentProduct.getProductId(),
                        currentProduct.getName(),
                        currentProduct.getImage(),
                        1,
                        currentProduct.getPrice(),
                        currentProduct.getOriginalPrice(),
                        Timestamp.now()
                );

                FirebaseUtil.getWishlistItems().add(wishlistItem);

                Toast.makeText(getActivity(),
                        "Added to Wishlist",
                        Toast.LENGTH_SHORT).show();

                wishlistLottie.playAnimation();

                wishlisted = true;

            } else {

                Toast.makeText(getActivity(),
                        "Product already in wishlist!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStock(FirestoreCallback callback) {
        FirebaseUtil.getProducts().whereEqualTo("productId", currentProduct.getProductId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int stock = (int) (long) document.getData().get("stock");
                                callback.onCallback(stock);
                            }
                        }
                    }
                });
    }

    private void getReviews() {
        Query query = FirebaseUtil.getReviews(currentProduct.getProductId()).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>()
                .setQuery(query, ReviewModel.class)
                .build();

        reviewAdapter = new ReviewAdapter(options, getActivity());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewAdapter.startListening();
    }

    private void getSimilarProducts() {

        Query query = FirebaseUtil.getProducts()
                .whereEqualTo("category", currentProduct.getCategory())
                .limit(8);

        FirestoreRecyclerOptions<ProductModel> options =
                new FirestoreRecyclerOptions.Builder<ProductModel>()
                        .setQuery(query, ProductModel.class)
                        .build();

        similarProductAdapter = new ProductAdapter(options, getActivity());

        similarProductRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

        similarProductRecyclerView.setAdapter(similarProductAdapter);
        similarProductAdapter.startListening();
    }

//    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            rating = intent.getFloatExtra("rating", 0);
//            noOfRating = intent.getIntExtra("noOfRating", 0);
//            ratingBar.setRating(rating);
//            ratingTextView.setText(rating+"");
//            noOfRatingTextView.setText("("+noOfRating+")");
//        }
//    };

    public interface FirestoreCallback {
        void onCallback(ProductModel currentProduct);

        void onCallback(int stock);
    }

    public static ProductFragment newInstance(int productId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        fragment.setArguments(args);

        return fragment;
    }

    public static ProductFragment newInstance(ProductModel product) {
        ProductFragment fragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productObj", product);
        fragment.setArguments(bundle);

        return fragment;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("rating"));
//    }
}
package com.example.newEcom.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newEcom.R;
import com.example.newEcom.activities.CheckoutActivity;
import com.example.newEcom.activities.MainActivity;
import com.example.newEcom.adapters.CartAdapter;
import com.example.newEcom.model.CartItemModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class CartFragment extends Fragment {

    TextView cartPriceTextView;
    RecyclerView cartRecyclerView;
    Button continueBtn;
    ImageView backBtn, emptyCartImageView;
    CartAdapter cartAdapter;

    int totalPrice = 0;

    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout mainLinearLayout;

    public CartFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartPriceTextView = view.findViewById(R.id.cartPriceTextView);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        continueBtn = view.findViewById(R.id.continueBtn);
        backBtn = view.findViewById(R.id.backBtn);
        emptyCartImageView = view.findViewById(R.id.emptyCartImageView);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        mainLinearLayout = view.findViewById(R.id.mainLinearLayout);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.hideSearchBar();
        }

        shimmerFrameLayout.startShimmer();
        mainLinearLayout.setVisibility(View.GONE);

        setupRecyclerView();

        continueBtn.setOnClickListener(v -> {
            if (totalPrice == 0) {
                Toast.makeText(getActivity(),
                        "Cart is empty!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            intent.putExtra("price", totalPrice);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
            if (activity != null) {
                activity.onBackPressed();
            }
        });

        return view;
    }

    private void setupRecyclerView() {

        // ✅ IMPORTANT: NO orderBy (avoid crash if timestamp missing)
        Query query = FirebaseUtil.getCartItems();

        FirestoreRecyclerOptions<CartItemModel> options =
                new FirestoreRecyclerOptions.Builder<CartItemModel>()
                        .setQuery(query, CartItemModel.class)
                        .build();

        cartAdapter = new CartAdapter(options, getActivity());

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    // ✅ Receive total price
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalPrice = intent.getIntExtra("totalPrice", 0);
            cartPriceTextView.setText("₹ " + totalPrice);
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        if (cartAdapter != null) {
            cartAdapter.startListening();
        }

        LocalBroadcastManager.getInstance(requireActivity())
                .registerReceiver(mMessageReceiver, new IntentFilter("price"));
    }

    @Override
    public void onStop() {
        super.onStop();

        if (cartAdapter != null) {
            cartAdapter.stopListening();
        }

        LocalBroadcastManager.getInstance(requireActivity())
                .unregisterReceiver(mMessageReceiver);
    }
}
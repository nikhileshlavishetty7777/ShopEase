package com.example.newEcom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.newEcom.R;
import com.example.newEcom.fragments.CartFragment;
import com.example.newEcom.fragments.HomeFragment;
import com.example.newEcom.fragments.ProductFragment;
import com.example.newEcom.fragments.ProfileFragment;
import com.example.newEcom.fragments.SearchFragment;
import com.example.newEcom.fragments.WishlistFragment;
import com.example.newEcom.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment;
    CartFragment cartFragment;
    WishlistFragment wishlistFragment;
    ProfileFragment profileFragment;
    SearchFragment searchFragment;

    LinearLayout searchLinearLayout;
    MaterialSearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchLinearLayout = findViewById(R.id.linearLayout);
        searchBar = findViewById(R.id.searchBar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        wishlistFragment = new WishlistFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.home) {
                fragment = homeFragment;
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            else if (item.getItemId() == R.id.cart) {
                fragment = cartFragment;
            }
            else if (item.getItemId() == R.id.wishlist) {
                fragment = wishlistFragment;
            }
            else if (item.getItemId() == R.id.profile) {
                fragment = profileFragment;
            }

            if(fragment!=null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame_layout, fragment)
                        .commit();
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);

        addOrRemoveBadge();

        searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchConfirmed(CharSequence text) {

                Bundle bundle = new Bundle();
                bundle.putString("search", text.toString());

                searchFragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_frame_layout, searchFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        handleDeepLink();

        if (getIntent().getBooleanExtra("orderPlaced", false)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, profileFragment)
                    .commit();

            bottomNavigationView.setSelectedItemId(R.id.profile);
        }
    }

    public void showSearchBar(){
        searchLinearLayout.setVisibility(View.VISIBLE);
    }

    public void hideSearchBar(){
        searchLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    // ---------------- Cart Badge ----------------

    public void addOrRemoveBadge() {

        FirebaseUtil.getCartItems().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                            int n = task.getResult().size();

                            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.cart);

                            badge.setBackgroundColor(Color.parseColor("#F44336"));

                            if(n>0){
                                badge.setVisible(true);
                                badge.setNumber(n);
                            }
                            else{
                                badge.clearNumber();
                                badge.setVisible(false);
                            }
                        }
                    }
                });
    }

    // ---------------- Deep Link ----------------

    private void handleDeepLink(){

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {

                    Uri deepLink = null;

                    if(pendingDynamicLinkData!=null)
                        deepLink = pendingDynamicLinkData.getLink();

                    if(deepLink!=null){

                        String productId = deepLink.getQueryParameter("product_id");

                        if(productId!=null){

                            Fragment fragment = ProductFragment.newInstance(Integer.parseInt(productId));

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_frame_layout, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
    }
}
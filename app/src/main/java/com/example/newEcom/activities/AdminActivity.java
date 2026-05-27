package com.example.newEcom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newEcom.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    TextView userNameTextView;

    LinearLayout logoutBtn;

    CardView addProductBtn, modifyProductBtn;
    CardView addCategoryBtn, modifyCategoryBtn;
    CardView addBannerBtn, modifyBannerBtn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userNameTextView = findViewById(R.id.userNameTextView);
        logoutBtn = findViewById(R.id.logoutBtn);

        addProductBtn = findViewById(R.id.addProductBtn);
        modifyProductBtn = findViewById(R.id.modifyProductBtn);
        addCategoryBtn = findViewById(R.id.addCategoryBtn);
        modifyCategoryBtn = findViewById(R.id.modifyCategoryBtn);
        addBannerBtn = findViewById(R.id.addBannerBtn);
        modifyBannerBtn = findViewById(R.id.modifyBannerBtn);

        auth = FirebaseAuth.getInstance();

        userNameTextView.setText("Hello, Admin");

        // LOGOUT
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            finish();
        });

        // ADD PRODUCT
        addProductBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddProductActivity.class));
        });

        // MODIFY PRODUCT
        modifyProductBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ModifyProductActivity.class));
        });

        // ADD CATEGORY
        addCategoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddCategoryActivity.class));
        });

        // MODIFY CATEGORY
        modifyCategoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ModifyCategoryActivity.class));
        });

        // ADD BANNER
        addBannerBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, AddBannerActivity.class));
        });

        // MODIFY BANNER
        modifyBannerBtn.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ModifyBannerActivity.class));
        });
    }
}
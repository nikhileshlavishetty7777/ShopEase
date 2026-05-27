package com.example.newEcom.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.newEcom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimation;
    private static final long SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Lottie Animation
        lottieAnimation = findViewById(R.id.lottieAnimationView);
        lottieAnimation.playAnimation();

        // Delayed navigation after splash
        new Handler().postDelayed(() -> navigateUser(), SPLASH_DELAY);
    }

    private void navigateUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // User not logged in, go to LoginActivity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        } else {
            // User logged in
            String email = currentUser.getEmail();
            if (email != null && email.equals("harshlohiya.photos@gmail.com")) {
                // Admin user
                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
            } else {
                // Regular user
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }

        finish(); // Close SplashActivity
    }
}
package com.example.newEcom.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newEcom.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText emailEditText, passEditText;
    ImageView loginBtn;
    TextView signupPageBtn;
    Button googleLoginBtn;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        progressBar = findViewById(R.id.progress_bar);
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);
        loginBtn = findViewById(R.id.loginBtn);
        signupPageBtn = findViewById(R.id.signupPageBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);

        auth = FirebaseAuth.getInstance();

        // Google Sign In config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        loginBtn.setOnClickListener(v -> loginUser());

        signupPageBtn.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));

        googleLoginBtn.setOnClickListener(v -> {
            googleSignInClient.signOut();
            googleSignin();
        });

        getWindow().setExitTransition(new Explode());
    }

    private void loginUser() {

        String email = emailEditText.getText().toString().trim();
        String pass = passEditText.getText().toString().trim();

        if (!validate(email, pass)) return;

        loginAccountInFirebase(email, pass);
    }

    private void loginAccountInFirebase(String email, String pass) {

        changeInProgress(true);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {

                    changeInProgress(false);

                    if (task.isSuccessful()) {

                        if (auth.getCurrentUser().isEmailVerified()) {

                            navigateAfterLogin(auth.getCurrentUser().getEmail());

                        } else {

                            Toast.makeText(LoginActivity.this,
                                    "Email not verified. Please verify email.",
                                    Toast.LENGTH_LONG).show();

                            FirebaseAuth.getInstance().signOut();
                        }

                    } else {

                        Toast.makeText(LoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ADMIN / CUSTOMER NAVIGATION
    private void navigateAfterLogin(String email) {

        Toast.makeText(this, "Logged in: " + email, Toast.LENGTH_LONG).show();

        if(email.equals("nageshlavishetty@gmail.com")){
            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
        }
        else{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        finish();
    }

    private void changeInProgress(boolean inProgress) {

        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean validate(String email, String pass) {

        boolean valid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            valid = false;
        }

        if (pass.length() < 6) {
            passEditText.setError("Password must be at least 6 characters");
            valid = false;
        }

        return valid;
    }

    // GOOGLE LOGIN
    private void googleSignin() {

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && data != null) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {

                Toast.makeText(this, "Google Sign-In failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        navigateAfterLogin(auth.getCurrentUser().getEmail());

                    } else {

                        Toast.makeText(this,
                                "Authentication failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
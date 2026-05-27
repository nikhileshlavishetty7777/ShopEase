package com.example.newEcom.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newEcom.R;
import com.example.newEcom.model.CartItemModel;
import com.example.newEcom.model.OrderItemModel;
import com.example.newEcom.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CheckoutActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText, addressEditText;
    Button placeOrderBtn;

    TextView subtotalTextView, deliveryTextView, totalTextView;

    int subtotal = 0;
    int delivery = 500;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);

        subtotalTextView = findViewById(R.id.subtotalTextView);
        deliveryTextView = findViewById(R.id.deliveryTextView);
        totalTextView = findViewById(R.id.totalTextView);

        // ✅ Get subtotal from CartFragment
        subtotal = getIntent().getIntExtra("price", 0);

        // ✅ Calculate total
        total = subtotal + delivery;

        // ✅ SET UI
        subtotalTextView.setText("₹ " + subtotal);
        deliveryTextView.setText("₹ " + delivery);
        totalTextView.setText("₹ " + total);

        placeOrderBtn.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Enter your name");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter valid email");
            return;
        }

        if (phone.isEmpty() || !phone.matches("[6-9][0-9]{9}")) {
            phoneEditText.setError("Enter valid phone");
            return;
        }

        if (address.isEmpty()) {
            addressEditText.setError("Enter address");
            return;
        }

        int orderId = (int) System.currentTimeMillis();

        FirebaseUtil.getCartItems().get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot doc : task.getResult()) {

                    CartItemModel cart = doc.toObject(CartItemModel.class);

                    OrderItemModel orderItem = new OrderItemModel(
                            orderId,
                            cart.getProductId(),
                            cart.getName(),
                            cart.getImage(),
                            cart.getPrice(),
                            cart.getQuantity(),
                            Timestamp.now(),
                            name,
                            email,
                            phone,
                            address,
                            ""
                    );

                    FirebaseUtil.getOrderItems().add(orderItem);
                }

                clearCart();
                Toast.makeText(
                        CheckoutActivity.this,
                        "Order Placed Successfully",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart() {
        FirebaseUtil.getCartItems().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    FirebaseUtil.getCartItems()
                            .document(doc.getId())
                            .delete();
                }
            }
        });
    }
}
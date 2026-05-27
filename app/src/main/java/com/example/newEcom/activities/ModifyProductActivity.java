package com.example.newEcom.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newEcom.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ModifyProductActivity extends AppCompatActivity {

    AutoCompleteTextView idDropDown;

    EditText nameEditText,descriptionEditText,specificationEditText,
            stockEditText,priceEditText,discountEditText;

    LinearLayout detailsLinearLayout;

    FirebaseFirestore db;

    ArrayList<String> productIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);

        idDropDown = findViewById(R.id.idDropDown);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        specificationEditText = findViewById(R.id.specificationEditText);
        stockEditText = findViewById(R.id.stockEditText);
        priceEditText = findViewById(R.id.priceEditText);
        discountEditText = findViewById(R.id.discountEditText);

        detailsLinearLayout = findViewById(R.id.detailsLinearLayout);

        db = FirebaseFirestore.getInstance();

        loadProductIds();
    }

    private void loadProductIds(){

        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    productIds.clear();

                    for(DocumentSnapshot doc : queryDocumentSnapshots){

                        if(doc.get("productId") != null){
                            productIds.add(doc.get("productId").toString());
                        }

                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    productIds);

                    idDropDown.setAdapter(adapter);
                });

    }
    private void loadProductDetails(String id){

        db.collection("products")
                .whereEqualTo("productId", Integer.parseInt(id))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot doc : queryDocumentSnapshots){

                        detailsLinearLayout.setVisibility(View.VISIBLE);

                        nameEditText.setText(doc.getString("name"));
                        descriptionEditText.setText(doc.getString("description"));
                        specificationEditText.setText(doc.getString("specification"));
                        stockEditText.setText(doc.get("stock").toString());
                        priceEditText.setText(doc.get("price").toString());
                        discountEditText.setText(doc.get("discount").toString());

                    }

                });

    }
}
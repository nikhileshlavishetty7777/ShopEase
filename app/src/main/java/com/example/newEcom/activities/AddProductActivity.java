package com.example.newEcom.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newEcom.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    EditText nameEditText,descriptionEditText,specificationEditText,
            stockEditText,priceEditText,discountEditText;

    AutoCompleteTextView categoryDropDown;

    Button addProductBtn,imageBtn;

    ImageView productImageView;

    FirebaseFirestore db;

    Uri imageUri;

    String[] categories = {"Shoes","Mobiles","Clothes","Electronics"};

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        specificationEditText = findViewById(R.id.specificationEditText);
        stockEditText = findViewById(R.id.stockEditText);
        priceEditText = findViewById(R.id.priceEditText);
        discountEditText = findViewById(R.id.discountEditText);

        categoryDropDown = findViewById(R.id.categoryDropDown);

        addProductBtn = findViewById(R.id.addProductBtn);
        imageBtn = findViewById(R.id.imageBtn);
        productImageView = findViewById(R.id.productImageView);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        categories);

        categoryDropDown.setAdapter(adapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if(result.getResultCode() == RESULT_OK){

                        Intent data = result.getData();

                        if(data != null){
                            imageUri = data.getData();
                            productImageView.setImageURI(imageUri);
                        }
                    }
                });

        imageBtn.setOnClickListener(v -> openGallery());

        addProductBtn.setOnClickListener(v -> addProduct());
    }

    private void openGallery(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        imagePickerLauncher.launch(intent);
    }

    private void addProduct(){

        String name = nameEditText.getText().toString();
        String category = categoryDropDown.getText().toString();
        String description = descriptionEditText.getText().toString();
        String specification = specificationEditText.getText().toString();

        int stock = Integer.parseInt(stockEditText.getText().toString());
        int price = Integer.parseInt(priceEditText.getText().toString());
        int discount = Integer.parseInt(discountEditText.getText().toString());

        Map<String,Object> product = new HashMap<>();

        product.put("name",name);
        product.put("category",category);
        product.put("description",description);
        product.put("specification",specification);
        product.put("stock",stock);
        product.put("price",price);
        product.put("discount",discount);

        // Important fields
        product.put("rating",0);
        product.put("noOfRating",0);

        // temporary image (later firebase storage use karna)
        product.put("image","https://i.imgur.com/1bX5QH6.png");

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this,"Product Added",Toast.LENGTH_SHORT).show());
    }
}
package com.example.retten.retten.Activities;
//package com.example.retten.retten.model;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retten.retten.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProductForm extends AppCompatActivity {

    TextView productid, title, type, description, price, quantity;

    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_form);

        myref = FirebaseDatabase.getInstance().getReference("Supermarkt/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        productid = (TextView) findViewById(R.id.addProductId);
        title = (TextView) findViewById(R.id.addProductTitle);
        type = (TextView) findViewById(R.id.addProductType);
        description = (TextView) findViewById(R.id.addProductDescription);
        price = (TextView) findViewById(R.id.addProductPrice);
        quantity = (TextView) findViewById(R.id.addProductQuantity);

        findViewById(R.id.addProductSubmit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (productid.getText().toString().matches("") ||
                        title.getText().toString().matches("") ||
                        type.getText().toString().matches("") ||
                        description.getText().toString().matches("") ||
                        price.getText().toString().matches("") ||
                        quantity.getText().toString().matches("")) {

                    Toast.makeText(getApplicationContext(), "Bitte alles ausfüllen", Toast.LENGTH_SHORT).show();

                } else {

                    myref.addListenerForSingleValueEvent(new ValueEventListener() {
                        ArrayList<ShoppingItem> productList = new ArrayList<>();
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                      //      Boolean islistempty  = Boolean.valueOf(dataSnapshot.child("isProdsEmpty").getValue().toString());
                         //   if (!islistempty) {
                                myref.child("isProdsEmpty").setValue(Boolean.FALSE.toString());
                         //   } else {
                                for (DataSnapshot snap : dataSnapshot.child("products").getChildren()) {
                                    int itemPrice = -1;
                                    try {
                                        itemPrice = Integer.valueOf(NumberFormat.getCurrencyInstance()
                                                .parse(String.valueOf(snap.child("price").getValue()))
                                                .toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String productID = snap.child("productID").getValue().toString();

                                    productList.add(new ShoppingItem(
                                            productID,
                                            snap.child("title").getValue().toString(),
                                            snap.child("type").getValue().toString(),
                                            snap.child("description").getValue().toString(),
                                            itemPrice,
                                            Integer.valueOf(snap.child("quantity").getValue().toString())
                                    ));
                                }
                         //   }
//
                            productList.add(new ShoppingItem(
                                    productid.getText().toString(),
                                    title.getText().toString(),
                                    type.getText().toString(),
                                    description.getText().toString(),
                                    Integer.valueOf(price.getText().toString()),
                                    Integer.valueOf(quantity.getText().toString()))
                            );

                            Map<String, Object> cartItemsMap = new HashMap<>();
                            cartItemsMap.put("products", productList);
                            myref.updateChildren(cartItemsMap);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("", "Wert könnte nicht gelesen werden", databaseError.toException());
                        }
                    });

                }
            }
        });
    }
}



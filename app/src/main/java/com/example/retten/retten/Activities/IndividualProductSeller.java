package com.example.retten.retten.Activities;

import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retten.retten.R;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndividualProductSeller extends AppCompatActivity {

    private final String TAG = IndividualProduct.class.getSimpleName();

    private int quantity=-1;
    String ip;
    ShoppingItem item;
    TextView name, description, quantityView;
    ImageView productImage;
    ProgressBar progressBar;
    Button removeProduct;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase;
    private DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product_seller);

        progressBar = (ProgressBar) findViewById(R.id.individualProductPageProgressBarSeller);
        progressBar.setVisibility(View.VISIBLE);

        item = (ShoppingItem) getIntent().getSerializableExtra("Products");
        quantity = item.getQuantity();

        removeProduct = (Button) findViewById(R.id.deleteProductSeller);

        mDatabase = database.getReference("Supermarkt/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    ArrayList<ShoppingItem> productList = new ArrayList<>();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // lieber neue Klass erstellen
                        for (DataSnapshot snap : dataSnapshot.child("Products").getChildren()) {
                            int itemPrice = -1;
                            try {
                                itemPrice = Integer.valueOf(NumberFormat.getCurrencyInstance()
                                        .parse(String.valueOf(snap.child("Price").getValue()))
                                        .toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String productID = snap.child("productid").getValue().toString();

                            productList.add(new ShoppingItem(
                                    productID,
                                    snap.child("title").getValue().toString(),
                                    snap.child("type").getValue().toString(),
                                    snap.child("description").getValue().toString(),
                                    itemPrice,
                                    Integer.valueOf(snap.child("quantity").getValue().toString())
                            ));
                        }
                        for (ShoppingItem x : productList){
                            if(x.getProductID().equals(item.getProductID())){
                                productList.remove(x);
                                break;
                            }
                        }
                        if(productList.size() == 0){
                            mDatabase.child("isProdsEmpty").setValue(Boolean.TRUE.toString());
                            productList.add(new ShoppingItem("", "", "", "", -1, -1));
                        }

                        Map<String, Object> supermarkt_products = new HashMap<>();
                        supermarkt_products.put("Products", productList);
                        mDatabase.updateChildren(supermarkt_products);
                        Toast.makeText(getApplicationContext(), "gelöscht!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSeller);
        toolbar.setTitle("");
//        setSupportActionBar(toolbar);

        ip = getResources().getString(R.string.ip);

        name = (TextView) findViewById(R.id.productNameIndividualProductSeller);
        name.setText(item.getTitle());

        description = (TextView) findViewById(R.id.productDescriptionIndividualProductSeller);
        description.setText(item.getDescription());

        quantityView = (TextView) findViewById(R.id.quantityIndividualProductSeller);
        quantityView.setText(String.valueOf(quantity));

        ((TextView) findViewById(R.id.productPriceIndividualProductSeller))
                .setText(item.getPrice());

        productImage = (ImageView) findViewById(R.id.productImageIndividualProductSeller);
        Picasso.with(getApplicationContext())
                .load(ip + String.valueOf(item.getProductID()) + ".jpg")
                .fit()
                .into(productImage);

        progressBar.setVisibility(View.GONE);
    }
}
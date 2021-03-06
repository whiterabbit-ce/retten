package com.example.retten.retten.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.retten.retten.R;
import com.example.retten.retten.database.DataHolder;
import com.example.retten.retten.model.Supermarkt;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class search extends AppCompatActivity {


    private DatabaseReference mDatabase;


   public final String TAG = search.class.getSimpleName();
    ListView shoppingItemView;
    ShoppingListAdapter adapter;
    EditText toolbar;
    ProgressBar progressBar;
    EditText searchbar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();




    private Boolean exit = false;
    private ArrayList<ShoppingItem> shoppingItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Rett€n");
            setSupportActionBar(toolbar);

            searchbar = (EditText)findViewById(R.id.searchBar);

            FloatingActionButton shoppingCart = (FloatingActionButton) findViewById(R.id.cartMainPage);
            shoppingCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ShoppingCartWindow.class));
                }
            });

            progressBar = (ProgressBar) findViewById(R.id.mainPageProgressBar);
            shoppingItemView = (ListView) findViewById(R.id.shoppingList);

            DatabaseReference myRef = database.getReference("items");
            myRef.addValueEventListener(new ValueEventListener() {
                //  listener von items
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //dieser Methode lädt die Information jedes mal die es sich ändert

                    // Query Daten aus dem Server
                    shoppingItems = getAllItems(dataSnapshot);
                    adapter = new ShoppingListAdapter(getApplicationContext(), shoppingItems);
                    progressBar.setVisibility(View.GONE);
                    shoppingItemView.setAdapter(adapter);

                    shoppingItemView.setTextFilterEnabled(true);
                    searchbar.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            int textlength = charSequence.length();
                            ArrayList<ShoppingItem> tempShoppingItems = new ArrayList<>();
                            for(ShoppingItem x: shoppingItems){
                                if (textlength <= x.getTitle().length()) {
                                    if (x.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                        tempShoppingItems.add(x);
                                    }
                                }
                            }
                            shoppingItemView.setAdapter(new ShoppingListAdapter(getApplicationContext(), tempShoppingItems));
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            shoppingItemView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent productIntent = new Intent(search.this, IndividualProduct.class);
                    productIntent.putExtra("product", shoppingItems.get(i));
                    startActivity(productIntent);
                }
            });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.menu_main_app_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutItem) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // For exiting the application
    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Snackbar.make(findViewById(R.id.main_content), "um zu schliessen, noch einmal die Zurücktaste druecken", Snackbar.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    public static ArrayList<ShoppingItem> getAllItems(DataSnapshot dataSnapshot){

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();

        for (DataSnapshot item : dataSnapshot.getChildren()) {

            items.add(new ShoppingItem(
                    item.child("productid").getValue().toString(),
                    item.child("name").getValue().toString(),
                    item.child("type").getValue().toString(),
                    item.child("description").getValue().toString(),
                    Integer.valueOf(item.child("price").getValue().toString()),
                    Integer.valueOf(item.child("quantity").getValue().toString())
            ));

        }

        return items;
    }

    public static ArrayList<ShoppingItem> setUpList(DataSnapshot dataSnapshot) {

        ArrayList<ShoppingItem> items  = new ArrayList<ShoppingItem>();

        for (DataSnapshot snap : dataSnapshot.getChildren()){

            int itemPrice = -1, quantity = 0;

            try{
                itemPrice = Integer.valueOf(NumberFormat.getCurrencyInstance()
                        .parse(String.valueOf(snap.child("price").getValue()))
                        .toString());
            } catch (ParseException e){
                e.printStackTrace();
            }

            quantity = Integer.valueOf(snap.child("quantity").getValue().toString());
            items.add(new ShoppingItem(
                    snap.child("productID").getValue().toString(),
                    snap.child("title").getValue().toString(),
                    snap.child("type").getValue().toString(),
                    snap.child("description").getValue().toString(),
                    itemPrice,
                    quantity
            ));
        }

        return items;

    }



}

package com.example.retten.retten.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retten.retten.R;
import com.example.retten.retten.database.DataHolder;
import com.example.retten.retten.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.time.Instant;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    ProgressBar progressBar;
    private Button loginButton;
    private ImageView loginPhoto;
    private EditText user, pass;
    private TextView newUser, resetPassword, newSeller;
    private String username, password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginBtn);
        user = (EditText) findViewById(R.id.userMail);
        pass = (EditText) findViewById(R.id.userPassword);
        setInputs(true);
        newUser = (TextView) findViewById(R.id.newuserbutton);
        newSeller = (TextView) findViewById(R.id.newsuperreg);
        resetPassword = (TextView) findViewById(R.id.forgotPassword);
        progressBar = (ProgressBar) findViewById(R.id.loginProgress);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    FirebaseDatabase.getInstance().getReference("Supermarkt/" + user.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    if (dataSnapshot.exists()) {
                                        intent.putExtra("isSupermarkt", true);
                                    } else {
                                        intent.putExtra("isSupermarkt", false);
                                    }
                                    Toast.makeText(getApplicationContext(), "Eingelogt!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Failed to read value
                                    Log.w(TAG, "Wert könnte nicht gelesen werden.", databaseError.toException());
                                }
                            });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        progressBar.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                username = user.getText().toString();
                password = pass.getText().toString();
                setInputs(false);
                signIn(username, password);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, forgotPassword.class));
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newUserReg = new Intent(LoginActivity.this, RegisterActivity.class);
                newUserReg.putExtra("isSupermarkt", false);
                startActivity(newUserReg);
                finish();
            }
        });

        newSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newSellerReg = new Intent(LoginActivity.this, RegisterActivity.class);
                newSellerReg.putExtra("isSupermarkt", true);
                startActivity(newSellerReg);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signIn(String user, String password) {
        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                            setInputs(true);
                        }

                        // ...
                    }
                });
    }

    private void setInputs(boolean val) {
        user.setEnabled(val);
        pass.setEnabled(val);
    }



/*
    private EditText userMail,userPassword;
    private Button btnLogin, newuserbutton;
    private ProgressBar loginProgress;
    private TextView  resetPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Intent HomeActivity;
    private ImageView loginPhoto;
    private int progressStatus = 0;
    private Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.userMail);
        userPassword = findViewById(R.id.userPassword);
        btnLogin = findViewById(R.id.loginBtn);
        newuserbutton = findViewById(R.id.newuserbutton);
        loginProgress = findViewById(R.id.loginProgress);
        resetPassword = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();





        HomeActivity = new Intent(this,com.example.retten.retten.Activities.HomeActivity.class);
        loginPhoto = findViewById(R.id.login_photo);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish(); }
        });

        newuserbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish(); }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, forgotPassword.class));
            }
        });

        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Bitte jeden Feld ausfüllen");
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }
                else
                {
                    signIn(mail,password);

                }




            }
        });



    }



    private void signIn(final String mail, final String password) {


        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {

                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    User m_user =new User();
                    m_user.set_email(mail);
                    m_user.set_UID(mAuth.getUid());
                    user = mAuth.getCurrentUser();
                    m_user.set_firebaseUser(user);
                    DataHolder.getInstance().getUserData(mail,password);
                    updateUI();

                }
                else {
                    showMessage(task.getException().getMessage());
                    btnLogin.setVisibility(View.VISIBLE);
                    loginProgress.setVisibility(View.INVISIBLE);
                }


            }
        });




    }

   //  Thread thread=new Thread();

  /*  private void updateUI() {

        if(mAuth.getCurrentUser()!=null)
        { loginProgress = (ProgressBar) findViewById(R.id.loginProgress);
            thread = new Thread(new Runnable() {
                public void run() {
                    while (progressStatus < 100) {
                        progressStatus += 40;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {
                                DataHolder.getInstance().getUserData(mAuth.getCurrentUser().getEmail(),userPassword.getText().toString());
                                loginProgress.setProgress(progressStatus);
                                if (progressStatus > 100) {
                                    loginProgress.setVisibility(View.GONE);

                                   /* Intent inent = new Intent(getApplicationContext(),HomeActivity.class);
                                    startActivity(inent);

                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference uidRef = rootRef.child("user").child(uid);
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {




                                            if(dataSnapshot.child("isAdmin") != null || dataSnapshot.child("isSupermarkt") != null) {

                                                if(dataSnapshot.child("isSupermarkt") != null) {

                                                  //  startActivity(new Intent(getApplicationContext(), SupermarktActivity.class));
                                                    Intent inent = new Intent(getApplicationContext(),SupermarktActivity.class);
                                                    startActivity(inent);

                                                }

                                                else if(dataSnapshot.child("isAdmin") != null) {

                                                //    startActivity(new Intent(getApplicationContext(), AdminPage.class));
                                                    Intent inent = new Intent(getApplicationContext(),AdminPage.class);
                                                    startActivity(inent);


                                                }
                                                else if(dataSnapshot.child("isAdmin") == null && dataSnapshot.child("isSupermarkt") == null) {
                                                   // startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                                    Intent inent = new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(inent);
                                                }

                                            }


                                        }

                                        private static final String TAG = "LoginActivity";
                                        @Override

                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            Log.d(TAG, databaseError.getMessage());
                                        }
                                    };
                                    uidRef.addListenerForSingleValueEvent(valueEventListener);









                                    thread.interrupt();
                                }

                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }


    }*/

   /* private void showMessage(String text) {

        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }*/




  /*  @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();

        if(mAuth.getCurrentUser()!=null)
        {


            loginProgress = (ProgressBar) findViewById(R.id.loginProgress);
            thread = new Thread(new Runnable() {
                public void run() {
                    while (progressStatus < 100) {
                        progressStatus += 30;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {
                                DataHolder.getInstance().getUserData(mAuth.getCurrentUser().getEmail(),userPassword.getText().toString());
                                loginProgress.setProgress(progressStatus);
                                if (progressStatus > 100) {
                                    loginProgress.setVisibility(View.GONE);


                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference uidRef = rootRef.child("user").child(uid);
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {




                                            if(dataSnapshot.child("isAdmin") != null || dataSnapshot.child("isSupermarkt") != null) {

                                                if(dataSnapshot.child("isSupermarkt") != null) {

                                                  //  startActivity(new Intent(getApplicationContext(), SupermarktActivity.class));
                                                    Intent inent = new Intent(getApplicationContext(),SupermarktActivity.class);
                                                    startActivity(inent);

                                                }

                                                else if(dataSnapshot.child("isAdmin") != null) {

                                               //     startActivity(new Intent(getApplicationContext(), AdminPage.class));
                                                    Intent inent = new Intent(getApplicationContext(),AdminPage.class);
                                                    startActivity(inent);


                                                }
                                                else if(dataSnapshot.child("isAdmin") == null && dataSnapshot.child("isSupermarkt") == null) {
                                                 //   startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                                                    Intent inent = new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(inent);
                                                }

                                            }


                                        }

                                        private static final String TAG = "LoginActivity";
                                    @Override

                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        Log.d(TAG, databaseError.getMessage());
                                    }
                                    };
                                    uidRef.addListenerForSingleValueEvent(valueEventListener);

                                    Intent inent = new Intent(getApplicationContext(),HomeActivity.class);
                                    startActivity(inent);
                                    thread.interrupt();



                                }

                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
}//comment

    private void updateUI() {

        startActivity(HomeActivity);
        finish();

    }




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {

            //user is already connected  so we need to redirect him to home page
            updateUI();

        }



    }*/





}
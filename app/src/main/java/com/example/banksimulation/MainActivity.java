package com.example.banksimulation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    private TextView accountHolderName, textViewBalance;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private FirebaseUser currentuser;
    private String txt_name;
    private long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateToolbar();

        getUserProfile();

        textViewBalance = findViewById(R.id.txt_balance);

    }

    public void updateToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void getUserProfile() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser();
        documentReference = db.collection("users").document(currentuser.getEmail());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userProfileReturned userProfileReturned = documentSnapshot.toObject(userProfileReturned.class);
                txt_name = userProfileReturned.getName();
                balance = userProfileReturned.getBalance();
                accountHolderName = findViewById(R.id.accountHolderName);
                accountHolderName.setText("Welcome " + txt_name);
                textViewBalance.setText("Balance : " + balance);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.change_password:
                //FirebaseAuth.getInstance().sendPasswordResetEmail();
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class
                ));
                return true;
            // manage other entries if you have it ...
        }

        return true;
    }

    public void deposit(View view) {
        final EditText editText = findViewById(R.id.deposit);
        if (!editText.getText().toString().isEmpty()) {
            final long val = Integer.valueOf(editText.getText().toString());
            documentReference.update("balance", val + balance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        balance = balance + val;
                        Map<String,Object> map = new HashMap<>();
                        map.put("deposit",val);
                        map.put("closing_balance",balance);
                        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                        db.collection("users").document(currentuser.getEmail()).collection("history").document(String.valueOf(timeStamp)).set(map);
                        textViewBalance.setText("Balance : " + String.valueOf(balance));
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            });
        }
    }

    public void withdraw(View view) {
        final EditText editText = findViewById(R.id.withdraw);
        if (!editText.getText().toString().isEmpty()) {
            final long val = Integer.valueOf(editText.getText().toString());
            documentReference.update("balance", balance - val).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (val <= balance) {
                            balance = balance - val;
                            textViewBalance.setText("Balance : " + String.valueOf(balance));
                            Map<String,Object> map = new HashMap<>();
                            map.put("withdraw",val);
                            map.put("closing_balance",balance);
                            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                            db.collection("users").document(currentuser.getEmail()).collection("history").document(String.valueOf(timeStamp)).set(map);
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                            finish();

                            Toast toast = new Toast(MainActivity.this);
                            ImageView imageView = new ImageView(MainActivity.this);
                            if (val < 100) {
                                imageView.setImageResource(R.drawable.rs10note);
                            } else if (val >= 100 && val < 500) {
                                imageView.setImageResource(R.drawable.rs100note);
                            } else if (val >= 500 && val < 2000) {
                                imageView.setImageResource(R.drawable.rs500note);
                            } else if (val >= 2000) {
                                imageView.setImageResource(R.drawable.rs2000note);
                            }
                            toast.setView(imageView);
                            toast.show();

                        } else {
                            Toast.makeText(MainActivity.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }
    }

    public void history(View view) {
        startActivity(new Intent(MainActivity.this, History.class));
    }


}

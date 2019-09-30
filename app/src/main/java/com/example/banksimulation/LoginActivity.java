package com.example.banksimulation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    EditText email, password;
    String txt_email, txt_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    public void login(View view) {
        txt_email = email.getText().toString();
        txt_password = password.getText().toString();

        if (!txt_email.isEmpty() && !txt_password.isEmpty()) {
            auth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user.isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void resetPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.reset_password,null);
        builder.setView(dialogView);
        builder.setTitle("Enter your email");

        final EditText email = dialogView.findViewById(R.id.resetPassword);
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String str_email = email.getText().toString();
                if (!str_email.isEmpty()) {
                    auth.sendPasswordResetEmail(str_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Email sent for password reset.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Your email is not registered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog b = builder.create();
        b.show();
    }

    public void newUser(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Open your account");

        final EditText name = dialogView.findViewById(R.id.name);
        final EditText email = dialogView.findViewById(R.id.email);
        final EditText password = dialogView.findViewById(R.id.password);
        final EditText confirmPassword = dialogView.findViewById(R.id.confirm_password);
        final EditText phone_no = dialogView.findViewById(R.id.phone_no);
        //dialogBuilder.setMessage("");
        dialogBuilder.setPositiveButton("Sign up", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String str_name = name.getText().toString();
                final String str_email = email.getText().toString();
                final String str_phone_no = phone_no.getText().toString();
                final String str_password = password.getText().toString();
                final String str_confirm_password = confirmPassword.getText().toString();

                if (!str_name.isEmpty() &&!str_phone_no.isEmpty() && !str_email.isEmpty() && !str_password.isEmpty() && ! str_confirm_password.isEmpty())  {
                    if (str_password.equals(str_confirm_password)) {
                        auth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    db = FirebaseFirestore.getInstance();
                                    db = FirebaseFirestore.getInstance();
                                    Map<String,Object> register_user = new HashMap<>();
                                    register_user.put("name",str_name);
                                    register_user.put("phone_no",str_phone_no);
                                    register_user.put("email",str_email);
                                    register_user.put("balance",1000);
                                    String acc = "2019" + str_phone_no;
                                    int account_no = Integer.valueOf(acc);
                                    register_user.put("account_no", account_no);
                                    db.collection("users").document(str_email).set(register_user);

                                    FirebaseUser user = auth.getCurrentUser();
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Email sent, please verify your email address.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                } else {
                                    Toast.makeText(LoginActivity.this, "Autnentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "All fields are necessary!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}

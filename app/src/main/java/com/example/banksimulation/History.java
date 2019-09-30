package com.example.banksimulation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView accountHolderName;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private FirebaseUser currentuser;
    private String txt_name;
    private ListView listView;

    ArrayList<Integer> image = new ArrayList<>();
    ArrayList<String> amount = new ArrayList<>();
    ArrayList<String> closing_balance = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<Boolean> type = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        updateToolbar();

        getUserProfile();
        displayHistory();

        listView = findViewById(R.id.listView);
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
                accountHolderName = findViewById(R.id.accountHolderName);
                accountHolderName.setText("Welcome " + txt_name);
            }
        });
    }

    public void displayHistory() {
        db.collection("users").document(currentuser.getEmail()).collection("history").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<QueryDocumentSnapshot> arr = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arr.add(document);
                            }

                            for (int i = arr.size()-1;i>=0;--i) {
                                Object obj =  arr.get(i).get("deposit");
                                if (obj != null) {
                                    type.add(true);
                                    String temp = "" + arr.get(i).get("closing_balance");
                                    closing_balance.add(temp);
                                    image.add(R.drawable.deposit);
                                    temp = "" + arr.get(i).get("deposit");
                                    amount.add("+"+temp);
                                    temp = "" + arr.get(i).getId();
                                    date.add(temp.substring(0,10));
                                    temp = temp.substring(11,22);
                                    int n = Integer.valueOf(temp.substring(0,2));
                                    if (n>=13) {
                                        n-=12;
                                        time.add(n+temp.substring(2,8)+" pm");
                                    } else {
                                        time.add(n+temp.substring(2,8)+" am");
                                    }

                                } else {
                                    type.add(false);
                                    obj = arr.get(i).get("withdraw");
                                    Log.d("O/P","Time" + arr.get(i).getId() + "Withdraw" +obj);
                                    String temp = "" + arr.get(i).get("closing_balance");
                                    closing_balance.add(temp);
                                    image.add(R.drawable.withdraw);
                                    temp = "" + arr.get(i).get("withdraw");
                                    amount.add("-"+temp);
                                    temp = "" + arr.get(i).getId();
                                    date.add(temp.substring(0,10));
                                    temp = temp.substring(11,22);
                                    int n = Integer.valueOf(temp.substring(0,2));
                                    if (n>=13) {
                                        n-=12;
                                        time.add(n+temp.substring(2,8)+" pm");
                                    } else {
                                        time.add(n+temp.substring(2,8)+" am");
                                    }
                                }
                            }
                            //Converting arraylists to arrays

                            Integer[] image1 = image.toArray(new Integer[image.size()]);
                            String[] amount1 = amount.toArray(new String[amount.size()]);
                            String[] closing_balance1 = closing_balance.toArray(new String[closing_balance.size()]);
                            String[] date1 = date.toArray(new String[date.size()]);
                            String[] time1 = time.toArray(new String[time.size()]);
                            Boolean[] type1 = type.toArray(new Boolean[type.size()]);

                            MyListAdapter adapter=new MyListAdapter(History.this, type1, image1, amount1,closing_balance1, date1, time1);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d("Error", "Error getting history: ", task.getException());
                        }
                    }
                });
    }

}

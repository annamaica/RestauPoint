package com.example.maica.mapssample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.maica.mapssample.R.drawable.addreview;

public class View_reviewedrestau extends AppCompatActivity {

    List<AddReview> addreview;
    rateUserAdapter2 rateUser2;
    ProgressDialog progressDialog;
    ListView listView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviewedrestau);

        auth = FirebaseAuth.getInstance();

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("rateuserown").child(auth.getCurrentUser().getUid());

        addreview = new ArrayList<>();

        listView = (ListView) findViewById(R.id.reviewView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading rating information..... ");
        progressDialog.show();


        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addreview.clear();
                progressDialog.dismiss();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    AddReview rateupload = snapshot.getValue(AddReview.class);
                    addreview.add(rateupload);
                }
                rateUser2 = new rateUserAdapter2(View_reviewedrestau.this, R.layout.rateuser_layout, addreview);
                listView.setAdapter(rateUser2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TextView txt  = (TextView)view.findViewById(R.id.textView5);
                final TextView txt2  = (TextView)view.findViewById(R.id.textView9);

                AlertDialog.Builder builder = new AlertDialog.Builder(View_reviewedrestau.this);

                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        String restauname = txt.getText().toString();
                        String rateIDD = txt2.getText().toString();

                        DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference("rateuserown");
                        Query queryRef = dbref2.child(auth.getCurrentUser().getUid()).orderByChild("rateID").equalTo(rateIDD);

                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    snapshot.getRef().removeValue();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference dbref3 = FirebaseDatabase.getInstance().getReference("rateuser");
                        Query queryRef2 = dbref3.child(restauname).orderByChild("rateID").equalTo(rateIDD);

                        queryRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    //snapshot.getRef().removeValue();
                                    snapshot.getRef().removeValue();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return false;
            }
        });


    }
}

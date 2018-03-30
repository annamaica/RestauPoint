package com.example.maica.mapssample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.maica.mapssample.View_List.restauname;
import static com.example.maica.mapssample.View_List.restauid;

public class Add_review extends AppCompatActivity {
    RatingBar ratingbar;
    EditText editText;
    Button btn_addreview;
    FirebaseAuth auth;
    DatabaseReference mDatabaseReference, mDatabaseReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ratingbar = (RatingBar)findViewById(R.id.add_rating) ;
        editText = (EditText)findViewById(R.id.add_review) ;
        btn_addreview = (Button) findViewById(R.id.btn_addreview);
        final String restuaname = restauname;
        auth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("rateuser").child(restuaname);
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("rateuserown").child(auth.getCurrentUser().getUid());

        btn_addreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rate = String.valueOf(ratingbar.getRating());
                String comment = editText.getText().toString();

                String rateID = mDatabaseReference.push().getKey();
                AddReview addreview = new AddReview(rateID, auth.getCurrentUser().getUid(), rate, comment);
                AddReview addreview2 = new AddReview(rateID, restuaname, rate, comment);
                mDatabaseReference.child(rateID).setValue(addreview);
                mDatabaseReference2.child(rateID).setValue(addreview2);

                startActivity(new Intent(getApplicationContext(), ViewRestaurant.class));
                Toast.makeText(getApplicationContext(), "Successfully Rated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}

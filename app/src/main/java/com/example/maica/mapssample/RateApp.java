package com.example.maica.mapssample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RateApp extends AppCompatActivity {

    RatingBar userrating;
    Button btn_addrating;
    FirebaseAuth auth;
    DatabaseReference RatingDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_app);
        setTitle("Rate Us");

        userrating = (RatingBar)findViewById(R.id.apprating) ;
        btn_addrating = (Button) findViewById(R.id.submitrate);
        auth = FirebaseAuth.getInstance();

        RatingDatabaseReference = FirebaseDatabase.getInstance().getReference("userrate");

        btn_addrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String rate = String.valueOf(userrating.getRating());

                FirebaseUser user = auth.getCurrentUser();

                AppRating apprate = new AppRating(auth.getCurrentUser().getUid(), rate);
                RatingDatabaseReference.child(user.getUid()).setValue(apprate);

                finish();
                Toast.makeText(getApplicationContext(), "Thank you for rating our app!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), TabActivity.class));
            }
        });
    }
}

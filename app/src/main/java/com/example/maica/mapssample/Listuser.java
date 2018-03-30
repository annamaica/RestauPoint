package com.example.maica.mapssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Listuser extends AppCompatActivity {

    ListView listView;
    private List<ProfileInformation> listuser;
    ProfileAdapter profileAdapter;
    DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listuser);

        listuser = new ArrayList<>();
        final ListView listView3 = (ListView)findViewById(R.id.userlist);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listuser.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ProfileInformation rateupload = snapshot.getValue(ProfileInformation.class);
                    listuser.add(rateupload);
                }
                profileAdapter = new ProfileAdapter(Listuser.this, R.layout.userlist, listuser);
                listView3.setAdapter(profileAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

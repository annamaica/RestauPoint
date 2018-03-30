package com.example.maica.mapssample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Maica on 8/13/2017.
 */

public class ProfileActivity extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail, textViewUserName, textViewFirstName;
    private ImageView userimage;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button buttonEditProfile, buttonLogout, buttonViewReview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile,container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            getActivity().finish();
            startActivity(new Intent(getActivity(), Log_in.class));
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");

        FirebaseUser user = firebaseAuth.getCurrentUser();

        userimage = (ImageView) view.findViewById(R.id.viewImageUser);


        textViewUserEmail = (TextView) view.findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(user.getEmail());
        textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewFirstName = (TextView) view.findViewById(R.id.textViewFirstName);
        buttonEditProfile = (Button) view.findViewById(R.id.buttonEditProfile);
        buttonLogout = (Button) view. findViewById(R.id.buttonLogout);
        buttonViewReview  = (Button) view. findViewById(R.id.buttonViewReview);


        buttonLogout.setOnClickListener(this);
        buttonEditProfile.setOnClickListener(this);
        buttonViewReview.setOnClickListener(this);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        String id = firebaseAuth.getCurrentUser().getUid();
        Query search = databaseReference.child(id);
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfileInformation info = dataSnapshot.getValue(ProfileInformation.class);
                String userName = info.getUsername();
                String lastname = info.getLastname();
                String firstname = info.getFirstname();
                textViewUserName.setText(userName);
                textViewFirstName.setText(firstname + " " + lastname);
                Glide.with(ProfileActivity.this).load(info.getUrl()).into(userimage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            firebaseAuth.signOut();
            getActivity().finish();
            startActivity(new Intent(getActivity(), Log_in.class));
        }
        if (view == buttonEditProfile){
            startActivity(new Intent(getActivity(), Edit_Profile.class));
        }
        if (view == buttonViewReview){
            startActivity(new Intent(getActivity(), View_reviewedrestau.class));
        }
    }
}

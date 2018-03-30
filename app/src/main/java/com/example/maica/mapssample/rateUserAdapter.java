package com.example.maica.mapssample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Maica on 9/17/2017.
 */

public class rateUserAdapter extends ArrayAdapter<AddReview> {
    private Context context;
    private int resource;
    private List<AddReview> listImage;

    public rateUserAdapter (Context context, int resource, List<AddReview> objects){
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listImage = objects;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.rateuser_layout, parent, false);
        final TextView txtrate = (TextView) row.findViewById(R.id.textView3);
        final TextView txtcomment = (TextView) row.findViewById(R.id.textView4);
        final TextView txtuser = (TextView) row.findViewById(R.id.textView5);

        final String rate = "Rating: "+ listImage.get(position).getRateNum();
        //txtrate.setText(rate);
        txtrate.setVisibility(View.GONE);

        final String comm = "Review: "+ listImage.get(position).getRateComment();
        //txtcomment.setText(comm);
        txtcomment.setVisibility(View.GONE);

        String uid = listImage.get(position).getRateUser();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        Query search = dbref.child("Users").orderByChild("userID").equalTo(uid);
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ProfileInformation profile = snapshot.getValue(ProfileInformation.class);
                    String userr = profile.getUsername();
                    String alls = userr + "\n" + rate + "\n" + comm;
                    txtuser.setText(alls);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return  row;
    }
}

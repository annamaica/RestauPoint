package com.example.maica.mapssample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class rateUserAdapter2 extends ArrayAdapter<AddReview> {
    private Context context;
    private int resource;
    private List<AddReview> listImage;

    public rateUserAdapter2(Context context, int resource, List<AddReview> objects){
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
        TextView txtrate = (TextView) row.findViewById(R.id.textView3);
        TextView txtcomment = (TextView) row.findViewById(R.id.textView4);
        final TextView txtuser = (TextView) row.findViewById(R.id.textView5);
        final TextView txtid = (TextView) row.findViewById(R.id.textView9);

        String rate = "Rating: "+ listImage.get(position).getRateNum();
        txtrate.setText(rate);

        String comm = "Review: "+ listImage.get(position).getRateComment();
        txtcomment.setText(comm);

        String restau = listImage.get(position).getRateUser();
        txtuser.setText(restau);

        txtid.setText(listImage.get(position).getRateID());
        txtid.setVisibility(View.INVISIBLE);

        return  row;
    }
}

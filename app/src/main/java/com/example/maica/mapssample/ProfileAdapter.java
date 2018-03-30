package com.example.maica.mapssample;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maica on 9/3/2017.
 */

public class ProfileAdapter extends ArrayAdapter <ProfileInformation> {
    private Context context;
    private int resource;
    private List<ProfileInformation> listImage;

    public ProfileAdapter(Context context, int resource, List<ProfileInformation> objects){
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
        row = layoutInflater.inflate(R.layout.userlist,parent,false);

        TextView textView = (TextView)row.findViewById(R.id.textView10);
        textView.setText(listImage.get(position).getUsername());

        TextView textView1 = (TextView)row.findViewById(R.id.textView8);
        textView1.setText(listImage.get(position).getFirstname());

        TextView textView2 = (TextView)row.findViewById(R.id.textView11);
        textView2.setText(listImage.get(position).getLastname());

        return row;
    }
}

package com.example.maica.mapssample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Maica on 9/21/2017.
 */

public class MenuListAdapter extends ArrayAdapter<Menu> {
    private Context context;
    private int resource;
    private List<Menu> listImage;

    public MenuListAdapter(Context context, int resource, List<Menu> objects) {
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
        row = layoutInflater.inflate(R.layout.menu_rowlist, parent, false);
        TextView txtrate = (TextView) row.findViewById(R.id.textView6);
        TextView txtcomment = (TextView) row.findViewById(R.id.textView7);
        ImageView img = (ImageView) row.findViewById(R.id.imageView) ;


        Glide.with(context).load(listImage.get(position).getPicture()).into(img);
        txtrate.setText(listImage.get(position).getMenu());
        txtcomment.setText(listImage.get(position).getPrice());


        return row;
    }
}
package com.softwareag.ecp.parking_pi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.softwareag.ecp.parking_pi.BeanClass.Places;

import java.util.ArrayList;

/**
 * Created by KAVI on 07-07-2016.
 */
public class MainActivityArrayAdapter extends ArrayAdapter<Places>{

    private Activity context;

    public MainActivityArrayAdapter(Activity context, int resource, ArrayList<Places> placesArrayList) {
        super(context, R.layout.activity_main_listview_layout, placesArrayList);
        this.context = context;
    }

    public class ViewHolder{
        TextView text1;
        TextView text2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Places places = getItem(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_main_listview_layout, parent, false);
            holder.text1 = (TextView)convertView.findViewById(R.id.textView2);
            holder.text2 = (TextView)convertView.findViewById(R.id.textView3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.text1.setText(places.getPlaceName());
        holder.text2.setText(places.getVicinity());

        return convertView;
    }
}

package com.softwareag.ecp.parking_pi.PageControllers;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareag.ecp.parking_pi.BeanClass.Place;
import com.softwareag.ecp.parking_pi.BeanClass.Variables;
import com.softwareag.ecp.parking_pi.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KAVI on 07-07-2016.
 */
public class MainListViewLayout extends ArrayAdapter<Place> {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    private Activity context;

    public MainListViewLayout(Activity context, int resource, ArrayList<Place> placesArrayList) {
        super(context, R.layout.activity_main_listview_layout, placesArrayList);
        this.context = context;
        Log.i(MESSAGE_LOG, "MainListViewLayout -> constructor");
    }

    public class ViewHolder {
        TextView text1;
        TextView text2;
        ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(MESSAGE_LOG, "MainListViewLayout -> getView");
        Place places = getItem(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_main_listview_layout, parent, false);
            holder.text1 = (TextView) convertView.findViewById(R.id.placeTitle);
            holder.text2 = (TextView) convertView.findViewById(R.id.placeSubTitle);
            holder.icon = (ImageView) convertView.findViewById(R.id.place_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(places.getPlaceName());
        holder.text2.setText(places.getVicinity());

        if (places.getPhoto_reference() != null) {
            Picasso.with(context).load(placePhotosURL(places.getPhoto_reference())).into(holder.icon);
        } else {
            Picasso.with(context).load(Variables.getNoImageUrl()).into(holder.icon);
        }
        return convertView;
    }

    public String placePhotosURL(String photoReference) {
        Log.i(MESSAGE_LOG, "MainActivity -> placePhotos");
        StringBuilder sb = new StringBuilder(Variables.getGooglePhotourl());
        sb.append("maxwidth=400");
        sb.append("&photoreference=").append(photoReference);
        sb.append("&key=").append(Variables.getGoogleApiKey());
        Log.d(MESSAGE_LOG, "Google Photos URL api: " + sb.toString());
        return sb.toString();
    }


}
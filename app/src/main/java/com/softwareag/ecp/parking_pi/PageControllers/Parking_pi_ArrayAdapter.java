package com.softwareag.ecp.parking_pi.PageControllers;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareag.ecp.parking_pi.BeanClass.Location;
import com.softwareag.ecp.parking_pi.R;

import java.util.List;

/**
 * Created by KAVI on 22-06-2016.
 */
public class Parking_pi_ArrayAdapter extends ArrayAdapter<Location> {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    private Activity context;
    private int locationListSize;
    private List<Location> locationsList;
    private Dialog dialog;

    public Parking_pi_ArrayAdapter(Activity context, int resource, List<Location> locationsList) {
        super(context, resource, locationsList);
        this.context = context;

        Log.i(MESSAGE_LOG, "Parking_pi_ArrayAdapter -> Parking_pi_ArrayAdapter");

        locationListSize = locationsList.size();
        this.locationsList = locationsList;

        dialog = new Dialog(context);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.12);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_error_page);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public class ViewHolder {
        ImageView car;
        TextView availability;

        ImageView car1;
        TextView availability1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i(MESSAGE_LOG, "Parking_pi_ArrayAdapter -> getView");
        Location locations = getItem(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.availability_layout, parent, false);
            holder.car = (ImageView) convertView.findViewById(R.id.imageView3);
            holder.availability = (TextView) convertView.findViewById(R.id.textView4);

            holder.car1 = (ImageView) convertView.findViewById(R.id.imageView);
            holder.availability1 = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!locations.isActive()) {
            dialog.show();
        }
        if (locations.getStatus().equals("available")) {
            holder.availability.setText(locations.getName());
            Drawable draw = holder.car.getDrawable();
            if (draw != null) {
                holder.car.setImageDrawable(null);
            }
        } else {
            holder.car.setImageResource(R.drawable.car);
            holder.availability.setText(locations.getName());
        }

        if (locations.getStatus1().equals("available")) {
            holder.availability1.setText(locations.getName1());

            Drawable draw1 = holder.car1.getDrawable();
            if (draw1 != null) {
                holder.car1.setImageDrawable(null);
            }
        } else {
            holder.car1.setImageResource(R.drawable.car1);
            holder.availability1.setText(locations.getName1());
        }
        return convertView;
    }
}

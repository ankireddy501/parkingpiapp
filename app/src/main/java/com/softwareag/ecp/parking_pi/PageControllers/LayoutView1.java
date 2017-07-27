package com.softwareag.ecp.parking_pi.PageControllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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


public class LayoutView1 extends ArrayAdapter<Location> {


    private final String MESSAGE_LOG = "PARKING_PI APP";
    private Activity context;
    private int locationSize;
    private List<Location> locationList;
    private Dialog dialog;

    public LayoutView1(Activity context, int resource, List<Location> locationsList) {
        super(context, resource, locationsList);
        this.context = context;
        this.locationList = locationsList;
        locationSize = locationsList.size();

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
        ImageView car1;
        TextView textView1;

        ImageView car2;
        TextView textView2;
    }

    @Override
    public View getView(int postition, View convertView, ViewGroup parent) {
        Log.i(MESSAGE_LOG, "LayoutView1 -> getView");

        Location location = getItem(postition);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_layout_view1, parent, false);

            holder.car1 = (ImageView) convertView.findViewById(R.id.listImage1);
            holder.textView1 = (TextView) convertView.findViewById(R.id.listText1);

            holder.car2 = (ImageView) convertView.findViewById(R.id.listImage2);
            holder.textView2 = (TextView) convertView.findViewById(R.id.listText2);
            convertView.setTag(holder);
        } else {
            holder = (LayoutView1.ViewHolder) convertView.getTag();
        }
        if (!location.isActive()) {
            dialog.show();
        }
        if (location.getStatus().equals("available")) {
            holder.textView1.setText(location.getName());
            Drawable draw = holder.car1.getDrawable();
            if (draw != null) {
                holder.car1.setImageDrawable(null);
            }
        } else {
            holder.car1.setImageResource(R.drawable.car);
            holder.textView1.setText(location.getName());
        }

        if (location.getStatus1().equals("available")) {
            holder.textView2.setText(location.getName1());

            Drawable draw1 = holder.car2.getDrawable();
            if (draw1 != null) {
                holder.car2.setImageDrawable(null);
            }
        } else {
            holder.car2.setImageResource(R.drawable.car);
            holder.textView2.setText(location.getName1());
        }
        return convertView;
    }


}

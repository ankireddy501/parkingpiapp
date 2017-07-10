package com.softwareag.ecp.parking_pi.PageControllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwareag.ecp.parking_pi.BeanClass.Variables;
import com.softwareag.ecp.parking_pi.R;
import com.squareup.picasso.Picasso;

public class ErrorPage extends Activity {

    public class ErrorBean {
        private String errorMessage;
        private String errorImageURL;

        ErrorBean(String errorMessage, String errorImageUrl) {
            this.errorMessage = errorMessage;
            this.errorImageURL = errorImageUrl;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorImageURL() {
            return errorImageURL;
        }

        public void setErrorImageURL(String errorImageURL) {
            this.errorImageURL = errorImageURL;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_page);

        ViewGroup viewGroup = null;

        LayoutInflater layout = this.getLayoutInflater();
        View view = layout.inflate(R.layout.activity_error_page, viewGroup);
        TextView text = (TextView) view.findViewById(R.id.error_dialouge);
        ImageView image = (ImageView) view.findViewById(R.id.error_image);

        ErrorBean bean = createError(Variables.getErrorMessage(), Variables.getSadSmileyUrl());
        text.setText(bean.getErrorMessage());
        Picasso.with(this).load(bean.errorImageURL).into(image);


    }

    public ErrorBean createError(String errorMessage, String errorImageUrl) {
        ErrorBean bean = new ErrorBean(errorMessage, errorImageUrl);
        return bean;
    }
}

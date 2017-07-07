package com.softwareag.ecp.parking_pi;

import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.AllLocations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KAVI on 23-06-2016.
 */
public class MainActivityJsonParser {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    public List<AllLocations> getAllLocations(String locationsJsonString) throws JSONException {

        Log.v(MESSAGE_LOG, "MainActivityJsonParser -> getAllLocations");
        List<AllLocations> allLocationsArrayList = new ArrayList<>();

        if (locationsJsonString == null || locationsJsonString.length() == 0) {
            return new ArrayList<>();
        }

        JSONArray locationAry = new JSONObject(locationsJsonString).getJSONArray("locations");
        AllLocations allLocations;
        for (int i = 0; i < locationAry.length(); i++) {
            JSONObject locations = (JSONObject) locationAry.get(i);
            String name = locations.getString("name");
            Double lattitude = locations.getDouble("lattitude");
            Double longitude = locations.getDouble("longitude");
            int total = locations.getInt("total");
            int available = locations.getInt("available");
            boolean isActive = locations.getBoolean("active");
            allLocations = new AllLocations(name, lattitude, longitude, total, available, isActive);
            allLocationsArrayList.add(allLocations);
        }
        return allLocationsArrayList;
    }
}

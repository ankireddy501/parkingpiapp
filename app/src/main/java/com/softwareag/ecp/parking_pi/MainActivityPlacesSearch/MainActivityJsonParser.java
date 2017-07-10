package com.softwareag.ecp.parking_pi.MainActivityPlacesSearch;

import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.AllLocation;

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

    public List<AllLocation> getAllLocations(String locationsJsonString) throws JSONException {

        Log.v(MESSAGE_LOG, "MainActivityJsonParser -> getAllLocations");
        List<AllLocation> allLocationsArrayList = new ArrayList<>();

        if (locationsJsonString == null || locationsJsonString.length() == 0) {
            return new ArrayList<>();
        }

        JSONArray locationAry = new JSONObject(locationsJsonString).getJSONArray("locations");
        AllLocation allLocations;
        for (int i = 0; i < locationAry.length(); i++) {
            JSONObject locations = (JSONObject) locationAry.get(i);
            String name = locations.getString("name");
            Double lattitude = locations.getDouble("lattitude");
            Double longitude = locations.getDouble("longitude");
            int total = locations.getInt("total");
            int available = locations.getInt("available");
            boolean isActive = locations.getBoolean("active");
            allLocations = new AllLocation(name, lattitude, longitude, total, available, isActive);
            allLocationsArrayList.add(allLocations);
        }
        return allLocationsArrayList;
    }
}

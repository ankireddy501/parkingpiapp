package com.softwareag.ecp.parking_pi;

import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.Locations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KAVI on 21-06-2016.
 */
public class AvailabilityActivityJsonParser {

    private final String MESSAGE_LOG = "PARKING_PI APP";


    public ArrayList<Locations> getAvailability(String jsonString) throws JSONException {

        Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> getAvailability()");

        JSONObject json = new JSONObject(jsonString);
        ArrayList<Locations> locationsArrayList = new ArrayList<Locations>();

        String branchName = json.getString("name");
        Double lattitude = json.getDouble("lattitude");
        Double longitude = json.getDouble("longitude");
        int total = json.getInt("total");
        int available = json.getInt("available");
        boolean isActive = json.getBoolean("active");
        Locations locations = null;
        JSONArray array = json.getJSONArray("slots");
        Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> JsonParser array length " + array.length());

        for (int i = 0; i < array.length(); i++) {
            JSONObject slots = (JSONObject) array.get(i);
            String name = slots.getString("name");
            String status = slots.getString("status");
            String ownerId = slots.getString("ownerId");
            Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> array list " + name + " " + status + " " + ownerId);
            Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> value of i before increment " + i);
            i = ++i;
            Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> value of i after increment " + i);

            JSONObject slots1 = (JSONObject) array.get(i);
            String name1 = slots1.getString("name");
            String status1 = slots1.getString("status");
            String ownerId1 = slots1.getString("ownerId");

            Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> array list " + name1 + " " + status1 + " " + ownerId1);
            locations = new Locations(branchName, lattitude, longitude, total, available, isActive,
                    name, status, ownerId,
                    name1, status1, ownerId1);
            locationsArrayList.add(locations);
        }
        Log.i(MESSAGE_LOG, "AvailabilityActivityJsonParser -> ARRAY LIST SIZE " + locationsArrayList.size());
        return locationsArrayList;
    }
}

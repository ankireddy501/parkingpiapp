package com.softwareag.ecp.parking_pi;

import com.softwareag.ecp.parking_pi.BeanClass.AllLocations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KAVI on 23-06-2016.
 */
public class MainActivityJsonParser {

    ArrayList<AllLocations> allLocationsArrayList;

    public ArrayList<AllLocations> getAllLocations(String locationsJsonString)throws JSONException{

        JSONObject locationsObj = new JSONObject(locationsJsonString);

        return getAllLocations(locationsObj);
    }

    public ArrayList<AllLocations> getAllLocations(JSONObject locationsObj)throws JSONException{
        if (locationsObj == null){
            return null;
        }
        AllLocations allLocations = null;
        allLocationsArrayList = new ArrayList<AllLocations>();

        JSONArray locationAry = locationsObj.getJSONArray("locations");

        for(int i=0; i < locationAry.length(); i++){
            JSONObject locations = (JSONObject)locationAry.get(i);
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

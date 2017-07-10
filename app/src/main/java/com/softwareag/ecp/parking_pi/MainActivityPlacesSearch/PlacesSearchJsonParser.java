package com.softwareag.ecp.parking_pi.MainActivityPlacesSearch;

import android.util.Log;

import com.softwareag.ecp.parking_pi.BeanClass.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KAVI on 07-07-2016.
 */
public class PlacesSearchJsonParser {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    public ArrayList<Place> getPlaces(String placeData) throws JSONException {

        ArrayList<Place> placesArrayList = new ArrayList<>();

        JSONObject json = new JSONObject(placeData);
        JSONArray placesAry = json.getJSONArray("results");

        for (int i = 0; i < placesAry.length(); i++) {

            Place place = new Place();

            JSONObject placesJson = (JSONObject) placesAry.get(i);
            place.setPlaceName(placesJson.getString("name"));
            place.setVicinity(placesJson.getString("vicinity"));
            place.setLattitude(placesJson.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            place.setLongitude(placesJson.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            place.setIcon(placesJson.getString("icon"));
            place.setReference(placesJson.getString("reference"));

            if (placesJson.has("photos")) {
                JSONArray photo_referenceArray = placesJson.getJSONArray("photos");
                JSONObject photoJSON = (JSONObject) photo_referenceArray.get(0);
                place.setPhoto_reference(photoJSON.getString("photo_reference"));
            }

            placesArrayList.add(place);
        }

        for (Place place : placesArrayList) {
            Log.d("Place : ", place.getPlaceName() + place.getPhoto_reference());
        }

        return placesArrayList;
    }
}

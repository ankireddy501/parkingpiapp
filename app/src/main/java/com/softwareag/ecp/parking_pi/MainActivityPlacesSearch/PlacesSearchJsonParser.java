package com.softwareag.ecp.parking_pi.MainActivityPlacesSearch;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softwareag.ecp.parking_pi.BeanClass.Places;
import com.softwareag.ecp.parking_pi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KAVI on 07-07-2016.
 */
public class PlacesSearchJsonParser {

    private final String MESSAGE_LOG = "PARKING_PI APP";

    public ArrayList<Places> getPlaces(String placeData) throws JSONException {

        ArrayList<Places> placesArrayList = new ArrayList<>();

        JSONObject json = new JSONObject(placeData);
        JSONArray placesAry = json.getJSONArray("results");

        for (int i = 0; i < placesAry.length(); i++) {

            Places place = new Places();

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

/*
            String placeName = placesJson.getString("name");
            String vicinity = placesJson.getString("vicinity");
            String lattitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            String reference = placesJson.getString("reference");
            String icon = placesJson.getString("icon");
            String photo_reference = photoJSON.getString("photo_reference");
            Places places = new Places(placeName, vicinity, lattitude, longitude, reference, icon, photo_reference);
*/
            placesArrayList.add(place);
        }

        for (Places place : placesArrayList) {
            Log.d("Places : ", place.getPlaceName() + place.getPhoto_reference());
        }

        return placesArrayList;
    }
}

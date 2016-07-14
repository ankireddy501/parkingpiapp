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
    ArrayList<Places> placesArrayList;

    public ArrayList<Places> getPlaces(String placeDatas)throws JSONException{

        JSONObject json = new JSONObject(placeDatas);

        return getPlaces(json);
    }

    public ArrayList<Places> getPlaces(JSONObject placesObj)throws JSONException{

        if(placesObj == null){
            return null;
        }
        placesArrayList = new ArrayList<Places>();

        JSONArray placesAry = placesObj.getJSONArray("results");

        for(int i=0; i<placesAry.length(); i++){
            JSONObject placesJson = (JSONObject)placesAry.get(i);
            String placeName = placesJson.getString("name");
            String vicinity = placesJson.getString("vicinity");
            String lattitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = placesJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            String reference = placesJson.getString("reference");

            Log.v("PLACES ","OBJ "+placeName+ " "+vicinity+" "+lattitude+ " "+longitude);
            Places places = new Places(placeName, vicinity, lattitude, longitude, reference);
            placesArrayList.add(places);
        }



        return placesArrayList;
    }

    public void setMarkers(GoogleMap googleMap){

        for(int i =0; i<placesArrayList.size(); i++){
            Double lattitude = Double.parseDouble(placesArrayList.get(i).getLattitude());
            Double longitude = Double.parseDouble(placesArrayList.get(i).getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurants))
                    .title(placesArrayList.get(i).getPlaceName())
                    .position(new LatLng(lattitude, longitude)));
        }
    }
}

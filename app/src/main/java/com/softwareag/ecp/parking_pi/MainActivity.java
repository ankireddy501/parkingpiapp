package com.softwareag.ecp.parking_pi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softwareag.ecp.parking_pi.BeanClass.AllLocations;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.PlacesSearchAsyncTask;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.PlacesSearchJsonParser;
import com.softwareag.ecp.parking_pi.Service.AllLocationSearchService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener ,OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks {

    private List<AllLocations> allLocationsArrayList;
    private TimerTask timertask;
    private Timer timer;
    private GoogleMap googleMaps;
    private GoogleApiClient apiClient;
    private MainActivityArrayAdapter arrayAdapter;
    private ListView listView;
    private Marker mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        HttpUrlConnectionAsyncTask connection = new HttpUrlConnectionAsyncTask(this, "");
        MainActivityJsonParser jsonParser = new MainActivityJsonParser();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String vmName = preferences.getString("VMName", null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setVisibility(View.GONE);

        apiClient =  new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (vmName == null || vmName.isEmpty()) {
            Intent intent = new Intent(this, ChangeVMActivity.class);
            startActivity(intent);
        }

        String locations;
        try {
            locations = connection.execute().get();
            if (locations == null) {
                //Do what has to be done when the locations is not available.
                return;
            }
            allLocationsArrayList = jsonParser.getAllLocations(locations);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            //Do what has to be done when the locations is not available.
            return;
        }

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // This will start the background timer task. It will refresh the connection for every 3.5sec and will save the
        // datas in shared preference
        Intent intent = new Intent(this, AllLocationSearchService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMaps = googleMap;
        Log.v("MainActivity ", "allLocationsArrayList " + allLocationsArrayList.size());
        for(int i = 0; i < allLocationsArrayList.size(); i ++) {
            AllLocations allLocations = allLocationsArrayList.get(i);
            createMarker(allLocations.getName(), allLocations.getLattitude(),
                    allLocations.getLongitude(), allLocations.getTotal(),
                    allLocations.getAvailable(), allLocations.isActive(), googleMap);
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String location = marker.getTitle();
                location = location.replaceAll(" ","%20");
                HttpUrlConnectionAsyncTask connection = new HttpUrlConnectionAsyncTask(MainActivity.this, location);

                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    List<Address> str = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    int maxAddressLineIndex = str.get(0).getMaxAddressLineIndex();
                    StringBuilder builder = new StringBuilder();
                    for(int i = 1; i < maxAddressLineIndex; i ++){
                        String address = str.get(0).getAddressLine(i);
                        builder.append(address).append("\n");
                    }
                    Log.v("ADDRESS "," "+builder.toString());

                    String data = connection.execute().get();
                    if(data != null){
                        Log.v("MainActivity ", "get datas based on selected location " + data);
                        Intent intent = new Intent(MainActivity.this, AvailabilityActivity.class);
                        intent.putExtra("LocationBasedDatas", data);
                        intent.putExtra("branchName",location);
                        intent.putExtra("address",builder.toString());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e("MainActivity", e.getMessage());
                }


            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                prepareListView(marker.getPosition().latitude, marker.getPosition().longitude);
                return false;
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.v("MainActivity ", "place to search for " + place.getName());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                googleMap.addMarker(new MarkerOptions().title(
                        (String) place.getName()).position(place.getLatLng()));
                listView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Status status) {
                Log.e("MainActivity", "Error " + status);
            }
        });

        if (allLocationsArrayList == null || allLocationsArrayList.size() == 0) {
            Intent intent = new Intent(this, ChangeVMActivity.class);
            startActivity(intent);
        }
        LatLng latlng = new LatLng(allLocationsArrayList.get(0).getLattitude(),
                allLocationsArrayList.get(0).getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
    }

    public void createMarker(String name, Double lattitude, Double longitude, int total,
                             int available, boolean isActive, GoogleMap googleMap){
        LatLng latlng = new LatLng(lattitude, longitude);
        Log.v("LATTITUDE ", "LONGITUDE " + name + " " + lattitude + " " + available);

        if(isActive) {
            if (available == 0) {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.not_available1))
                        .title(name).position(latlng));
            } else if (available <= 2 && available != 0) {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.fast_filling))
                        .title(name).position(latlng));
            } else {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.available1))
                        .title(name).position(latlng));
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        timerTask();
        apiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        timertask.cancel();
    }

    public void timerTask(){
        timer = new Timer();
        final Handler handler = new Handler();
        timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Retrieves the data which has stored in the shared preference after refreshing the connection in
                        // the service class
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        String newData = preferences.getString("All locations", null);

                        List<AllLocations> arrayList;
                        MainActivityJsonParser jsonParser = new MainActivityJsonParser();
                        try {
                            arrayList = jsonParser.getAllLocations(newData);
                            refreshData(arrayList);
                        } catch (JSONException e) {
                            Log.e("MainActivity", "Error while parsing JSON");
                        }
                        Log.v("Main activity ", "fetch data " + newData);

                    }
                });
            }
        };
        timer.schedule(timertask, 400, 3500);
    }

    public void refreshData(List<AllLocations> locationsArrayList){
        // This will update the marker in the map for every 3.5 sec
        for (int i = 0; i < locationsArrayList.size(); i++) {
            if (locationsArrayList.get(i).isActive()) {
                mark.setPosition(new LatLng(locationsArrayList.get(i).getLattitude(),
                        locationsArrayList.get(i).getLongitude()));
                mark.setTitle(allLocationsArrayList.get(i).getName());
            }
        }
    }

    public void prepareListView(Double lattitude, Double longitude){
        StringBuilder sbValue = new StringBuilder(sbMethod(lattitude, longitude));
        PlacesSearchAsyncTask placesSearchAsyncTask = new PlacesSearchAsyncTask();
        listView.setVisibility(View.VISIBLE);
        try {
            String placeData = placesSearchAsyncTask.execute(sbValue.toString()).get();
            PlacesSearchJsonParser parser = new PlacesSearchJsonParser();
            ArrayList<com.softwareag.ecp.parking_pi.BeanClass.Places> placesArrayList =
                    parser.getPlaces(placeData);
            arrayAdapter = new MainActivityArrayAdapter(MainActivity.this, 0, placesArrayList);
            listView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.softwareag.ecp.parking_pi.BeanClass.Places selectedPlace =
                        arrayAdapter.getItem(position);
                Double lattitude = Double.parseDouble(selectedPlace.getLattitude());
                Double longitude = Double.parseDouble(selectedPlace.getLongitude());
                Log.v("lat ", "lng " + lattitude + " " + longitude);
                LatLng latlng = new LatLng(lattitude, longitude);
                googleMaps.addMarker(new MarkerOptions().title(
                        selectedPlace.getPlaceName()).position(latlng)).showInfoWindow();
                googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 21));

            }
        });
    }

    public StringBuilder sbMethod(Double lattitude, Double longitude) {
        //use your current location here
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=").append(lattitude).append(",").append(longitude);
        sb.append("&radius=500");
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyArF3avQzRG_ddobYilkCHt7tO044ZIDmk");
        Log.d("Map", "api: " + sb.toString());
        return sb;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("onActivity ","result ");
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                Log.v("Result ok "," datas "+place.getName());
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Connection failed ", Toast.LENGTH_SHORT).show();
    }

}

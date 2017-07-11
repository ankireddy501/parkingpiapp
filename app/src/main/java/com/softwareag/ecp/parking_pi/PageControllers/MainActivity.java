package com.softwareag.ecp.parking_pi.PageControllers;

import android.content.Context;
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
import com.softwareag.ecp.parking_pi.BeanClass.AllLocation;
import com.softwareag.ecp.parking_pi.BeanClass.Variables;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.MainActivityJsonParser;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.PlacesSearchAsyncTask;
import com.softwareag.ecp.parking_pi.MainActivityPlacesSearch.PlacesSearchJsonParser;
import com.softwareag.ecp.parking_pi.R;
import com.softwareag.ecp.parking_pi.Service.AllLocationSearchService;
import com.softwareag.ecp.parking_pi.Service.AvailabilityActivity;
import com.softwareag.ecp.parking_pi.Service.URLLocationAccess;
import com.softwareag.ecp.parking_pi.Service.URLLocationWithBranch;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks {


    private final String MESSAGE_LOG = "PARKING_PI APP";

    private List<AllLocation> allLocationsArrayList;
    private TimerTask timertask;
    private Timer timer;
    private GoogleMap googleMaps;
    private GoogleApiClient apiClient;
    private MainListViewLayout arrayAdapter;
    private ListView listView;
    private Marker mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(MESSAGE_LOG, "MainActivity -> onCreate");

        MainActivityJsonParser jsonParser = new MainActivityJsonParser();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String vmName = preferences.getString("VMName", null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.placesData);
        listView.setVisibility(View.GONE);

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (vmName == null || vmName.isEmpty()) {
            Log.i(MESSAGE_LOG, "MainActivity -> vmName == null || vmName.isEmpty()");
            Intent intent = new Intent(this, ChangeVMActivity.class);
            startActivity(intent);
        }

        String locations;
        try {
            URLLocationAccess connection = new URLLocationAccess(this);
            locations = connection.execute().get();
            if (locations == null) {
                Log.i(MESSAGE_LOG, "MainActivity -> locationEmpty");

            }
            allLocationsArrayList = jsonParser.getAllLocations(locations);
        } catch (JSONException e) {
            Log.e(MESSAGE_LOG, "MainActivity -> JSONException" + e.getMessage().toString());
        } catch (InterruptedException e) {
            Log.e(MESSAGE_LOG, "MainActivity -> InterruptedException" + e.getMessage().toString());
        } catch (ExecutionException e) {
            Log.e(MESSAGE_LOG, "MainActivity -> ExecutionException" + e.getMessage().toString());
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // This will start the background timer task. It will refresh the connection for every 3.5sec and will save the
        // datas in shared preference
        Intent intent = new Intent(this, AllLocationSearchService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMaps = googleMap;
        Log.i(MESSAGE_LOG, "MainActivity -> onMapReady " + allLocationsArrayList.size());
        for (int i = 0; i < allLocationsArrayList.size(); i++) {
            AllLocation allLocations = allLocationsArrayList.get(i);
            createMarker(allLocations.getName(), allLocations.getLattitude(),
                    allLocations.getLongitude(), allLocations.getTotal(),
                    allLocations.getAvailable(), allLocations.isActive(), googleMap);
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(MESSAGE_LOG, "MainActivity -> googleMap.setOnInfoWindowClickListener");
                String location = marker.getTitle();
                location = location.replaceAll(" ", "%20");
                URLLocationWithBranch connectionWithBranch = new URLLocationWithBranch(MainActivity.this, location);

                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    List<Address> str = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    int maxAddressLineIndex = str.get(0).getMaxAddressLineIndex();
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < maxAddressLineIndex; i++) {
                        String address = str.get(0).getAddressLine(i);
                        builder.append(address).append("\n");
                    }
                    Log.i("ADDRESS ", " " + builder.toString());

                    String data = connectionWithBranch.execute().get();
                    if (data != null) {
                        Log.i("MainActivity ", "get datas based on selected location " + data);
                        Intent intent = new Intent(MainActivity.this, AvailabilityActivity.class);
                        intent.putExtra("LocationBasedDatas", data);
                        intent.putExtra("branchName", location);
                        intent.putExtra("address", builder.toString());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(MESSAGE_LOG, "MainActivity -> googleMap.setOnInfoWindowClickListener -> Exception" + e.getMessage().toString());
                }

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(MESSAGE_LOG, "MainActivity -> googleMap.setOnMarkerClickListener");
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
                Log.i(MESSAGE_LOG, "MainActivity -> place to search for " + place.getName());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                googleMap.addMarker(new MarkerOptions().title(
                        (String) place.getName()).position(place.getLatLng()));
                listView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Status status) {
                Log.e(MESSAGE_LOG, "MainActivity -> onError - Status " + status);
            }
        });

        if (allLocationsArrayList == null || allLocationsArrayList.size() == 0) {
            Log.i(MESSAGE_LOG, "MainActivity -> allLocationsArrayList == null ");
            Context context = getApplicationContext();
            CharSequence text = "Server Connection Failed!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Intent intent = new Intent(this, ChangeVMActivity.class);
            startActivity(intent);
        } else {
            LatLng latlng = new LatLng(allLocationsArrayList.get(0).getLattitude(),
                    allLocationsArrayList.get(0).getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
        }
    }

    public void createMarker(String name, Double lattitude, Double longitude, int total,
                             int available, boolean isActive, GoogleMap googleMap) {
        LatLng latlng = new LatLng(lattitude, longitude);
        Log.i(MESSAGE_LOG, "MainActivity -> LATTITUDE LONGITUDE " + name + " " + lattitude + " " + available);
        if (isActive) {
            if (available == 0) {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.not_available1))
                        .title(name).position(latlng));
            } else if (available == total) {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.available1))
                        .title(name).position(latlng));
            } else {
                mark = googleMap.addMarker(new MarkerOptions().icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.fast_filling))
                        .title(name).position(latlng));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(MESSAGE_LOG, "MainActivity -> onBackPressed");
        listView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        Log.i(MESSAGE_LOG, "MainActivity -> onStart");
        super.onStart();
        timerTask();
        apiClient.connect();
    }

    protected void onPause() {
        Log.i(MESSAGE_LOG, "MainActivity -> onPause");
        super.onPause();
    }

    public void timerTask() {
        timer = new Timer();
        final Handler handler = new Handler();
        timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(MESSAGE_LOG, "MainActivity -> timerTask");
                        //Retrieves the data which has stored in the shared preference after refreshing the connection in
                        // the service class
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        String newData = preferences.getString("All locations", null);

                        List<AllLocation> arrayList;
                        MainActivityJsonParser jsonParser = new MainActivityJsonParser();
                        try {
                            arrayList = jsonParser.getAllLocations(newData);
                            refreshData(arrayList);
                        } catch (JSONException e) {
                            Log.e(MESSAGE_LOG, "MainActivity -> timerTask JSONException " + e.getMessage().toString());
                        }
                        Log.v(MESSAGE_LOG, "MainActivity -> Fetching Data :" + newData);
                    }
                });
            }
        };
        timer.schedule(timertask, 400, 3500);
    }

    public void refreshData(List<AllLocation> arrayList) {
        // This will update the marker in the map for every 3.5 sec
        Log.v(MESSAGE_LOG, "MainActivity -> refreshData");
        if (mark != null) {
            for (AllLocation location : arrayList) {
                if (location.isActive()) {
                    mark.setPosition(new LatLng(location.getLattitude(),
                            location.getLongitude()));
                    mark.setTitle(location.getName());
                }
            }
        } else {
            boolean value = false;
            if (value = false) {
                Context context = getApplicationContext();
                CharSequence text = "Server Connection Failed!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                value = true;
            }
        }

    }

    public void prepareListView(Double lattitude, Double longitude) {
        Log.i(MESSAGE_LOG, "MainActivity -> prepareListView");
        StringBuilder sbValue = new StringBuilder(sbMethod(lattitude, longitude));
        PlacesSearchAsyncTask placesSearchAsyncTask = new PlacesSearchAsyncTask();
        listView.setVisibility(View.VISIBLE);
        try {
            String placeData = placesSearchAsyncTask.execute(sbValue.toString()).get();
            PlacesSearchJsonParser parser = new PlacesSearchJsonParser();
            ArrayList<com.softwareag.ecp.parking_pi.BeanClass.Place> placesArrayList =
                    parser.getPlaces(placeData);

            for (com.softwareag.ecp.parking_pi.BeanClass.Place photo : placesArrayList) {
                Log.d("PlacesData", photo.getPlaceName() + " " + photo.getPhoto_reference());
            }

            arrayAdapter = new MainListViewLayout(MainActivity.this, 0, placesArrayList);
            listView.setAdapter(arrayAdapter);
        } catch (JSONException e) {
            Log.e(MESSAGE_LOG, "MainActivity -> prepareListView Exception " + e.getMessage().toString());
        } catch (Exception e) {
            Log.e(MESSAGE_LOG, "MainActivity -> prepareListView Exception " + e.getMessage().toString());
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(MESSAGE_LOG, "MainActivity -> listView.setOnItemClickListener");
                com.softwareag.ecp.parking_pi.BeanClass.Place selectedPlace =
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
        Log.i(MESSAGE_LOG, "MainActivity -> sbMethodURL : " + Variables.getGooglePlacesurl());
        StringBuilder sb = new StringBuilder(Variables.getGooglePlacesurl());
        sb.append("location=").append(lattitude).append(",").append(longitude);
        sb.append("&radius=500");
        sb.append("&sensor=true");
        sb.append("&key=").append(Variables.getGoogleApiKey());
        Log.d(MESSAGE_LOG, "Google Place URL api: " + sb.toString());
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
        Log.i(MESSAGE_LOG, "MainActivity -> onActivityResult");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Log.i("Result ok ", " datas " + place.getName());
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(MESSAGE_LOG, "MainActivity -> onConnectionSuspended");
        Toast.makeText(MainActivity.this, "Connection failed ", Toast.LENGTH_SHORT).show();
    }

}

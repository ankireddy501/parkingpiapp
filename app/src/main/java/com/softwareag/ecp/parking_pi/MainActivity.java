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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener ,OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private ArrayList<AllLocations> allLocationsArrayList;
    private TimerTask timertask;
    private Timer timer;
    private GoogleMap googleMaps;
    GoogleApiClient apiClient;
    MainActivityArrayAdapter arrayAdapter;
    private ListView listView;
    Marker mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView2);
        listView.setVisibility(View.GONE);
        HttpUrlConnectionAsyncTask connection = new HttpUrlConnectionAsyncTask(this, "");
        allLocationsArrayList = new ArrayList<AllLocations>();

        try {
            String locations = connection.execute().get();
            MainActivityJsonParser jsonParser = new MainActivityJsonParser();
            allLocationsArrayList = jsonParser.getAllLocations(locations);

        } catch (NullPointerException | InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);


        // This will start the background timer task. It will refresh the connection for every 3.5sec and will save the
        // datas in shared preference
        Intent intent = new Intent(this, AllLocationSearchService.class);
        startService(intent);

        apiClient =  new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMaps = googleMap;

        Log.v("MainActivity ", "allLocationsArrayList " + allLocationsArrayList.size());
       /* googleMap.setMyLocationEnabled(true);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager  = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);


            if(provider!=null && !provider.equals("")) {
                Location location = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 20000, 1, this);

                if (location != null){
                    Log.v("google ", "lattitude " + location.getLatitude());
                    Log.v("google ", "longitude " + location.getLongitude());
                }
                else {
                    Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
            }
        }*/

        for(int i=0; i<allLocationsArrayList.size(); i++){
            createMarker(allLocationsArrayList.get(i).getName(),
                    allLocationsArrayList.get(i).getLattitude(),
                    allLocationsArrayList.get(i).getLongitude(),
                    allLocationsArrayList.get(i).getTotal(),
                    allLocationsArrayList.get(i).getAvailable(),
                    allLocationsArrayList.get(i).isActive(), googleMap);
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
                    int maxAdressLineIndex = str.get(0).getMaxAddressLineIndex();
                    StringBuilder builder = new StringBuilder();
                    for(int i=1;i<maxAdressLineIndex; i++){
                        String address = str.get(0).getAddressLine(i);
                        builder.append(address+"\n");
                    }
                    Log.v("ADDRESS "," "+builder.toString());


                    String data = connection.execute().get();
                    if(data!=null){
                        Log.v("MainActivity ", "get datas based on selected location " + data);
                        Intent intent = new Intent(MainActivity.this, AvailabilityActivity.class);
                        intent.putExtra("LocationBasedDatas", data);
                        intent.putExtra("branchName",location);
                        intent.putExtra("address",builder.toString());
                        startActivity(intent);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.v("MainActivity ", "place to search for " + place.getName());

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12));
                googleMap.addMarker(new MarkerOptions().title((String) place.getName()).position(place.getLatLng()));
                LatLng latLng = place.getLatLng();

                listView.setVisibility(View.GONE);
            }

            @Override
            public void onError(Status status) {

            }
        });

        try{
            LatLng latlng = new LatLng(allLocationsArrayList.get(0).getLattitude(), allLocationsArrayList.get(0).getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
        }catch (IndexOutOfBoundsException e){
            Intent intent = new Intent(this, ChangeVMActivity.class);
            startActivity(intent);
        }


    }

    public void createMarker(String name, Double lattitude, Double longitude, int total , int available, boolean isActive, GoogleMap googleMap){
        LatLng latlng = new LatLng(lattitude, longitude);
        Log.v("LATTITUDE ", "LONGITUDE " + name + " " + lattitude + " " + available);

        if(isActive) {
            if (available == 0) {
                mark = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.not_available1))
                        .title(name).position(latlng));
            } else if (available <= 2 && available != 0) {
                mark = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.fast_filling))
                        .title(name).position(latlng));
            } else {
                mark = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.available1))
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
                        String newDatas = preferences.getString("All locations", null);

                        ArrayList<AllLocations> arrayList = new ArrayList<AllLocations>();
                        MainActivityJsonParser jsonParser = new MainActivityJsonParser();
                        try {
                            arrayList = jsonParser.getAllLocations(newDatas);
                            refreshDatas(arrayList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.v("Main activity ", "fetch datas " + newDatas);

                    }
                });
            }
        };
        timer.schedule(timertask, 400, 3500);
    }

    public void refreshDatas(ArrayList<AllLocations> locationsArrayList){

        // This will update the marker in the map for every 3.5 sec
        try {
            for (int i = 0; i < locationsArrayList.size(); i++) {
            /*createMarker(locationsArrayList.get(i).getName(),
                    locationsArrayList.get(i).getLattitude(),
                    locationsArrayList.get(i).getLongitude(),
                    locationsArrayList.get(i).getTotal(),
                    locationsArrayList.get(i).getAvailable(), googleMaps);*/
                if (locationsArrayList.get(i).isActive()) {
                    mark.setPosition(new LatLng(locationsArrayList.get(i).getLattitude(), locationsArrayList.get(i).getLongitude()));
                    mark.setTitle(allLocationsArrayList.get(i).getName());
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void prepareListView(Double lattitude, Double longitude){
        StringBuilder sbValue = new StringBuilder(sbMethod(lattitude, longitude));
        PlacesSearchAsyncTask placesSearchAsyncTask = new PlacesSearchAsyncTask();

        listView.setVisibility(View.VISIBLE);
        try {
            String placeDatas = placesSearchAsyncTask.execute(sbValue.toString()).get();
            PlacesSearchJsonParser parser = new PlacesSearchJsonParser();
            ArrayList<com.softwareag.ecp.parking_pi.BeanClass.Places> placesArrayList = parser.getPlaces(placeDatas);
          //  parser.setMarkers(googleMaps);
            arrayAdapter = new MainActivityArrayAdapter(MainActivity.this, 0, placesArrayList);
            listView.setAdapter(arrayAdapter);


        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.softwareag.ecp.parking_pi.BeanClass.Places selectedPlace = arrayAdapter.getItem(position);
                Double lattitude = Double.parseDouble(selectedPlace.getLattitude());
                Double longitude = Double.parseDouble(selectedPlace.getLongitude());
                Log.v("lat ", "lng " + lattitude + " " + longitude);
                LatLng latlng = new LatLng(lattitude, longitude);
                googleMaps.addMarker(new MarkerOptions().title(selectedPlace.getPlaceName()).position(latlng)).showInfoWindow();
                googleMaps.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 21));

            }
        });


    }

    public StringBuilder sbMethod(Double lattitude, Double longitude) {

        //use your current location here
      /*  double mLatitude = 13.04757654;
        double mLongitude = 80.23424052;*/

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + lattitude + "," + longitude);
        sb.append("&radius=500");
       // sb.append("&types=" + "restaurant");
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
        /*AddPlaceRequest place =
                new AddPlaceRequest(
                        "Manly Sea Life Sanctuary", // Name
                        new LatLng(-33.7991, 151.2813), // Latitude and longitude
                        "W Esplanade, Manly NSW 2095", // Address
                        Collections.singletonList(Place.TYPE_AQUARIUM), // Place types
                        "+61 1800 199 742", // Phone number
                        Uri.parse("http://www.manlysealifesanctuary.com.au/") // Website
                );

        Places.GeoDataApi.addPlace(apiClient, place)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        Log.i("status", "Place add result: " + places.getStatus().toString());
                        Log.i("status ", "Added place: " + places.get(0).getName().toString());
                        places.release();
                    }
                });*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Connection failed ", Toast.LENGTH_SHORT).show();
    }

}

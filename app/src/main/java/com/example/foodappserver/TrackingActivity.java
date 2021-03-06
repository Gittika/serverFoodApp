package com.example.foodappserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;

import com.example.foodappserver.Remote.IGeoCordinates;
import  com.google.android.gms.location.LocationListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private GoogleMap mMap;
    private  final  static  int PLAY_SERVICE_RESOLUTION_REQUEST =1000;
    private  final  static  int LOCATION_PERMISSION_REQUEST=1001;

    private Location mLastLocation;

    private  GoogleApiClient  mGoogleApiClient;
    private  LocationRequest mLocationRequest;

    private  static  int UPDATE_INTERVAL=1000;
    private static  int FATSET_INTERVAL=5000;
    private  static int DISPLACEMENT =10;
    private IGeoCordinates mService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
         mService=Common.getGeoCodeServices();
   if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){


          requestRuntimePermission();
         }
   else {
       if(checkPlayServices()){

           buildGoogleApiClient();
           createLocationRequest();
       }
   }
   displayLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestRuntimePermission();
        }
        else {
            mLastLocation =LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation!=null)
        {
            double latitude =mLastLocation.getLatitude();
            double longitude=mLastLocation.getLongitude();

            LatLng yourLocation =new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            // After add marker for your location .add marker for this order and draw route
            drawRoute(yourLocation,Common.currentRequest.getAddress());


        }
        }


    }

    private void drawRoute(LatLng yourLocation, String address) {
mService.getGeoCode(address).enqueue(new Callback<String>() {
    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        try {
            JSONObject jsonObject =new JSONObject(response.body().toString());

            String lat =((JSONArray)jsonObject.get("results"))
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .get("lat").toString();

            String lng =((JSONArray)jsonObject.get("results"))
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")
                    .get("lng").toString();

            LatLng orderLocation =new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cart);
            bitmap=Common.scaleBitmap(bitmap,70,70);

            MarkerOptions markerOptions =new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .title("Order of"+Common.currentRequest.getName())
                    .position(orderLocation);
            mMap.addMarker(markerOptions);
//draw route
            mService.getDirections(yourLocation.latitude+";"+yourLocation.longitude,
                    orderLocation.latitude+";"+orderLocation.longitude)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            new ParserTask().execute(response.body().toString());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });






        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {

    }
});

    }


    private void createLocationRequest() {
        mLocationRequest =new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATSET_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient =new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RESOLUTION_REQUEST).show();

            } else {
                Toast.makeText(this, "This device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this,new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },LOCATION_PERMISSION_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         switch (requestCode)
         {
             case LOCATION_PERMISSION_REQUEST:
                 if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                 {
                     if(checkPlayServices()){

                         buildGoogleApiClient();
                         createLocationRequest();

                         displayLocation();
                     }
                 }
                 break;
         }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation =location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                      return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,  this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {



    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
ProgressDialog mDialog =new ProgressDialog(TrackingActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please waiting.......");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> routes =null;
            try {
                jsonObject =new JSONObject(strings[0]);
                DirectionJSONParser parser =new DirectionJSONParser();
            routes =parser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
             mDialog.dismiss();
             ArrayList points =null;
             PolylineOptions lineOptions =null;

            for(int i=0; i<lists.size();i++){
                points=new ArrayList();
                lineOptions =new PolylineOptions();
                List<HashMap<String,String>> path =lists.get(i);
                for (int j=0;j<path.size();j++){
                    HashMap<String,String> point =path.get(j);

                    double lat =Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng position= new LatLng(lat,lng);

                    points.add(position);

                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }
            mMap.addPolyline(lineOptions);
        }
    }
}
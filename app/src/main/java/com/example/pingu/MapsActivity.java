package com.example.pingu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,InternetSpeedTest.AsyncResponse{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    MarkerOptions markerOptions;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    android.location.LocationListener locationListener;
    Button capture;
    private static final int ZOOM_LEVEL = 15;
    private static final int TILT_LEVEL = 0;
    private static final int BEARING_LEVEL = 0;
    private Marker previousMarker = null;
    private Button captureBtn;
    public String username;
    double latitude;
    double longitude;
    double a=12.0192;
    double b=79.0712;
    Marker myMarker;
    String carrierSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        final MaterialSpinner spinner2 = (MaterialSpinner) findViewById(R.id.spinner2);
        final ArrayList<String> myList2=new ArrayList<>();
        myList2.add("JIO");
        myList2.add("AIRTEL");
        myList2.add("VODAFONE");
        myList2.add("BSNL");
        myList2.add("IDEA");
        spinner2.setItems(myList2);
        spinner2.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                carrierSelected=item;
            }
        });
        mapFragment.getMapAsync(this);
        captureBtn=findViewById(R.id.captureArea);



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
               latitude = location.getLatitude();
                longitude = location.getLongitude();
                String locationName = "Random Location";
                String address = "Random Location";
                address = address.replace(",", "\n");
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(getApplicationContext());
                captureBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new InternetSpeedTest(MapsActivity.this).execute("http://www.daycomsolutions.com/Support/BatchImage/HPIM0050w800.JPG");
                        Toast.makeText(MapsActivity.this, "Connection Data Updated", Toast.LENGTH_SHORT).show();
                        captureBtn.setVisibility(View.GONE);
                    }
                });
                try {
                    List<Address> addresses =
                            geocoder.getFromLocation(latitude, longitude, 1);
                    String result = addresses.get(0).getLocality()+":";
                    result += addresses.get(0).getCountryName();
                    final LatLng latLng = new LatLng(latitude, longitude);

                    if (marker != null){
                        marker.remove();
                        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 40.0f));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100.0f));

                        double distance=distance(latLng.latitude,latLng.longitude,a, b);
                        Toast.makeText(MapsActivity.this, String.valueOf(distance*1000+" m"), Toast.LENGTH_SHORT).show();
                        if(distance*1000<=50)
                        {
                            captureBtn.setVisibility(View.VISIBLE);
                        }

                    }
                    else{
                        final String finalResult = result;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(finalResult));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 40.0f));
                            }
                        },2000);




                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng[] position = new LatLng[1];
        db.collection("connections")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                position[0] =new LatLng(document.getDouble("lat"),document.getDouble("lon"));
                                MarkerOptions markerOptions=new MarkerOptions()
                                        .position(position[0])
                                        .title(document.getId())
                                        .snippet(document.getString("carrier"))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                Marker myMarker=mMap.addMarker(markerOptions);
                                CameraPosition camPos = new CameraPosition(position[0], ZOOM_LEVEL, TILT_LEVEL, BEARING_LEVEL);
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos),4000,null);
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                captureBtn.setVisibility(View.GONE);
                a=marker.getPosition().latitude;
                b=marker.getPosition().longitude;
                username=marker.getTitle();
                return false;
            }
        });
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        new LatLng(document.getDouble("lat"), document.getDouble("lon"));
//                        MarkerOptions markerOptions=new MarkerOptions()
//                                .position(position[0])
//                                .title("This is my title")
//                                .snippet("and snippet")
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
//                        Marker myMarker=mMap.addMarker(markerOptions);
//                        CameraPosition camPos = new CameraPosition(position[0][0], ZOOM_LEVEL, TILT_LEVEL, BEARING_LEVEL);
//                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos),4000,null);
//                    } else {
//                        Log.d("ERROR DOC", "No such document");
//                    }
//                } else {
//                    Log.d("EXCEPTION", "get failed with ", task.getException());
//                }
//            }
//        });


//        mMap.addMarker(new MarkerOptions()
//                .position(position)
//                .title("This is my title")
//                .snippet("and snippet")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        CameraPosition camPos = new CameraPosition(position, ZOOM_LEVEL, TILT_LEVEL, BEARING_LEVEL);
//
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos),4000,null);
//
//        position = new LatLng(Math.random()*13, Math.random()*75);
//        mMap.addMarker(new MarkerOptions()
//                .position(position)
//                .title("This is my title")
//                .snippet("and snippet")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        camPos = new CameraPosition(position, ZOOM_LEVEL, TILT_LEVEL, BEARING_LEVEL);
//
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos),4000,null);
//
//        position = new LatLng(Math.random()*13, Math.random()*75);
//        mMap.addMarker(new MarkerOptions()
//                .position(position)
//                .title("This is my title")
//                .snippet("and snippet")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//        camPos = new CameraPosition(position, ZOOM_LEVEL, TILT_LEVEL, BEARING_LEVEL);
//
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos),4000,null);



        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void processFinish(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        Map<String, Object> connection = new HashMap<>();
        connection.put("carrier", carrierSelected);
        connection.put("speed", Double.valueOf(output));
        connection.put("lat",latitude);
        connection.put("lon",longitude);
        connection.put("flag",1);
//        mMap.clear();
        db.collection("connections").document(username)
                .set(connection)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Data Loaded", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed", "Error writing document", e);
                    }
                });

    }
}


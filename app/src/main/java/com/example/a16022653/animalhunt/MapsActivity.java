package com.example.a16022653.animalhunt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private GoogleMap mMap;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double latti;
    double longi;
//    AlertDialog builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap = googleMap;
        getLocation();
        LatLng currentloc = new LatLng(latti, longi);
        MarkerOptions markerOptions = new MarkerOptions();



        final CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);
        InfoWindowData info = new InfoWindowData();
        info.setImage("flag");
        mMap.addMarker(new MarkerOptions().position(currentloc).title("Marker in current location")).setTag(info);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentloc, 17.0f));


     mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
         @Override
         public void onInfoWindowClick(Marker marker) {
             AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
             LayoutInflater inflater = (MapsActivity.this).getLayoutInflater();
             builder.setView(inflater.inflate(R.layout.custom_dialog_layout, null));


//             builder.setTitle("Bio for picture");
             builder.setMessage("Enter bio: ");
             builder.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();

                 }

             });
builder.show();
         }
     });


    }


    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();


            } else {

            }
        }

    }




}





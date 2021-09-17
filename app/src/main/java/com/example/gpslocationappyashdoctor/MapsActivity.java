package com.example.gpslocationappyashdoctor;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap mMap;
    LocationManager manage;
    Location loc;
    double lati = 0, longi = 0;
    boolean isGps, isNet;
    Address address;
    Geocoder geo;
    String area;
    LatLng current, prev;
    Marker mark = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manage = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGps = manage.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNet = manage.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (isNet || isGps) {
            if (isNet) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manage.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, this);

                if (manage != null) {
                    loc = manage.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (loc != null) {
                        lati = loc.getLatitude();
                        longi = loc.getLongitude();
                    }
                }

            }

            if (isGps) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manage.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

                if (manage != null) {
                    loc = manage.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (loc != null) {
                        lati = loc.getLatitude();
                        longi = loc.getLongitude();
                    }
                }
            }

            Toast.makeText(getApplicationContext(), "LATI=" + lati + "LONGI = " + longi, Toast.LENGTH_LONG).show();

            try {

                geo = new Geocoder(this, Locale.getDefault());
                List<Address> list = geo.getFromLocation(lati, longi, 1);

                address = list.get(0);

                area = address.getAddressLine(0);
                area = area + "," + address.getLocality();
                area = area + "," + address.getAdminArea();
                area = area + "," + address.getCountryName();
                area = area + "," + address.getPostalCode();

                Toast.makeText(getApplicationContext(), area, Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }

        }


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

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        current = new LatLng(lati, longi);
        mMap.addMarker(new MarkerOptions().position(current).title("Marker in Vadodara"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (mark != null) {
            mark.setVisible(false);
        }

        lati = location.getLatitude();
        longi = location.getLongitude();

        prev = current;
        current = new LatLng(lati, longi);

        mark = mMap.addMarker(new MarkerOptions().position(current).title("CURRENTLY HERE"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        Polyline line = mMap.addPolyline(new PolylineOptions().add(prev, current).width(5).color(Color.RED));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
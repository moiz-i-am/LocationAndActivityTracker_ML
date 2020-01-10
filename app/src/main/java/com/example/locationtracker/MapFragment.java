package com.example.locationtracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment {

//    Button btnShowLocation;
//    private static final int REQUEST_CODE_PERMISSION = 2;
//    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
//
//    GPSTracker gps;
//    TextView location;


    TextView address;
    TextView city;
    TextView country;

    LocationManager locationManager;
    LocationListener locationListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_map, container,false);




//        try{
//            if(ActivityCompat.checkSelfPermission(getActivity(), mPermission)!= PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},REQUEST_CODE_PERMISSION);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        btnShowLocation = (Button) RootView.findViewById(R.id.buttonShow);
//       // btnShowLocation = getView().findViewById(R.id.buttonShow);
//        btnShowLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gps = new GPSTracker(getActivity());
//                location = (TextView) getView().findViewById(R.id.locationText);
//                if(gps.canGetLocation()){
//                    double latitude = gps.getLatitude();
//                    double longitude = gps.getLongitude();
//                    location.setText(latitude+""+longitude);
//                }else{
//                    gps.showSettingsAlert();
//                }
//            }
//        });



        address = (TextView) RootView.findViewById(R.id.locationText);
        city = (TextView) RootView.findViewById(R.id.cityText);
        country = (TextView) RootView.findViewById(R.id.countryText);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS_enable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPS_enable){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    try{

                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);

                        address.setText(addressList.get(0).getAddressLine(0));
                        city.setText(addressList.get(0).getAdminArea());
                        country.setText(addressList.get(0).getCountryName());

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
        }

        if (ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);

        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        }


        return RootView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
                address.setText("Getting Location");
                city.setText("Getting Location");
                country.setText("Getting Location");
            }
        }else{
            address.setText("Not Granted");
            city.setText("Not Granted");
            country.setText("Not Granted");
        }

    }

}

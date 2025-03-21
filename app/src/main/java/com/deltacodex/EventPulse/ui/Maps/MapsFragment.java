package com.deltacodex.EventPulse.ui.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.Cinema;
import com.deltacodex.EventPulse.model.GamingHub;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int GPS_REQUEST_CODE = 101;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Load map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String style =
                "[ " +
                        "{ " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#212121\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.icon\", " +
                        "\"stylers\": [ { \"visibility\": \"off\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#757575\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.text.stroke\", " +
                        "\"stylers\": [ { \"color\": \"#212121\" } ] " +
                        "}, { " +
                        "\"featureType\": \"administrative.locality\", " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"poi\", " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"poi.park\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#2e3b4e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#2c2c2c\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.arterial\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.highway\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.local\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"transit.station\", " +
                        "\"elementType\": \"labels.icon\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"water\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#000000\" } ] " +
                        "} ]";
        try {
            boolean success = mMap.setMapStyle(new MapStyleOptions(style));
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }


        requestLocationPermission();  // Request permission and get location
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkGPSAndFetchLocation();  // If permission is granted, check GPS and fetch location
        }
    }

    private void checkGPSAndFetchLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(), "GPS is disabled. Turning it on...", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(2000);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) return;
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        updateMapWithLocation(latitude, longitude);
                        addManualMarkers(); // Add manually defined markers for cinemas & gaming hubs
                        fusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateMapWithLocation(double latitude, double longitude) {
        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Me"));
    }

    private void addManualMarkers() {
        List<Cinema> cinemas = new ArrayList<>();

        cinemas.add(new Cinema("PVR Cinema", new LatLng(6.92744130336914, 79.84545672486995)));
        cinemas.add(new Cinema("Liberty By Scope Cinemas - Colpetty", new LatLng(6.912428861013223, 79.85102730952714)));
        cinemas.add(new Cinema("Savoy 3D Cinema", new LatLng(6.87941677343623, 79.85959448254113)));
        cinemas.add(new Cinema("Majestic complex Theater", new LatLng(6.894425141011146, 79.85474839418433)));
        cinemas.add(new Cinema("Savoy", new LatLng(6.909077162602351, 79.89631792486976)));
        cinemas.add(new Cinema("Regal Cinema - Dematagoda", new LatLng(6.928939404624853, 79.87824072671975)));
        cinemas.add(new Cinema("Ram Theater", new LatLng(6.990209876642366, 79.89246878254154)));
        cinemas.add(new Cinema("Scope Cinemas Multiplex - Havelock City Mall", new LatLng(6.882620017492015, 79.86756916719834)));
        cinemas.add(new Cinema("City Cinema", new LatLng(6.827721389929901, 79.86917920952676)));
        cinemas.add(new Cinema("Aqua Lite 3D Cinema", new LatLng(7.206065474725135, 79.84256099788516)));
        cinemas.add(new Cinema("Roxy Theater", new LatLng(6.8657847944840755, 79.86301004021242)));
        cinemas.add(new Cinema("Ruoo Theater", new LatLng(7.165998224075155, 79.88542690952812)));
        cinemas.add(new Cinema("Liberty", new LatLng(6.977396807744131, 79.92889151360157)));
        cinemas.add(new Cinema("Cinemax 3D Theater", new LatLng(7.075665378281371, 79.8923607365136)));
        cinemas.add(new Cinema("Flick Theater", new LatLng(6.844319488389424, 79.92537829603391)));
        cinemas.add(new Cinema("Scope Cinemas Multiplex - CCC", new LatLng(6.918104459776658, 79.85566685370556)));
        cinemas.add(new Cinema("Cine City", new LatLng(6.929915405019106, 79.86341991137698)));
        cinemas.add(new Cinema("Milano Cineplex - Kegalle", new LatLng(7.253297454238375, 80.34542950952854)));
        cinemas.add(new Cinema("Eros Cinema", new LatLng(6.874284923915002, 79.87063538069125)));
        cinemas.add(new Cinema("Amity", new LatLng(6.849157186332827, 79.92375880952684)));
        cinemas.add(new Cinema("Tharangani Theater", new LatLng(6.902316106063892, 79.86566988439095)));
        cinemas.add(new Cinema("Open Theater", new LatLng(7.491940949594806, 80.36742937531626)));
        cinemas.add(new Cinema("Jubile Cinema", new LatLng(6.91943140838867, 79.86059278254119)));
        cinemas.add(new Cinema("Nanda Theater", new LatLng(7.74962129331278, 80.11548814021631)));
        cinemas.add(new Cinema("Wonder: The mini movie theater", new LatLng(6.908975911716911, 79.89626585185565)));
        cinemas.add(new Cinema("Sandalanka Cinema", new LatLng(7.319416366462947, 79.96681012302176)));
        cinemas.add(new Cinema("Madampe Theater", new LatLng(7.495882945515721, 79.84078602134733)));
        cinemas.add(new Cinema("Regal Cinema Bandarawela", new LatLng(6.831228141273169, 80.9883734537052)));
        cinemas.add(new Cinema("Regal Cinema Nuwareliya", new LatLng(6.974940580675614, 80.76668711137717)));
        cinemas.add(new Cinema("KCC Multiplex", new LatLng(7.292765359818221, 80.63743966720007)));
        cinemas.add(new Cinema("Sinexpo 3D Cinema", new LatLng(7.481497705156068, 80.36096549418683)));
        cinemas.add(new Cinema("SkyLite Cinema", new LatLng(5.9437295037720626, 80.54911350952386)));
        cinemas.add(new Cinema("Lakmali Theater", new LatLng(7.012022407827478, 80.85941320952752)));
        cinemas.add(new Cinema("Regal Cinema Diyathalawa", new LatLng(6.806588908896848, 80.95878395952185)));
        cinemas.add(new Cinema("Rex Theater", new LatLng(6.987767330203454, 81.06078195370581)));
        cinemas.add(new Cinema("VoxX Lite 3D Cinema", new LatLng(6.956221931388245, 80.20627917495878)));
        cinemas.add(new Cinema("Willmax Cinema", new LatLng(8.323855544780983, 80.40299912487662)));
        cinemas.add(new Cinema("Queen's Cinema", new LatLng(6.034418781732501, 80.2166553825382)));


        for (Cinema cinema : cinemas) {
            mMap.addMarker(new MarkerOptions()
                    .position(cinema.getLatLng())
                    .title(cinema.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.popcorn_icon))); // Popcorn icon
        }
        List<GamingHub> gaming = new ArrayList<>();
        gaming.add(new GamingHub("Havelock Gaming Cafe", new LatLng(6.882981983244217, 79.86921608444115)));
        gaming.add(new GamingHub("Ultra Gaming Cafe", new LatLng(6.88299529763519, 79.86921340223222)));
        gaming.add(new GamingHub("Play Arena Games", new LatLng(12.911666884241663, 77.67634201142194)));
        gaming.add(new GamingHub("RIP Gaming Saloon", new LatLng(6.906297912925709, 79.85151809603414)));
        gaming.add(new GamingHub("AVR Game Zone - Havelock City Mall", new LatLng(6.884536994345424, 79.86799734233098)));
        gaming.add(new GamingHub("AVR - One Galle Face Mall", new LatLng(6.929155389091675, 79.84565996011831)));
        gaming.add(new GamingHub("Sri Lanka Karting Circuit, Bandaragama", new LatLng(6.737272633527565, 79.98536935370483)));
        gaming.add(new GamingHub("Fun Factory Mount Lavinia", new LatLng(6.832724670926525, 79.8691250974946)));
        gaming.add(new GamingHub("Unique Gaming Hub", new LatLng(6.927203804521585, 79.89864475370557)));
        gaming.add(new GamingHub("GameEdge Sports", new LatLng(6.866375615779377, 79.88489755185556)));
        gaming.add(new GamingHub("Starnet Game Zone", new LatLng(6.849595933881496, 79.92417952301977)));
        gaming.add(new GamingHub("PC Game House", new LatLng(6.793452177497224, 79.89329852301961)));
        gaming.add(new GamingHub("GameOn Majestic City", new LatLng(6.8941575646008, 79.85477601137683)));
        gaming.add(new GamingHub("Cloud gaming lounge", new LatLng(6.843690555439302, 79.95858489603386)));
        gaming.add(new GamingHub("Phoenix Gaming Net Cafe", new LatLng(6.840702984999716, 79.96496212301975)));
        gaming.add(new GamingHub("Gaming Monster", new LatLng(6.92990785271366, 79.92760156719852)));
        gaming.add(new GamingHub("NextGen Gaming Cafe", new LatLng(7.092885334117434, 79.99858488254189)));
        gaming.add(new GamingHub("Blastation Gaming Centre Rajagiriya", new LatLng(6.909042009823912, 79.8962696806914)));
        gaming.add(new GamingHub("GG Gaming Cafe", new LatLng(6.861971448782156, 79.86423356904811)));
        gaming.add(new GamingHub("Icy Lounge Bandarawela", new LatLng(6.830128616013372, 80.99665953836248)));
        gaming.add(new GamingHub("ShalomTek - Bandarawela", new LatLng(6.829985041772679, 80.98982568069108)));
        gaming.add(new GamingHub("Qball Cafe Badulla", new LatLng(6.985041629556596, 81.06151885185598)));

        for (GamingHub games : gaming) {
            mMap.addMarker(new MarkerOptions()
                    .position(games.getLatLng())
                    .title(games.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.joystick_icon))); // Popcorn icon
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkGPSAndFetchLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

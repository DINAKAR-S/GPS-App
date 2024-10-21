package com.example.gps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    TextView t1, distanceView, nameView, registerNumberView;
    Button startButton, stopButton;
    LocationManager locationManager;
    LocationListener locationListener;

    Location startLocation, stopLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1 = findViewById(R.id.t1);
        distanceView = findViewById(R.id.distanceView);
        nameView = findViewById(R.id.nameView);
        registerNumberView = findViewById(R.id.registerNumberView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // Set name and register number
        nameView.setText("Name: Dinakar S");
        registerNumberView.setText("Register Number: 21013");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                t1.setText("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(MainActivity.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        startButton.setOnClickListener(v -> startGPS());
        stopButton.setOnClickListener(v -> stopGPS());
    }

    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);

        startLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (startLocation != null) {
            Toast.makeText(this, "Start Location Captured", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopGPS() {
        stopLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (stopLocation != null) {
            Toast.makeText(this, "Stop Location Captured", Toast.LENGTH_SHORT).show();
            double distance = calculateDistance(startLocation, stopLocation);
            distanceView.setText("Distance: " + distance + " meters");
        }
        locationManager.removeUpdates(locationListener);
    }

    private double calculateDistance(Location start, Location stop) {
        if (start != null && stop != null) {
            return start.distanceTo(stop);
        }
        return 0.0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startGPS();
        }
    }
}

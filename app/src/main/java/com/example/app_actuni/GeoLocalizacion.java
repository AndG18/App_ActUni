package com.example.app_actuni;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeoLocalizacion extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private TextView tvlatitud, tvlongitud;
    private GoogleMap mMap;
    private int CODIGO_PERMISO_UBICACION = 100;
    private boolean isPermisos = false;
    private FusedLocationProviderClient fusedLocatClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_geo_localizacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        solicitudPermisos();

        tvlatitud = findViewById(R.id.tvLatitud);
        tvlongitud = findViewById(R.id.tvLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void solicitudPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermisos = true;
            iniciarLocalizacion();

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            mostrarDialogoExpl();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_PERMISO_UBICACION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] granResults) {
        super.onRequestPermissionsResult(requestCode, permissions, granResults);
        if (requestCode == CODIGO_PERMISO_UBICACION) {
            if (granResults.length > 0 && granResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermisos = true;
                iniciarLocalizacion();
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarDialogoExpl() {
        AlertDialog.Builder bild = new AlertDialog.Builder(this);
        bild.setTitle("Permisos Requeridos");
        bild.setMessage("Esta aplicacion requiere acceder a tu ubicación para mostrar tus coordenadas \n" +
                        "¿Deseas permitir el acceso?");
        bild.setPositiveButton("Aceptar", ((dialog, which) -> {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_PERMISO_UBICACION);
        }));
        bild.setNegativeButton("Rechazar", (dialog, which) -> {
            dialog.dismiss();
            mostrarMssPD();
        });
        bild.show();
    }

    private void mostrarMssPD() {
        Toast.makeText(this, "La aplicación necesita permisos de ubicación para funcionar", Toast.LENGTH_LONG).show();
    }

    private void iniciarLocalizacion() {
        fusedLocatClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            LocationRequest.Builder locationRequest = new LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 30000);
                locationRequest.setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL);
                locationRequest.setWaitForAccurateLocation(true);
                locationRequest.build();

                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        actualizarUbi(locationResult.getLastLocation());
                    }
                };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocatClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    actualizarUbi(location);
                }
            }).addOnFailureListener(e -> {
                Log.e("Location", "Error de ubicación", e);
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show();
            });

            fusedLocatClient.requestLocationUpdates(
                    locationRequest.build(),
                    locationCallback,
                    Looper.getMainLooper()
            );

            }catch (SecurityException e) {
                Log.e("Location","Error de permisos: " + e);
                Toast.makeText(this, "Error: Permisos no disponibles", Toast.LENGTH_LONG).show();
            }

        }

    private void actualizarUbi(Location location) {
        tvlatitud.setText(String.format("%.6f", location.getLatitude()));
        tvlongitud.setText(String.format("%.6f", location.getLongitude()));


        Log.d("Location", "Lat: " + location.getLatitude() + "Lon: " + location.getLongitude());
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng col = new LatLng(4.4880655,-73.8477277);
        mMap.addMarker(new MarkerOptions().position(col).title("Colombia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(col));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        tvlatitud.setText(String.format("%.6f", latLng.latitude));
        tvlongitud.setText(String.format("%.6f", latLng.longitude));
        mMap.clear();
        LatLng col = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(col).title("Colombia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(col));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        tvlatitud.setText(String.format("%.6f", latLng.latitude));
        tvlongitud.setText(String.format("%.6f", latLng.longitude));
        mMap.clear();
        LatLng col = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(col).title("Colombia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(col));
    }
}
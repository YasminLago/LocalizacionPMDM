package com.example.yasmin.localizacionpmdm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirstMapFragment mFirstMapFragment;
    private static final int PERMISO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Creacion fragmento de forma dinamica
        mFirstMapFragment = FirstMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mFirstMapFragment)
                .commit();
        mFirstMapFragment.getMapAsync(this);//Asocia la actividad como escucha
    }

    /**
     * Este método es llamado cuando el mapa está listo para ser utilizado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        solicitarPermiso();
        mMap.getUiSettings().setZoomControlsEnabled(true); //Controles integrados de zoom
        // Marcadores
        mMap.addMarker(new MarkerOptions().position(new LatLng(42.1416, -8.4324)));
    }

    /***********************************PERMISOS DE LOCALIZACION***********************************/
    /**
     * Comprueba los permisos y los concede
     * Muestra un mensaje en tiempo de ejecución explicando la necesidad de dichos permisos
     */
    public void solicitarPermiso(){
        int checkPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkPerm == PackageManager.PERMISSION_GRANTED) { //Concede el permiso para la localización
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Aquí se muestra el diálogo explicativo de porque se necesitan los permisos
            } else {
                // Se solicitan los permisos
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISO);
            }
        }
    }

    /**
     * Devuelve la llamada para el resultado de solicitar permisos.
     * Se invoca a través del método requestPermission()
     * @param requestCode Código de solicitud devuelto por requestPermission()
     * @param permissions Permisos solicitados. Nunca puede ser nulo
     * @param grantResults Resultado de la conexión para los permisos
     *                     correspondientes.(PERMISSION_GRANTED o PERMISSION_DENIED)Nunca nulo
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISO) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                /*
                Al habilitar la capa MyLocation aparece en la parte superior derecha un botón de localización.
                Al pulsarlo, la camara centra el mapa en la ubicación actual.
                La ubicación se muestra a través de un punto azul.
                 */
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }

    /*******************************FIN PERMISOS LOCALIZACION**************************************/
}

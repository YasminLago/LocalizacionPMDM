package com.example.yasmin.localizacionpmdm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirstMapFragment mFirstMapFragment;
    private static final int PERMISO = 1;

    //Coordenadas del usuario //Distancia entre el usuario y la marca
    double latitud, longitud, distanciaEntrePuntos;

    //Coordenadas del sitio donde está puesta la marca
    double latMark = 42.2368914;
    double lngMark = -8.712825199999997;

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
        obtenerUbicacion();
    }

    /**
     * Este método es llamado cuando el mapa está listo para ser utilizado
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        solicitarPermiso();
        mMap.getUiSettings().setZoomControlsEnabled(true); //Controles integrados de zoom
    }

    /***********************OBTENCIÓN DE UBICACIÓN A TRAVÉS DE LOCATION MANAGER********************/
    /**
     * Obtiene la posición actual del usuario
     */
    public void obtenerUbicacion(){
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locLis);
    }

    LocationListener locLis= new LocationListener() {
        /**
         * Se ejecuta cada vez que el GPS recibe nuevas coordenadas
         * debido a la detección de un cambio de ubicación
         * @param loc
         */
        @Override
        public void onLocationChanged(Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            Log.e("GIIIIIII", "WIIIIII" + loc.getLatitude() + " " + loc.getLongitude());
            comparaDistancia(loc);
            calcularDistancia(loc);
        }

        /**
         * Se ejecuta cada vez que se detecta un cambio en el
         * status del proveedor de localización (GPS)
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

        /**
         * Se ejecuta cuando el GPS está activado
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS activado",
                    Toast.LENGTH_SHORT).show();
        }

        /**
         * Se ejecuta cuando el GPS está desactivado
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS desactivado",
                    Toast.LENGTH_SHORT).show();
        }
    };
    /********************************FIN OBTENCIÓN DE UBICACIÓN************************************/

    /**
     * Calcula la distancia entre la posición actual del usuario
     * y el sitio al que se tiene que llegar
     * @param loc
     * @return Retorna la distancia entre los dos puntos.
     */
   public double calcularDistancia(Location loc) {
       latitud = loc.getLatitude();
       longitud = loc.getLongitude();
       Log.e("GIIIIIII", "calcularDistancia" + latitud + longitud);
       Location myLocation = new Location("Mi localización");
           myLocation.setLatitude(latitud);
           myLocation.setLongitude(longitud);
       Location markerLocation = new Location("Localización marca");
       //markerLocation.setLatitude(42.2463092);
       //markerLocation.setLongitude(-8.701472200000012);
           markerLocation.setLatitude(latMark);
           markerLocation.setLongitude(lngMark);
       distanciaEntrePuntos = myLocation.distanceTo(markerLocation);

       return distanciaEntrePuntos;
    }

    /**
     * Compara la distancia entre 20 (en metros). Si la distancia es menor o igual
     * a 20 se mostrara la marca de dicho lugar.
     * @param loc
     */
   public void comparaDistancia(Location loc){
       double distancia = calcularDistancia(loc);
       Toast.makeText(getApplicationContext(), String.valueOf(distancia),
               Toast.LENGTH_SHORT).show();
       Log.e("GIIIIIII","distancia "+String.valueOf(distancia));
       if(distancia <= 20.00){
            crearMarca();
       }
   }

    /**
     * Crea la marca del lugar al que se tiene que llegar
     */
   public void crearMarca(){
       mMap.addMarker(new MarkerOptions()
               .position(new LatLng(latMark, lngMark))
               .title("Aquí estoy!!"));
   }

    /***********************************PERMISOS DE LOCALIZACIÓN***********************************/
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
                Al habilitar la capa MyLocation, aparece en la parte superior derecha un botón de localización.
                Al pulsarlo, la cámara centra el mapa en la ubicación actual.
                La ubicación se muestra a través de un punto azul.
                 */
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }
        }
    }
    /*******************************FIN PERMISOS LOCALIZACIÓN**************************************/
}

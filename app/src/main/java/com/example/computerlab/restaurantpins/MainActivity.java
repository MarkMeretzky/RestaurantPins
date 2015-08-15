package com.example.computerlab.restaurantpins;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    Helper helper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (connectionResult != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult, this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();   //Destroy this activity.
                        }
                    }
            );
            dialog.show();
        }

        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //eventually calls onMapReady

        helper = new Helper(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d("myTag", "onMapReady");
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        //NYU SCPS, 7 East 12th Street, New York, NY  10003.
        //Longitude west of the prime meridian (Greenwich) is negative.
        LatLng latLng = new LatLng(40.734457, -73.993886);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18f);
        googleMap.moveCamera(cameraUpdate);

        MarkerOptions markerOptions = new MarkerOptions(); //Create an empty MarkerOptions.
        markerOptions.position(latLng);                    //Put some options into it.
        markerOptions.title("7 E 12");
        markerOptions.snippet("NYU SCPS");
        //googleMap.addMarker(markerOptions);

        cursor = helper.getCursor();

        final int dbaIndex          = cursor.getColumnIndex("dba");
        final int violation_descriptionIndex = cursor.getColumnIndex("violation_description");
        int latitudeIndex         = cursor.getColumnIndex("latitude");
        int longitudeIndex        = cursor.getColumnIndex("longitude");

        while (cursor.moveToNext()) {
            LatLng ll = new LatLng(cursor.getDouble(latitudeIndex), cursor.getDouble(longitudeIndex));
            MarkerOptions mo = new MarkerOptions(); //Create an empty MarkerOptions.
            markerOptions.position(ll);                    //Put some options into it.
            markerOptions.title(cursor.getString(dbaIndex));
            markerOptions.snippet(cursor.getString(violation_descriptionIndex));
            googleMap.addMarker(markerOptions);

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                //Put the vertical LinearLayout into the default InfoWindow frame.
                @Override
                public View getInfoWindow(Marker marker) {
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.layout, null);
                    SQLiteDatabase db = helper.getReadableDatabase();
                    Cursor cursor = db.rawQuery(
                            "SELECT * FROM restaurant_table where latitude = ? and longitude = ?;",
                            new String[] {
                                    String.valueOf(marker.getPosition().latitude),
                                    String.valueOf(marker.getPosition().longitude),
                            });
                    cursor.moveToFirst();
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    textView.setText(cursor.getString(dbaIndex));

                    textView = (TextView) view.findViewById(R.id.body);
                    LatLng latLng = marker.getPosition();

                    textView.setText(cursor.getString(violation_descriptionIndex));
                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
        }

        cursor.close();

/*
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            //Put the vertical LinearLayout into the default InfoWindow frame.
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.info_window, null);

                TextView textView = (TextView)view.findViewById(R.id.title);
                textView.setText("7 East 12th Street");

                textView = (TextView)view.findViewById(R.id.body);
                LatLng latLng = marker.getPosition();

                textView.setText(
                          "Latitude "  + Math.abs(latLng.latitude)  + "\u00B0" + " " + (latLng.latitude  >= 0 ? "N" : "S") + "\n"
                        + "Longitude " + Math.abs(latLng.longitude) + "\u00B0" + " " + (latLng.longitude >= 0 ? "E" : "W")
                );

                return view;
            }
        });
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        mapView.onSaveInstanceState(bundle);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

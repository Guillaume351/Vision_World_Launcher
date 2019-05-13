package world.vision.launcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import world.vision.launcher.Adapter.disabledAdapter;

public class LauncherSettings extends AppCompatActivity {
    public String[] settingsCategories = {"Wifi\net Réseau", "Langues", "Actualiser la ville météo"};
    public GridView grdView;
    public ArrayAdapter<String> adapter;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.settings_layout);

        addSettingsCategories();


    }

    public void addSettingsCategories() {
        try {

            grdView = (GridView) findViewById(R.id.settings_gridview);
            grdView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.d("FOCUS CHANGETOGRID", "FOCUS CALLED3333");
                    grdView.getChildAt(0).requestFocus();

                }
            });
            if (adapter == null) {
                adapter = new disabledAdapter(this, R.layout.grille_categories_icone, Arrays.asList(settingsCategories)) {

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                    R.layout.grille_categories_icone, parent, false
                            );
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imgIcon);
                            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
                            viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);


                            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Do something
                                    if (settingsCategories[position].equals("Wifi\net Réseau")) {

                                        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                                    }

                                    if (settingsCategories[position].equals("Ethernet")) {//TODO : Trouver réglage pour ethernet

                                        startActivityForResult(new Intent(Settings.ACTION_WIFI_IP_SETTINGS), 0);
                                    }

                                    if (settingsCategories[position].equals("Langues")) {//TODO : Trouver réglage pour ethernet

                                        startActivityForResult(new Intent(Settings.ACTION_LOCALE_SETTINGS), 0);
                                    }

                                    if (settingsCategories[position].equals("Actualiser la ville météo")) {
                                        refreshWeatherCity(v);
                                    }

                                }
                            });

                            if (position == 0) {
                                viewHolder.icon.requestFocus();
                            }
                            convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        v.findViewById(R.id.imgIcon).requestFocus();

                                    }


                                }
                            });
                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        viewHolder.label.setText(settingsCategories[position]);
                        return convertView;

                    }

                    final class ViewHolderItem {
                        ImageView icon;
                        TextView label;
                        TextView name;
                    }
                };
            }


            grdView.setAdapter(adapter);
            // addGridListeners();
        } catch (Exception ex) {

        }


    }

    public void refreshWeatherCity(View v) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
        //  requestPermissions(permissions,1440);

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
            if (granted) {
                Log.d("Location granted", "refreshWeatherCity: ");
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
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object


                                    SharedPreferences preferences = getSharedPreferences("meteoCoordinates", MODE_PRIVATE);
                                    SharedPreferences.Editor edit = preferences.edit();

                                    edit.putBoolean("isFirstRun", false);
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();


                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        if (addresses.size() > 0) {
                                            String cityName = addresses.get(0).getAddressLine(0);
                                            String stateName = addresses.get(0).getAddressLine(1);
                                            String countryName = addresses.get(0).getAddressLine(2);
                                            edit.putString("city", cityName);
                                            edit.putBoolean("customCity", true);
                                            edit.putString("country", countryName);
                                            edit.putFloat("lat", (float) latitude);
                                            edit.putFloat("long", (float) longitude);
                                            Log.d(cityName, "onSuccess: (" + cityName);
                                            edit.apply();
                                            setResult(1, getIntent());
                                            finish();
                                        } else {
                                            // do your stuff
                                            Log.d("Error", "Address empty ");
                                        }


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Log.d("Location error", "Location null");
                                }
                            }
                        });

            } else {
                return;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            /*
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();


            SharedPreferences preferences = getSharedPreferences("meteoCoordinates", MODE_PRIVATE);
            SharedPreferences.Editor edit= preferences.edit();

            edit.putBoolean("isFirstRun", false);


            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(preferences.getFloat("long",0), preferences.getFloat("lat",0), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);
            edit.putString("city",cityName);
            edit.apply();
            */
        }


    }


}

package world.vision.launcher;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import world.vision.launcher.Adapter.disabledAdapter;

public class MainActivity extends AppCompatActivity {

    public static ArrayAdapter<String> adapter;
    public static int CATEGORY_VIDEO = 1, CATEGORY_RADIO = 2, CATEGORY_MUSIC = 3, CATEGORY_TV = 0, CATEGORY_PHOTO = 4, CATEGORY_AUTRES = 5;
    public static int STATE_HOME = 0, STATE_CATEGORY = 1; // Stocke l'etat dans lequel on se trouve (dans un menu, ou à l'accueil)
    public String[] categories = {"TV", "Vidéos", "Radios", "Musiques", "Photos", "Autres apps"};
    public int state = 0;
    TextView txtTime, txtDate;
    Calendar c;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleTimeFormat;
    String OPEN_WEATHER_MAP_API = "33db9672ac6de85e2dd02f02bc9445d4";
    GridView grdView;

    TextView detailsField, tempField, weatherIcon;//detailsField contient le nom de la ville

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);//Fix le support de Android 6.0


        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);
        findViewById(R.id.txtTime2).setVisibility(View.GONE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        // txtTime.setText(currentDateTimeString);
                        Date date = new Date();
                        txtTime.setText(android.text.format.DateFormat.format("HH:mm", date));
                        //android.text.format.DateFormat df = new android.text.format.DateFormat();//TODO: use to format hour & time
                        //android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date());
                        int dayOfWeek = date.getDay();

                        int month = date.getMonth();
                        String weekday = new DateFormatSymbols().getWeekdays()[dayOfWeek + 1];
                        String monthStr = new DateFormatSymbols().getMonths()[month];
                        txtDate.setText(weekday.substring(0, 1).toUpperCase() + weekday.substring(1) + android.text.format.DateFormat.format(" dd ", date) + monthStr + " " + android.text.format.DateFormat.format("yyyy", date));
                    }

                });

            }
        }, 0, 1000);

        detailsField = findViewById(R.id.txtMeteoDetails);
        weatherIcon = findViewById(R.id.txtMeteoLogo);
        tempField = findViewById(R.id.txtMeteoTemp);
        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");
        weatherIcon.setTypeface(weatherFont);

        //taskLoadUp("Toulouse, FR");
        Timer timerMeteo = new Timer();
        timerMeteo.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        taskLoadUp("Toulouse, FR");

                    }

                });

            }
        }, 0, 1000 * 60 * 60);//une fois par heure


        loadMainMenu();
    }

    public void addGridListeners() {
        try {
            grdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*
                    if(state == STATE_HOME){
                        if(i == CATEGORY_NAVIGATEUR){
                            startActivity(getPackageManager().getLaunchIntentForPackage("com.android.chrome"));
                            return;
                        }
                        Intent intent = new Intent(view.getContext(), CategoryViewHandler.class);
                        intent.putExtra("cat",Integer.toString(i));//Stocke l'identifiant de la categorie que l'on veut ouvrier
                        intent.putExtra("weatherIcon",weatherIcon.getText().toString());//On transfere les donnees meteo à la nouvelle Activity
                        intent.putExtra("cityName",detailsField.getText().toString());
                        intent.putExtra("weatherTemperature",tempField.getText().toString());
                        startActivity(intent);
                        Log.d("Main listener","Appui detecte");

                    }*/

                    Log.d("FOCUS CHANGETOGRID", "FOCUS.");
                    grdView.getChildAt(0).requestFocus();

                }
            });
        } catch (Exception ex) {
            Log.d("ERREUR", "Impossible de gerer");
        }

    }

    private void loadMainMenu() {
        try {

            grdView = (GridView) findViewById(R.id.categories_gridview);
            grdView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.d("FOCUS CHANGETOGRID", "FOCUS CALLED3333");
                    grdView.getChildAt(0).requestFocus();

                }
            });

            if (adapter == null) {
                adapter = new disabledAdapter(this, R.layout.grille_categories_icone, Arrays.asList(categories)) {

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
                                    if (position == 10)//TODO: à retirer
                                    {
                                        startActivity(getPackageManager().getLaunchIntentForPackage("com.android.chrome"));
                                    } else {
                                        Intent intent = new Intent(v.getContext(), CategoryViewHandler.class);
                                        intent.putExtra("cat", Integer.toString(position));
                                        intent.putExtra("weatherIcon", weatherIcon.getText().toString());//On transfere les donnees meteo à la nouvelle Activity
                                        intent.putExtra("cityName", detailsField.getText().toString());
                                        intent.putExtra("weatherTemperature", tempField.getText().toString());
                                        startActivity(intent);
                                        Log.d("Main listener", "Appui detecte");
                                    }
                                    return;

                                }
                            });

                            viewHolder.icon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    // zoomImageFromThumb(v, R.drawable.rounded_square_512)
                                    if (hasFocus) {
                                        v.setScaleX(1.3f);
                                        v.setScaleY(1.3f);
                                    } else {
                                        v.setScaleX(1.f);
                                        v.setScaleY(1.f);
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
                        /*
                        viewHolder.icon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {

                                Log.d("FOCUS CHANGE", "FOCUS CALLED222222");

                            }
                        });*/

//                      AppInfo appInfo = apps.get(position);
                        viewHolder.label.setText(categories[position]);
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
            addGridListeners();
        } catch (Exception ex) {

        }

    }

    public void openSettings(View v) {
        Intent intent = new Intent(this, LauncherSettings.class);
        startActivity(intent);
    }

    public void taskLoadUp(String query) {
        if (MeteoHandler.isNetworkAvailable(getApplicationContext())) {
            DownloadWeather task = new DownloadWeather(); //TODO: implement DownloadWeather (cf site)
            task.execute(query);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }


    class DownloadWeather extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String xml = MeteoHandler.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API);
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            try {
                JSONObject json = new JSONObject(xml);
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance();

                    //  cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(json.getString("name").toUpperCase(Locale.FRANCE));
                    // currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°");
                    //humidity_field.setText("Humidity: " + main.getString("humidity") + "%");
                    //pressure_field.setText("Pressure: " + main.getString("pressure") + " hPa");
                    //updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                    tempField.setText(String.format("%.1f", main.getDouble("temp")) + "°");
                    weatherIcon.setText(Html.fromHtml(MeteoHandler.setWeatherIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000)));


                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
            }

        }
    }


}

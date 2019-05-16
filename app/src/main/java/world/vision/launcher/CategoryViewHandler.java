package world.vision.launcher;

import android.animation.Animator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import world.vision.launcher.Adapter.AppsInfoArrayAdapter;
import world.vision.launcher.Objects.AppInfo;

import static android.view.View.GONE;


public class CategoryViewHandler extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {

    public static ArrayAdapter<AppInfo> adapter;
    public static List<AppInfo> apps;
    public String[] tvApps = {"tv.molotov.app", "com.sfr.android.sfrsport", "com.canal.android.canal", "com.nousguide.android.rbtv", "ptv.bein.ui", "com.eurosport.player", "com.deltatre.atp.tennis.android", "com.uktvradio", "com.tv.worldwide.spain", "com.tv.worldwide.spain", "com.arabiclite.revo"};
    public String[] videoApps = {"com.netflix.mediaclient", "com.google.android.youtube", "com.google.android.youtube.tv", "com.amazon.amazonvideo.livingroom", "com.orange.ocsgo", "fr.tf1.mytf1", "fr.francetv.pluzz", "fr.m6.m6replay", "com.disney.dedisneychannel_goo", "com.ldf.gulli.view"};
    public String[] radioApps = {"com.radio.fm.live.free.am.tunein"};
    public String[] musicApps = {"deezer.android.app", "com.apple.android.music", "com.spotify.music", "com.android.music", "deezer.android.tv", "com.amazon.mp3", "com.google.android.music", "com.google.android.apps.youtube.music", "com.google.android.apps.youtube.music", "com.soundcloud.android", "com.jamendo"};
    public String[] photoApps = {"com.android.gallery"};
    PackageManager packageManager;
    TextView txtTime, txtTime2, txtDate;
    Calendar c;
    GridView appGrdView;

    TextView meteoIcon, meteoTemp, meteoCityName;

    private Animator currentAnimator;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        List<AppInfo> listeApps = loadApps();//TODO: move out of this file


        txtTime = findViewById(R.id.txtTime);
        txtTime2 = findViewById(R.id.txtTime2);
        txtDate = findViewById(R.id.txtDate);

        txtDate.setVisibility(GONE);
        txtTime.setVisibility(GONE);


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
                        txtTime2.setText(android.text.format.DateFormat.format("HH:mm", date));
                        //android.text.format.DateFormat df = new android.text.format.DateFormat();//TODO: use to format hour & time
                        //android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date());
                        int dayOfWeek = date.getDay();
                        int month = date.getMonth();
                        String weekday = new DateFormatSymbols().getWeekdays()[dayOfWeek + 1];//Corrige l'erreur d'un jour
                        String monthStr = new DateFormatSymbols().getMonths()[month];
                        txtDate.setText(weekday.substring(0, 1).toUpperCase() + weekday.substring(1) + android.text.format.DateFormat.format(" dd ", date) + monthStr + " " + android.text.format.DateFormat.format("yyyy", date));
                        if(CategoryViewHandler.this.hasWindowFocus()) {
                            stopAllSound();
                        }
                    }

                });

            }
        }, 0, 1000);


        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf");

        meteoCityName = findViewById(R.id.txtMeteoDetails);
        meteoIcon = findViewById(R.id.txtMeteoLogo);
        meteoTemp = findViewById(R.id.txtMeteoTemp);
        meteoIcon.setTypeface(weatherFont);
        meteoIcon.setVisibility(GONE);
        meteoTemp.setVisibility(GONE);
        meteoCityName.setVisibility(GONE);
        //meteoCityName.setText(getIntent().getStringExtra("cityName"));

        //meteoTemp.setText(getIntent().getStringExtra("weatherTemperature"));
        //meteoIcon.setText(getIntent().getStringExtra("weatherIcon"));

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        findViewById(R.id.textView).setVisibility(GONE);
        findViewById(R.id.imageView).setVisibility(GONE);
        findViewById(R.id.imageView2).setVisibility(GONE);

        openCategory(Integer.parseInt(getIntent().getStringExtra("cat")), listeApps);
    }


    public List<AppInfo> loadApps() {

        try {

            packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    apps.add(appinfo);

                }
            }
            return apps;

        } catch (Exception ex) {

        }
        return null;
    }

    private void openCategory(int category, final List<AppInfo> apps) {

        final ArrayList<AppInfo> appDeCategorie = new ArrayList<>();
        String[] toutesLesAppsDeLaCategorie = {};
        if (category == MainActivity.CATEGORY_VIDEO) {
            toutesLesAppsDeLaCategorie = videoApps;
            findViewById(R.id.constLayout).setBackground(getDrawable(R.drawable.ic_sous_menu_videos));
        }
        if (category == MainActivity.CATEGORY_TV) {
            toutesLesAppsDeLaCategorie = tvApps;
            findViewById(R.id.constLayout).setBackground(getDrawable(R.drawable.ic_sous_menu_tv));
        }
        if (category == MainActivity.CATEGORY_MUSIC) {
            toutesLesAppsDeLaCategorie = musicApps;
            findViewById(R.id.constLayout).setBackground(getDrawable(R.drawable.ic_sous_menu_musiques));
        }
        if (category == MainActivity.CATEGORY_PHOTO) {
            toutesLesAppsDeLaCategorie = photoApps;
            findViewById(R.id.constLayout).setBackground(getDrawable(R.drawable.ic_sous_menu_stockage));
        }
        if (category == MainActivity.CATEGORY_RADIO) {
            toutesLesAppsDeLaCategorie = radioApps;
            findViewById(R.id.constLayout).setBackground(getDrawable(R.drawable.ic_sous_menu_radios));
        }
        if (category == MainActivity.CATEGORY_AUTRES) {
            for (AppInfo app : apps) {//On rajoute toutes les apps qui n'appartiennent aux listes établies pour les catégories

                if (!Arrays.asList(radioApps).contains(String.valueOf(app.name)) & !Arrays.asList(musicApps).contains(String.valueOf(app.name)) & !Arrays.asList(videoApps).contains(String.valueOf(app.name))) {

                    appDeCategorie.add(app);

                    Log.d("App Autre : ", String.valueOf(app.name));
                }
            }

        } else {
            for (AppInfo app : apps) {
                if (Arrays.asList(toutesLesAppsDeLaCategorie).contains(String.valueOf(app.name))) {
                    appDeCategorie.add(app);
                }
            }
        }

        try {
            appGrdView = findViewById(R.id.categories_gridview);
            appGrdView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    appGrdView.getChildAt(0).requestFocus();

                }
            });
            adapter = new AppsInfoArrayAdapter(this, R.layout.grille_categories_icone, appDeCategorie) {

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {

                    ViewHolderItem viewHolder = null;

                    if (convertView == null) {
                        convertView = getLayoutInflater().inflate(
                                R.layout.grille_categories_icone, parent, false
                        );
                        convertView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                appGrdView.getChildAt(0).requestFocus();
                            }
                        });
                        viewHolder = new ViewHolderItem();
                        viewHolder.icon = (ImageView) convertView.findViewById(R.id.imgIcon);
                        viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
                        viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolderItem) convertView.getTag();
                    }
                    Drawable imgIconDraw = appDeCategorie.get(position).icon;
                    imgIconDraw.setFilterBitmap(false);
                    viewHolder.icon.setImageDrawable(imgIconDraw);
                    viewHolder.label.setText(appDeCategorie.get(position).label);
                    viewHolder.name.setText(appDeCategorie.get(position).name);


                    final ViewHolderItem finalViewHolder = viewHolder;
                    viewHolder.icon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            // zoomImageFromThumb(v, R.drawable.rounded_square_512)
                            if (hasFocus) {
                                v.setScaleX(1.5f);
                                v.setScaleY(1.5f);
                            } else {
                                v.setScaleX(1.f);
                                v.setScaleY(1.f);
                            }

                        }
                    });
                    viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Do something
                            // zoomImageFromThumb(v, R.drawable.rounded_square_512);
                            // v.setScaleX(2.f);
                            //  v.setScaleY(2.f);
                            startActivity(getPackageManager().getLaunchIntentForPackage(appDeCategorie.get(position).name.toString()));
                            // return;


                        }
                    });
                    viewHolder.icon.setOnLongClickListener(new View.OnLongClickListener() {


                        @Override
                        public boolean onLongClick(View v) {


                            // zoomImageFromThumb(v, R.drawable.rounded_square_512);
                            return false;
                        }
                    });
                    if (position == 0) {
                        viewHolder.icon.requestFocus();
                    }
                    return convertView;

                }

                final class ViewHolderItem {
                    ImageView icon;
                    TextView label;
                    TextView name;
                }
            };

            appGrdView.setAdapter(adapter);
            // addAppGridListeners();
        } catch (Exception ex) {
            //Toast.makeText(GetApps.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            //Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }
    }

    public void addAppGridListeners() //TODO : gerer lorsque une app est touchée
    {
        try {
            appGrdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("APP TOUCHE", ((TextView) view.findViewById(R.id.txt_name)).getText().toString());
                    startActivity(getPackageManager().getLaunchIntentForPackage(((TextView) view.findViewById(R.id.txt_name)).getText().toString()));
                }
            });
        } catch (Exception ex) {
            Log.d("Erreur", "Impossible d'ajouter listeners des applications");
            Log.d("Erreur ", ex.toString());
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        stopAllSound();
        Log.d("ACTIVITY", "CALLED");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        stopAllSound();
        Log.d("ACTIVITY", "CALLED");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        stopAllSound();
        Log.d("ACTIVITY", "CALLED");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //stopAllSound();
        Log.d("ACTIVITY", "CALLED");
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void stopAllSound(){
        MediaPlayer player = new MediaPlayer();
        player.stop();
        AudioManager am = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

// Request audio focus for playback
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


    }





}

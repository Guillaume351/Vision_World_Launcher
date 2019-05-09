package world.vision.launcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
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


public class CategoryViewHandler extends AppCompatActivity {

    public static ArrayAdapter<AppInfo> adapter;
    public static List<AppInfo> apps;
    public String[] categories = {"Vidéos", "Radios", "Musiques", "Photos", "Navigateur", "Autres apps"};
    public String[] tvApps = {"tv.molotov.app", "fr.tf1.mytf1",};
    public String[] videoApps = {"com.netflix.mediaclient", "com.google.android.youtube"};
    public String[] radioApps = {"ch.radiosfrancaises"};
    public String[] musicApps = {"deezer.android.app", "com.apple.android.music", "com.spotify.music", "com.android.music"};
    public String[] photoApps = {"com.android.gallery"};
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleTimeFormat;
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
                                v.setScaleX(1.3f);
                                v.setScaleY(1.3f);
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

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.imgIcon);
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.linearLayout)//TODO: change
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }


}

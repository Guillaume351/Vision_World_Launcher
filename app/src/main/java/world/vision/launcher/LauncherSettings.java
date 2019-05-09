package world.vision.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.Reference;
import java.util.Arrays;

import world.vision.launcher.Adapter.disabledAdapter;

public class LauncherSettings extends AppCompatActivity {
    public String[] settingsCategories = {"Wifi\net Réseau", "Langues"};
    public GridView grdView;
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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


}

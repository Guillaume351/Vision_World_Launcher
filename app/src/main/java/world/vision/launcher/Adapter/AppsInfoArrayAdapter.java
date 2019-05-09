package world.vision.launcher.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import world.vision.launcher.Objects.AppInfo;

public class AppsInfoArrayAdapter extends ArrayAdapter<AppInfo> {
    public AppsInfoArrayAdapter(Context context, int resource, List<AppInfo> objects) {
        super(context, resource, objects);
    }


}

final class ViewHolderItem {
    ImageView icon;
    TextView label;
    TextView name;
}

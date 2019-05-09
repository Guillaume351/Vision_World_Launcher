package world.vision.launcher.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;


public class disabledAdapter extends ArrayAdapter<String> {

    public disabledAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}

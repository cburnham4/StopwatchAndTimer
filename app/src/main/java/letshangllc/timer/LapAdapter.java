package letshangllc.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cvburnha on 10/6/2015.
 */
public class LapAdapter extends ArrayAdapter<Lap> {
    // View lookup cache
    private static class ViewHolder {
        TextView num;
        TextView overallTime;
        TextView lapTime;
    }

    public LapAdapter(Context context, ArrayList<Lap> users) {
        super(context, R.layout.item_lap, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Lap lap = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lap, parent, false);
            viewHolder.num = (TextView) convertView.findViewById(R.id.tv_lapNum);
            viewHolder.overallTime = (TextView) convertView.findViewById(R.id.tv_overallTime);
            viewHolder.lapTime = (TextView) convertView.findViewById(R.id.tv_lapTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.num.setText(lap.getNumber()+"");
        viewHolder.overallTime.setText(lap.getOverallTime().toString());
        viewHolder.lapTime.setText(lap.getLapTime().toString());
        // Return the completed view to render on screen
        return convertView;
    }
}
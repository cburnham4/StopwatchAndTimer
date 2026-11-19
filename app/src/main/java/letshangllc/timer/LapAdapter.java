package letshangllc.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LapAdapter extends ArrayAdapter<Lap> {
    private static class ViewHolder {
        TextView num;
        TextView overallTime;
        TextView lapTime;
    }

    public LapAdapter(Context context, ArrayList<Lap> users) {
        super(context, R.layout.item_lap, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lap lap = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lap, parent, false);
            viewHolder.num = convertView.findViewById(R.id.tv_lapNum);
            viewHolder.overallTime = convertView.findViewById(R.id.tv_overallTime);
            viewHolder.lapTime = convertView.findViewById(R.id.tv_lapTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (lap != null) {
            viewHolder.num.setText(String.valueOf(lap.getNumber()));
            viewHolder.overallTime.setText(lap.getOverallTime().toString());
            viewHolder.lapTime.setText(lap.getLapTime().toString());
        }
        return convertView;
    }
}

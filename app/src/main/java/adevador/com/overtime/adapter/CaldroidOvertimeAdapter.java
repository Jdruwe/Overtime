package adevador.com.overtime.adapter;

import adevador.com.overtime.R;
import adevador.com.overtime.model.Workday;
import hirondelle.date4j.DateTime;
import io.realm.RealmResults;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CaldroidOvertimeAdapter extends CaldroidGridAdapter {

    public CaldroidOvertimeAdapter(Context context, int month, int year, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        Map<Date, Workday> extra = (Map<Date, Workday>) extraData.get("extra");

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_cell, null);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        TextView day = (TextView) cellView.findViewById(R.id.day);
        TextView time = (TextView) cellView.findViewById(R.id.time);

        day.setTextColor(Color.BLACK);
        time.setTextColor(Color.WHITE);

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            day.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            time.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }

        } else {
            shouldResetDiabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            if (CaldroidFragment.selectedBackgroundDrawable != -1) {
                cellView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
            } else {
                cellView.setBackgroundColor(resources
                        .getColor(com.caldroid.R.color.caldroid_sky_blue));
            }

            day.setTextColor(CaldroidFragment.selectedTextColor);

        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateTime.getMilliseconds(TimeZone.getDefault()));

        if (extra.containsKey(c.getTime())) {
            Workday workday = extra.get(c.getTime());
            day.setText("" + dateTime.getDay());
            if (workday.getMinutes() != 0) {
                time.setText(workday.getHours() + "h " + workday.getMinutes() + "m");
            } else {
                time.setText(workday.getHours() + "h");
            }
        } else {
            day.setText("" + dateTime.getDay());
            time.setText("");
        }

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, day);

        return cellView;
    }

}

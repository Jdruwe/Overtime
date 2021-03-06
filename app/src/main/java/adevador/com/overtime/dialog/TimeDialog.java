package adevador.com.overtime.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import adevador.com.overtime.R;
import adevador.com.overtime.activity.SettingsActivity;
import adevador.com.overtime.listener.TimeListener;
import adevador.com.overtime.listener.WorkdayListener;

/**
 * Created by druweje on 21/10/2014.
 */
public class TimeDialog extends DialogFragment {

    TimeListener mListener;
    private TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_time, null);

        timePicker = (TimePicker) view.findViewById(R.id.time_picker_start);
        timePicker.setIs24HourView(true);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Long milliseconds = sharedPref.getLong(SettingsActivity.KEY_PREF_HOURS_DAY, 25200000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);

        int hours = calendar.get(Calendar.HOUR);
        final int minutes = calendar.get(Calendar.MINUTE);

        timePicker.setCurrentHour(hours);
        timePicker.setCurrentMinute(minutes);

        final String title = getArguments().getString("title");

        builder.setTitle(title);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (title.equals("Start")) {
                            String d = timePicker.toString();
                            mListener.startSelected(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                        } else if (title.equals("End")) {
                            mListener.endSelected(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimeDialog.this.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (TimeListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement TimeListener");
        }
    }
}

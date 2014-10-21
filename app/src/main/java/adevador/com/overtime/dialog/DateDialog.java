package adevador.com.overtime.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import adevador.com.overtime.R;
import adevador.com.overtime.activity.SettingsActivity;
import adevador.com.overtime.listener.DateListener;
import adevador.com.overtime.listener.TimeListener;

/**
 * Created by druweje on 21/10/2014.
 */
public class DateDialog extends DialogFragment {

    DateListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                mListener.dateSelected(i, i2);
            }
        }, year, month, day);


        datePickerDialog.setTitle(R.string.action_date);

        DatePicker datePicker = datePickerDialog.getDatePicker();

        final int y = getArguments().getInt(CaldroidFragment.YEAR);
        final int m = getArguments().getInt(CaldroidFragment.MONTH);

        datePicker.updateDate(y, m, 0);

        try {
            java.lang.reflect.Field[] f = datePicker.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : f) {
                if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                    field.setAccessible(true);
                    Object dmPicker;
                    dmPicker = field.get(datePicker);
                    ((View) dmPicker).setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong while opening the date picker, contact a developer", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return datePickerDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DateListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement DateListener");
        }
    }
}

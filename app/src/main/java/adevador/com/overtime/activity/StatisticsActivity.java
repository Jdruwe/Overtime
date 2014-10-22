package adevador.com.overtime.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.joanzapata.android.iconify.Iconify;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import adevador.com.overtime.R;
import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.dialog.DateDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.model.Workday;
import io.realm.RealmResults;

public class StatisticsActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    private LineChart chart;
    private TextView infoLabel;
    private TextView result;
    private TextView overtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpNavigation();
        setContentView(R.layout.activity_statistics);
        processUI();
    }

    private void processUI() {
        chart = (LineChart) findViewById(R.id.chart);
        infoLabel = (TextView) findViewById(R.id.info_label);
        result = (TextView) findViewById(R.id.result);
        overtime = (TextView) findViewById(R.id.overtime);

        Calendar cal = Calendar.getInstance();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int year = sharedPref.getInt(SettingsActivity.KEY_PREF_YEAR, cal.get(cal.YEAR));
        int month = sharedPref.getInt(SettingsActivity.KEY_PREF_MONTH, cal.get(cal.MONTH));

        displayData(year, month);

    }

    private void displayData(int year, int month) {
        overtime.setVisibility(View.VISIBLE);
        RealmResults<Workday> workdays = getDataForPeriod(year, month);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");

        int hoursWorked = 0;
        int minutesWorked = 0;

        for (int i = 0; i < workdays.size(); i++) {
            hoursWorked += workdays.get(i).getHours();
            minutesWorked += workdays.get(i).getMinutes();
            xVals.add(simpleDateFormat.format(workdays.get(i).getDate()));
            String value = workdays.get(i).getHours() + "." + workdays.get(i).getMinutes();
            yVals.add(new Entry(Float.parseFloat(value), i));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Long milliseconds = sharedPref.getLong(SettingsActivity.KEY_PREF_HOURS_DAY, 25200000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);


        if (!workdays.isEmpty()) {
            int hoursLeft = minutesWorked / 60;
            int minutesLeft = minutesWorked % 60;
            hoursWorked += hoursLeft;

            String res = "You've worked <b>" + hoursWorked + "h " + minutesLeft + "m </b> over <b>" + workdays.size() + "</b> days";
            result.setText(Html.fromHtml(res));

            //Calculate difference to determine if overtime has happened in the current month
            int settingHours = calendar.get(Calendar.HOUR) * workdays.size();
            int settingMinutes = calendar.get(Calendar.MINUTE) * workdays.size();

            settingMinutes += (settingHours * 60);
            minutesWorked += (hoursWorked * 60);

            int difference = minutesWorked - settingMinutes;
            if (difference > 0) {
                int hours = difference / 60;
                int minutes = difference % 60;
                overtime.setText(Html.fromHtml(String.format("Overtime: <b>%dh %dm<b>", hours, minutes)));
            } else if (difference < 0) {
                int abs = Math.abs(difference);
                int hours = abs / 60;
                int minutes = abs % 60;
                overtime.setText(Html.fromHtml(String.format("Short time: <b>%dh %dm<b>", hours, minutes)));
            } else {
                overtime.setVisibility(View.GONE);
            }
        } else {
            result.setText(getString(R.string.no_date_found));
            overtime.setVisibility(View.GONE);
        }

        LineDataSet lineDataSet = new LineDataSet(yVals, "Working");
        lineDataSet.setLineWidth(4);
        lineDataSet.setHighLightColor(getResources().getColor(R.color.orange));
        lineDataSet.setCircleColor(getResources().getColor(R.color.orange));
        lineDataSet.setCircleSize(7);
        lineDataSet.setColors(new int[]{R.color.orange}, this);

        String monthString = new DateFormatSymbols().getMonths()[month];
        chart.setDescription(monthString + "- " + year);
        infoLabel.setText(monthString + "- " + year);
        chart.setData(new LineData(xVals, lineDataSet));
        chart.setDrawGridBackground(false);
        chart.invalidate();
    }

    private void showUpNavigation() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistics, menu);
        menu.findItem(R.id.action_date).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_calendar, R.color.dark_gray, 24, this));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_date:
                openDatePickerDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDatePickerDialog() {

        Calendar cal = Calendar.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int year = sharedPref.getInt(SettingsActivity.KEY_PREF_YEAR, cal.get(cal.YEAR));
        int month = sharedPref.getInt(SettingsActivity.KEY_PREF_MONTH, cal.get(cal.MONTH) + 1);

        Bundle b = new Bundle();
        b.putInt(DateDialog.YEAR, year);
        b.putInt(DateDialog.MONTH, month);
        b.putInt(DateDialog.DATE, 1);
        DialogFragment picker = new DateDialog();
        picker.setArguments(b);
        picker.show(getSupportFragmentManager(), "frag_date_picker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(SettingsActivity.KEY_PREF_YEAR, year);
        editor.putInt(SettingsActivity.KEY_PREF_MONTH, month);
        editor.apply();
        displayData(year, month);
    }

    private RealmResults<Workday> getDataForPeriod(int year, int month) {
        return WorkdayUtil.getAll(this, year, month);
    }
}

package adevador.com.overtime.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.joanzapata.android.iconify.Iconify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import adevador.com.overtime.R;
import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.dialog.DateDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.model.Workday;
import io.realm.RealmResults;

public class StatisticsActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpNavigation();
        setContentView(R.layout.activity_statistics);
        processUI();
    }

    private void processUI() {
        chart = (LineChart) findViewById(R.id.chart);

        Calendar cal = Calendar.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int year = sharedPref.getInt(SettingsActivity.KEY_PREF_YEAR, cal.get(cal.YEAR));
        int month = sharedPref.getInt(SettingsActivity.KEY_PREF_MONTH, cal.get(cal.MONTH));

        displayData(year, month);
    }

    private void displayData(int year, int month) {
        RealmResults<Workday> workdays = getDataForPeriod(year, month);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");

        for (int i = 0; i < workdays.size(); i++) {
            xVals.add(simpleDateFormat.format(workdays.get(i).getDate()));
            String value = workdays.get(i).getHours() + "." + workdays.get(i).getMinutes();
            yVals.add(new Entry(Float.parseFloat(value), i));
        }

        LineDataSet lineDataSet = new LineDataSet(yVals, "Working");
        lineDataSet.setLineWidth(4);
        lineDataSet.setHighLightColor(getResources().getColor(R.color.orange));
        lineDataSet.setCircleColor(getResources().getColor(R.color.orange));
        lineDataSet.setCircleSize(7);
        lineDataSet.setColors(new int[]{R.color.orange}, this);

        chart.setDescription("October - 2014");
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

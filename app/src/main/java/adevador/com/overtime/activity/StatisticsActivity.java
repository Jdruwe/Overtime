package adevador.com.overtime.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.joanzapata.android.iconify.Iconify;
import com.roomorama.caldroid.CaldroidFragment;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adevador.com.overtime.R;
import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.dialog.DateDialog;
import adevador.com.overtime.dialog.WorkDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.DateListener;
import adevador.com.overtime.model.Workday;
import io.realm.RealmResults;

public class StatisticsActivity extends ActionBarActivity implements DateListener {

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
        RealmResults<Workday> workdays = getDataForPeriod(2014, 9);

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

    }

    private void showUpNavigation() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistics, menu);
        menu.findItem(R.id.action_date).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_calendar, R.color.white, 24, this));
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
        DialogFragment dateDialog = new DateDialog();
        Bundle bundle = new Bundle();
        Calendar cal = Calendar.getInstance();
        bundle.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        bundle.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        dateDialog.setArguments(bundle);
        dateDialog.show(getSupportFragmentManager(), "date");
    }

    @Override
    public void dateSelected(int year, int month) {

    }

    private RealmResults<Workday> getDataForPeriod(int year, int month) {
        return WorkdayUtil.getAll(this, year, month);
    }
}

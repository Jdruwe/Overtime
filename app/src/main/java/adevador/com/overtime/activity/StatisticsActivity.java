package adevador.com.overtime.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.android.iconify.Iconify;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;

import adevador.com.overtime.R;
import adevador.com.overtime.dialog.DateDialog;
import adevador.com.overtime.dialog.WorkDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.DateListener;

public class StatisticsActivity extends ActionBarActivity implements DateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showUpNavigation();
        setContentView(R.layout.activity_statistics);
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
        System.out.println();
    }
}

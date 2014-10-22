package adevador.com.overtime.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import adevador.com.overtime.R;
import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.dialog.DeleteDialog;
import adevador.com.overtime.dialog.WorkDialog;
import adevador.com.overtime.fragment.CalendarFragment;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.CalendarListener;
import adevador.com.overtime.listener.TimeListener;
import adevador.com.overtime.model.Workday;


public class MainActivity extends ActionBarActivity implements CalendarListener, TimeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            displayFragment(CalendarFragment.newInstance());
        }
    }

    public void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_statistics).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_bar_chart_o, R.color.dark_gray, 24, this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_statistics:
                Intent statisticsIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(statisticsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dateSelected(Date date) {
        ArrayList<Date> dateList = new ArrayList<Date>();
        dateList.add(date);
        openTimeDialog(dateList);
    }

    @Override
    public void datesSelected(Set<Date> dates) {
        ArrayList<Date> dateList = new ArrayList<Date>();
        dateList.addAll(dates);
        openTimeDialog(dateList);
    }

    @Override
    public void workdayClicked(Date date) {
        Workday workday = WorkdayUtil.get(this, date);
        openDeleteDialog(workday.getDate());
    }

    private void openTimeDialog(ArrayList<Date> date) {
        DialogFragment workDialog = new WorkDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dates", date);
        workDialog.setArguments(bundle);
        workDialog.show(getSupportFragmentManager(), "hours");
    }

    private void openDeleteDialog(Date date) {
        DialogFragment deleteDialog = new DeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("date", date);
        deleteDialog.setArguments(bundle);
        deleteDialog.show(getSupportFragmentManager(), "delete");
    }


    @Override
    public void timeWorked(ArrayList<Date> dates, int hours, int minutes) {
        WorkdayUtil.save(this, dates, hours, minutes);
        refreshCalendar(null);
    }

    @Override
    public void deleteWorkday(Date date) {
        WorkdayUtil.delete(this, date);
        refreshCalendar(date);
    }

    private void refreshCalendar(Date date) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof CalendarFragment) {
                if (date != null) {
                    ((CalendarFragment) fragment).resetWorkdayBackground(date);
                }
                ((CalendarFragment) fragment).displayData();
                break;
            }
        }
    }
}

package adevador.com.overtime.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.android.iconify.Iconify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adevador.com.overtime.R;
import adevador.com.overtime.dialog.TimeDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.TimeListener;
import adevador.com.overtime.util.WorkdayHelper;

public class WorkdayActivity extends ActionBarActivity implements TimeListener {

    private Button addTimings;
    private ListView timingsList;
    private TextView infoLabel;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listItems;
    private ArrayList<Date> dates;

    private class Timing {

        private int startHour;
        private int startMinute;
        private int endHour;
        private int endMinute;

        public int getStartHour() {
            return startHour;
        }

        public void setStartHour(int startHour) {
            this.startHour = startHour;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public void setStartMinute(int startMinute) {
            this.startMinute = startMinute;
        }

        public int getEndHour() {
            return endHour;
        }

        public void setEndHour(int endHour) {
            this.endHour = endHour;
        }

        public int getEndMinute() {
            return endMinute;
        }

        public void setEndMinute(int endMinute) {
            this.endMinute = endMinute;
        }

        @Override
        public String toString() {

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, this.getStartHour());
            calendar.set(Calendar.MINUTE, this.getStartMinute());
            Date startDate = calendar.getTime();

            calendar.set(Calendar.HOUR_OF_DAY, this.getEndHour());
            calendar.set(Calendar.MINUTE, this.getEndMinute());
            Date endDate = calendar.getTime();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String start = simpleDateFormat.format(startDate);
            String end = simpleDateFormat.format(endDate);

            return start + " - " + end;
        }

        public Map<String, Integer> calculate() {

            Map<String, Integer> result = new HashMap<>();

            //Start End Result
            //8:00 17:00 9:00
            if ((getStartHour() < getEndHour()) && (getStartMinute() == getEndMinute())) {
                result.put("hour", getEndHour() - getStartHour());
                result.put("minute", 0);
            }

            //8:00 17:30 9:30
            if ((getStartHour() < getEndHour()) && (getStartMinute() < getEndMinute())) {
                result.put("hour", getEndHour() - getStartHour());
                result.put("minute", getEndMinute() - getStartMinute());
            }

            //8:30 17:20 8:50
            if ((getStartHour() < getEndHour()) && (getStartMinute() > getEndMinute())) {
                result.put("hour", (getEndHour() - getStartHour()) - 1);
                result.put("minute", 60 - (getStartMinute() - getEndMinute()));
            }

            //8:30 8:40 0:10
            if ((getStartHour() == getEndHour()) && (getStartMinute() < getEndMinute())) {
                result.put("hour", 0);
                result.put("minute", getEndMinute() - getStartMinute());
            }

            //17:00	8:00 15:00
            if ((getStartHour() > getEndHour()) && (getStartMinute() == getEndMinute())) {
                result.put("hour", (24 - getStartHour()) + getEndHour());
                result.put("minute", 0);
            }

            //17:30	8:00 14:30
            if ((getStartHour() > getEndHour()) && (getStartMinute() > getEndMinute())) {
                result.put("hour", ((24 - getStartHour()) + getEndHour()) - 1);
                result.put("minute", getStartMinute() - getEndMinute());
            }

            //17:20	8:30 15:10
            if ((getStartHour() > getEndHour()) && (getStartMinute() < getEndMinute())) {
                result.put("hour", (24 - getStartHour()) + getEndHour());
                result.put("minute", getEndMinute() - getStartMinute());
            }

            //17:00	17:00 24:00
            if ((getStartHour() == getEndHour()) && (getStartMinute() == getEndMinute())) {
                result.put("hour", 24);
                result.put("minute", 0);
            }

            //17:20	8:20 15:00
            if ((getStartHour() > getEndHour()) && (getStartMinute() == getEndMinute())) {
                result.put("hour", (24 - getStartHour()) + getEndHour());
                result.put("minute", 0);
            }

            //17:20	17:10 23:50
            if ((getStartHour() == getEndHour()) && (getStartMinute() > getEndMinute())) {
                result.put("hour", 23);
                result.put("minute", 60 - (getStartMinute() - getEndMinute()));
            }

            return result;
        }
    }

    private Timing timing;
    private List<Map<String, Integer>> finalTimings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workday);
        showUpNavigation();
        finalTimings = new ArrayList<>();

        infoLabel = (TextView) findViewById(R.id.info_label);

        addTimings = (Button) findViewById(R.id.add_timing);
        addTimings.setCompoundDrawables(IconGenerator.getIcon(Iconify.IconValue.fa_clock_o, R.color.dark_gray, 15, this), null, null, null);

        timingsList = (ListView) findViewById(R.id.timings_list);
        listItems = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        timingsList.setAdapter(adapter);

        //restore final timings on orientation change
        if (savedInstanceState != null) {
            finalTimings = (ArrayList) savedInstanceState.getSerializable("finalTimings");
            if (finalTimings.size() > 0) {
                timingsList.setVisibility(View.VISIBLE);
                infoLabel.setVisibility(View.VISIBLE);
                List<String> items = (ArrayList) savedInstanceState.getSerializable("listItems");
                for (String item : items) {
                    listItems.add(item);
                }
                adapter.notifyDataSetChanged();

                int totalHours = 0;
                int totalMinutes = 0;

                for (Map<String, Integer> timing : finalTimings) {
                    totalHours += timing.get("hour");
                    totalMinutes += timing.get("minute");
                }

                infoLabel.setText("Worked: " + totalHours + "h " + totalMinutes + "m");
            }
        }

        Intent intent = getIntent();
        dates = (ArrayList<Date>) intent.getSerializableExtra("dates");

        setListeners();
    }

    private void showUpNavigation() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setListeners() {
        addTimings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get start time
                DialogFragment startTimeDialog = new TimeDialog();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Start");
                startTimeDialog.setArguments(bundle);
                startTimeDialog.show(getSupportFragmentManager(), "start");
            }
        });
    }

    @Override
    public void startSelected(int hour, int minute) {

        timing = new Timing();
        timing.setStartHour(hour);
        timing.setStartMinute(minute);

        //Get end time
        DialogFragment startTimeDialog = new TimeDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", "End");
        startTimeDialog.setArguments(bundle);
        startTimeDialog.show(getSupportFragmentManager(), "end");
    }

    @Override
    public void endSelected(int hour, int minute) {

        timing.setEndHour(hour);
        timing.setEndMinute(minute);

        Map<String, Integer> result = timing.calculate();

        int totalHours = 0;
        int totalMinutes = 0;

        for (Map<String, Integer> timing : finalTimings) {
            totalHours += timing.get("hour");
            totalMinutes += timing.get("minute");
        }

        totalHours += result.get("hour");
        totalMinutes += result.get("minute");

        if ((totalMinutes == 0 && totalHours <= 24) || (totalMinutes > 0 && totalHours < 24)) {
            finalTimings.add(result);
            timingsList.setVisibility(View.VISIBLE);
            infoLabel.setVisibility(View.VISIBLE);
            infoLabel.setText("Worked: " + totalHours + "h " + totalMinutes + "m");
            listItems.add(timing.toString());
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Only 24 hours a day allowed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("finalTimings", (ArrayList) finalTimings);
        savedInstanceState.putSerializable("listItems", (ArrayList) listItems);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workday, menu);
        menu.findItem(R.id.action_lunch).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_coffee, R.color.dark_gray, 24, this));
        menu.findItem(R.id.action_save).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_check, R.color.dark_gray, 24, this));
        return true;
    }

    private void subtractLunchTime() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Long milliseconds = sharedPref.getLong(SettingsActivity.KEY_PREF_LUNCH_TIME, 0);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);

        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        listItems.add("- " + simpleDateFormat.format(date));

        Map<String, Integer> result = new HashMap<>();

        //Make both number negative
        result.put("hour", 0 - calendar.get(Calendar.HOUR_OF_DAY));
        result.put("minute", 0 - calendar.get(Calendar.MINUTE));

        finalTimings.add(result);

        int totalHours = 0;
        int totalMinutes = 0;

        for (Map<String, Integer> timing : finalTimings) {
            totalHours += timing.get("hour");
            totalMinutes += timing.get("minute");
        }

        timingsList.setVisibility(View.VISIBLE);
        infoLabel.setText("Worked: " + totalHours + "h " + totalMinutes + "m");
        infoLabel.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();

        Toast.makeText(getApplicationContext(), "Subtrated lunch time", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                saveTimings();
                return true;
            case R.id.action_lunch:
                subtractLunchTime();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTimings() {
        if (finalTimings.size() > 0) {

            int totalHours = 0;
            int totalMinutes = 0;

            for (Map<String, Integer> timing : finalTimings) {
                totalHours += timing.get("hour");
                totalMinutes += timing.get("minute");
            }

            WorkdayHelper.save(dates, totalHours, totalMinutes);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "No timings found", Toast.LENGTH_LONG).show();
        }
    }
}

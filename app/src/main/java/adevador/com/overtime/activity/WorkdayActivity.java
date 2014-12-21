package adevador.com.overtime.activity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.android.iconify.Iconify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adevador.com.overtime.R;
import adevador.com.overtime.dialog.TimeDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.TimeListener;

public class WorkdayActivity extends ActionBarActivity implements TimeListener {

    private Button addTimings;
    private ListView timingsList;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems;

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
    }

    private Timing timing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workday);

        addTimings = (Button) findViewById(R.id.add_timing);
        addTimings.setCompoundDrawables(IconGenerator.getIcon(Iconify.IconValue.fa_clock_o, R.color.dark_gray, 15, this), null, null, null);

        timingsList = (ListView) findViewById(R.id.timings_list);
        listItems = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        timingsList.setAdapter(adapter);

        Intent intent = getIntent();
        final ArrayList<Date> dates = (ArrayList<Date>) intent.getSerializableExtra("dates");

        setListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        //Show
        timingsList.setVisibility(View.VISIBLE);
        listItems.add(timing.toString());
        adapter.notifyDataSetChanged();
    }
}

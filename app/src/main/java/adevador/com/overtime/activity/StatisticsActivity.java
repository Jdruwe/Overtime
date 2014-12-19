package adevador.com.overtime.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.joanzapata.android.iconify.Iconify;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import adevador.com.overtime.R;
import adevador.com.overtime.util.GoogleCalendarHelper;
import adevador.com.overtime.util.WorkdayHelper;
import adevador.com.overtime.dialog.DateDialog;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.model.Workday;
import se.emilsjolander.sprinkles.CursorList;

public class StatisticsActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener {

    private LineChart chart;
    private TextView infoLabel;
    private TextView result;
    private TextView overtime;
    private List<ProcessedWorkDay> processedWorkDayList;

    class ProcessedWorkDay {
        private long id;
        private Date date;
        private Boolean overtime;
        private String timeEvaluation;
        private long googleCalendarEventId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Boolean getOvertime() {
            return overtime;
        }

        public void setOvertime(Boolean overtime) {
            this.overtime = overtime;
        }

        public String getTimeEvaluation() {
            return timeEvaluation;
        }

        public void setTimeEvaluation(String timeEvaluation) {
            this.timeEvaluation = timeEvaluation;
        }

        public long getGoogleCalendarEventId() {
            return googleCalendarEventId;
        }

        public void setGoogleCalendarEventId(long googleCalendarEventId) {
            this.googleCalendarEventId = googleCalendarEventId;
        }
    }

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

    private ProcessedWorkDay generateProcessedWorkday(long id, Date date, int h, int m, int settingsMinutes, long googleCalendarEventId) {
        ProcessedWorkDay processedWorkDay = new ProcessedWorkDay();
        processedWorkDay.setDate(date);
        processedWorkDay.setGoogleCalendarEventId(googleCalendarEventId);
        processedWorkDay.setId(id);

        int minutesWorked = m + (h * 60);
        minutesWorked = minutesWorked - settingsMinutes;

        String evaluation;

        if (minutesWorked > 0) {
            evaluation = "Overtime: ";
            processedWorkDay.setOvertime(true);
        } else if (minutesWorked < 0) {
            evaluation = "Short time: ";
            processedWorkDay.setOvertime(false);
        } else {
            evaluation = "No overtime";
            processedWorkDay.setOvertime(false);
        }

        if (minutesWorked != 0) {

            minutesWorked = Math.abs(minutesWorked);

            int hours = minutesWorked / 60;
            int minutes = minutesWorked % 60;

            if (hours > 0) {
                evaluation += hours + "h";
            }

            if (minutes > 0) {
                evaluation += minutes + "m";
            }
        }

        processedWorkDay.setTimeEvaluation(evaluation);

        return processedWorkDay;
    }

    private void displayData(int year, int month) {

        processedWorkDayList = new ArrayList<>();

        overtime.setVisibility(View.VISIBLE);
        CursorList<Workday> workdays = getDataForPeriod(year, month);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");

        int hoursWorked = 0;
        int minutesWorked = 0;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Long milliseconds = sharedPref.getLong(SettingsActivity.KEY_PREF_HOURS_DAY, 25200000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);

        //Calculate difference to determine if overtime has happened in the current month
        int settingHours = calendar.get(Calendar.HOUR) * workdays.size();
        int settingMinutes = calendar.get(Calendar.MINUTE) * workdays.size();

        settingMinutes += (settingHours * 60);

        int dailyMinutes = (calendar.get(Calendar.HOUR) * 60) + calendar.get(Calendar.MINUTE);

        for (int i = 0; i < workdays.size(); i++) {
            hoursWorked += workdays.get(i).getHours();
            minutesWorked += workdays.get(i).getMinutes();
            xVals.add(simpleDateFormat.format(workdays.get(i).getDate()));
            String value = workdays.get(i).getHours() + "." + workdays.get(i).getMinutes();
            yVals.add(new Entry(Float.parseFloat(value), i));

            processedWorkDayList.add(generateProcessedWorkday(workdays.get(i).getId(), workdays.get(i).getDate(),
                    workdays.get(i).getHours(), workdays.get(i).getMinutes(), dailyMinutes, workdays.get(i).getGoogleCalendarEventId()));
        }

        if (!workdays.asList().isEmpty()) {
            int hoursLeft = minutesWorked / 60;
            int minutesLeft = minutesWorked % 60;
            hoursWorked += hoursLeft;

            String res = "You've worked <b>" + hoursWorked + "h " + minutesLeft + "m </b> over <b>" + workdays.size() + "</b> days";
            result.setText(Html.fromHtml(res));

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
            result.setText(getString(R.string.no_data_found));
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
            case R.id.action_google_calendar_sync:
                new GoogleCalendarSync(StatisticsActivity.this).execute();
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

    private CursorList<Workday> getDataForPeriod(int year, int month) {
        return WorkdayHelper.getAll(year, month);
    }

    private class GoogleCalendarSync extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog dialog;

        public GoogleCalendarSync(StatisticsActivity statisticsActivity) {
            dialog = new ProgressDialog(statisticsActivity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Syncing Google calendar");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return syncCalendar();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result) {
                Toast.makeText(getApplicationContext(), "Calendar sync completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Calendar sync error", Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteCalendarEvent(long eventId) {
            GoogleCalendarHelper.deleteCalendarEvent(getContentResolver(), eventId);
        }

        private void insertCalendarEvent(long calendarId, ProcessedWorkDay processedWorkDay) {

            if (processedWorkDay.getGoogleCalendarEventId() != 0) {
                deleteCalendarEvent(processedWorkDay.getGoogleCalendarEventId());
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(processedWorkDay.getDate());
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            Calendar beginTime = new GregorianCalendar(year, month, day);
            beginTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            beginTime.set(Calendar.HOUR, 0);
            beginTime.set(Calendar.MINUTE, 0);
            beginTime.set(Calendar.SECOND, 0);
            beginTime.set(Calendar.MILLISECOND, 0);
            long start = beginTime.getTimeInMillis();

            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            contentValues.put(CalendarContract.Events.ALL_DAY, true);
            contentValues.put(CalendarContract.Events.DTSTART, start);
            contentValues.put(CalendarContract.Events.DTEND, start);
            contentValues.put(CalendarContract.Events.EVENT_COLOR, Color.GREEN);

            contentValues.put(CalendarContract.Events.TITLE, processedWorkDay.getTimeEvaluation());

            TimeZone tz = TimeZone.getDefault();
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());

            Uri uri = getContentResolver().insert(CalendarContract.Events.CONTENT_URI, contentValues);

            long eventId = Long.valueOf(uri.getLastPathSegment());
            Workday workday = WorkdayHelper.get(processedWorkDay.getId());
            WorkdayHelper.setGoogleCalendarEventId(workday, eventId);
        }

        private boolean syncCalendar() {
            // Initialize Calendar service with valid OAuth credentials
            String[] projection = new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.NAME,
                    CalendarContract.Calendars.ACCOUNT_NAME,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            };

            Cursor cursor = getContentResolver().query(
                    CalendarContract.Calendars.CONTENT_URI,
                    projection,
                    null,
                    null, CalendarContract.Calendars._ID + " ASC");

            if (cursor.moveToFirst()) {
                long calendarId = cursor.getLong(0);
                //String displayName = cursor.getString(3);
                for (ProcessedWorkDay processedWorkDay : processedWorkDayList) {
                    insertCalendarEvent(calendarId, processedWorkDay);
                }
                return true;
            } else {
                return false;
            }
        }

    }
}



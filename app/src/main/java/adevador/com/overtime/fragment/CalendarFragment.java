package adevador.com.overtime.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.android.iconify.Iconify;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import adevador.com.overtime.R;
import adevador.com.overtime.activity.SettingsActivity;
import adevador.com.overtime.data.WorkdayUtil;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.CalendarListener;
import adevador.com.overtime.model.Workday;
import io.realm.RealmResults;

public class CalendarFragment extends CaldroidFragment {

    private CalendarListener mListener;
    private CaldroidFragment caldroidFragment;
    private Set<Date> multiSelection;
    private boolean inMultiSelection;
    private Map<Date, Workday> workdays;
    private Calendar calendar;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.multi_selection, menu);
        menu.findItem(R.id.action_multi).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_clock_o, R.color.dark_gray, 24, getActivity()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_multi:
                mListener.datesSelected(multiSelection);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        multiSelection = new HashSet<Date>();
        inMultiSelection = false;
        calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JULY, 18, 0, 0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        processUI(view);
        setListeners();
        displayData();
        return view;

    }

    private void processUI(View view) {
        addCalendar();
    }

    private void addCalendar() {
        caldroidFragment = new CaldroidOvertimeFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();
    }

    private void setListeners() {

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                if (!checkIfWorkdayExists(date)) {
                    if (inMultiSelection) {
                        multiDateSelected(date);
                    } else {
                        mListener.dateSelected(date);
                    }
                } else {

                    mListener.workdayClicked(workdays.get(date));
                }
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                if (!checkIfWorkdayExists(date)) {
                    enableMultiSelection();
                    multiDateSelected(date);
                } else {
                    mListener.workdayClicked(workdays.get(date));
                }
            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }


    private boolean checkIfWorkdayExists(Date date) {
        Workday workday = workdays.get(date);
        return workday != null && workdays != null && !workdays.isEmpty();
    }

    private void multiDateSelected(Date date) {
        if (multiSelection.contains(date)) {
            multiSelection.remove(date);
            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_white, date);
            if (multiSelection.isEmpty()) {
                disableMultiSelection();
            }
        } else {
            multiSelection.add(date);
        }
        displayMultiSelection();
    }

    private void enableMultiSelection() {
        setHasOptionsMenu(true);
        inMultiSelection = true;
    }

    private void disableMultiSelection() {
        setHasOptionsMenu(false);
        inMultiSelection = false;
    }

    private void displayMultiSelection() {
        for (Date date : multiSelection) {
            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light, date);
        }
        caldroidFragment.refreshView();
    }

    public void resetWorkdayBackground(Date date) {
        caldroidFragment.setBackgroundResourceForDate(R.color.white, date);
    }

    public void displayData() {

        setHasOptionsMenu(false);
        multiSelection.clear();
        inMultiSelection = false;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Long milliseconds = sharedPref.getLong(SettingsActivity.KEY_PREF_HOURS_DAY, 25200000);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);

        workdays = new HashMap<>();

        Map<Date, Workday> extraData = new HashMap<Date, Workday>();

        RealmResults<Workday> results = WorkdayUtil.getAll(getActivity());
        for (Workday workday : results) {
            if (workday.getHours() > hours || (workday.getHours() == hours && workday.getMinutes() > minutes)) {
                caldroidFragment.setBackgroundResourceForDate(R.color.orange, workday.getDate());
            } else {
                caldroidFragment.setBackgroundResourceForDate(R.color.green, workday.getDate());
            }
            workdays.put(workday.getDate(), workday);
            extraData.put(workday.getDate(), workday);
        }
        sendDisplayData(extraData);
        caldroidFragment.refreshView();
    }

    private void sendDisplayData(Map<Date, Workday> extra) {
        HashMap<String, Object> extraData = caldroidFragment.getExtraData();
        extraData.put("extra", extra);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CalendarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CalendarListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

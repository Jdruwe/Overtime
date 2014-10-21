package adevador.com.overtime.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.joanzapata.android.iconify.Iconify;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import adevador.com.overtime.R;
import adevador.com.overtime.generator.IconGenerator;
import adevador.com.overtime.listener.CalendarListener;

public class CalendarFragment extends CaldroidFragment {

    private CalendarListener mListener;
    private CaldroidFragment caldroidFragment;
    private Set<Date> multiSelection;
    private boolean inMultiSelection;

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
        menu.findItem(R.id.action_multi).setIcon(IconGenerator.getIcon(Iconify.IconValue.fa_clock_o, R.color.white, 24, getActivity()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        multiSelection = new HashSet<Date>();
        inMultiSelection = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        processUI(view);
        setListeners();
        return view;

    }

    private void processUI(View view) {
        addCalendar();

    }

    private void addCalendar() {
        caldroidFragment = new CaldroidFragment();
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
                if (inMultiSelection) {
                    multiDateSelected(date);
                } else {
                    mListener.dateSelected(date);
                }
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                enableMultiSelection();
                multiDateSelected(date);
            }
        };

        caldroidFragment.setCaldroidListener(listener);
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

    private void enableMultiSelection(){
        setHasOptionsMenu(true);
        inMultiSelection = true;
    }

    private void disableMultiSelection(){
        setHasOptionsMenu(false);
        inMultiSelection = false;
    }

    private void displayMultiSelection() {
        for (Date date : multiSelection) {
            caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light, date);
        }
        caldroidFragment.refreshView();
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

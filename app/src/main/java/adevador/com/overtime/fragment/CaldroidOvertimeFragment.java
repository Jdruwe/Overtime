package adevador.com.overtime.fragment;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import adevador.com.overtime.adapter.CaldroidOvertimeAdapter;

public class CaldroidOvertimeFragment extends CaldroidFragment {

	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		return new CaldroidOvertimeAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);
	}

}

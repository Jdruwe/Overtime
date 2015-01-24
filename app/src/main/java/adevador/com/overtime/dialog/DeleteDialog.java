package adevador.com.overtime.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import adevador.com.overtime.R;
import adevador.com.overtime.listener.WorkdayListener;
import adevador.com.overtime.model.Workday;

/**
 * Created by druweje on 21/10/2014.
 */
public class DeleteDialog extends DialogFragment {

    WorkdayListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Workday workday = (Workday) getArguments().getSerializable("workday");

        builder.setTitle(getString(R.string.delete_title));
        builder.setMessage(R.string.delete_confirmation)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.deleteWorkday(workday);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DeleteDialog.this.dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (WorkdayListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement TimeListener");
        }
    }
}

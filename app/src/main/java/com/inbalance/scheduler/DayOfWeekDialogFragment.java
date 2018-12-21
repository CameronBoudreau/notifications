package com.inbalance.scheduler;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.inbalance.inbalance.R;

import java.util.ArrayList;


public class DayOfWeekDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick( ArrayList<Integer> checked, int position);
    }

    public static DayOfWeekDialogFragment newInstance(int pos, int[] days) {
        DayOfWeekDialogFragment frag = new DayOfWeekDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        bundle.putIntArray("days", days);
        frag.setArguments(bundle);

        return frag;
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    int pos;

    private boolean[] checked = new boolean[] {true, true, true, true, true, true, true};
    public ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getParentFragment().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get args to set checked state and note view position in list
        pos = getArguments().getInt("pos");
        int[] days = getArguments().getIntArray("days");

        for (int i=0; i < days.length; i++) {
            if (days[i] == 1) {
                this.checked[i] = true;
                mSelectedItems.add(i + 1);
            } else {
                this.checked[i] = false;
            }
        }
        Log.d("DialogClick", String.format("Selected items on create: %s", mSelectedItems));
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_days)
                .setMultiChoiceItems(Scheduler.daysOfWeekLong, this.checked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked && !mSelectedItems.contains(which + 1)) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(Integer.valueOf(which + 1));
                                    Log.d("DialogClick", String.format("Position %s checked! List after: %s", which, mSelectedItems));
                                } else if (mSelectedItems.contains(which + 1)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(new Integer(which + 1));
                                    Log.d("DialogClick", String.format("Position %s UNchecked! List after: %s", which, mSelectedItems));
                                }
                            }
                        })
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(mSelectedItems, pos);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();

    }

}

package com.inbalance.scheduler;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mListener != null) {
            this.mListener.returnTime(view, hourOfDay, minute, pos);
        }
    }

    public interface OnTimeSetListener {
        public void returnTime(TimePicker view, int hourOfDay, int minute, int pos);
    }

    public static TimePickerDialogFragment newInstance(int pos, int[] time) {
        TimePickerDialogFragment frag = new TimePickerDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pos", pos);
        bundle.putIntArray("time", time);
        frag.setArguments(bundle);

        return frag;
    }

    TimePickerDialogFragment.OnTimeSetListener mListener;
    int pos;
    int hour;
    int min;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TimePickerDia", "Starting picker");
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnTimeSetListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getParentFragment().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Get Args
        pos = getArguments().getInt("pos");
        int[] time = getArguments().getIntArray("time");

        if (time != null) {
            hour = time[0];
            min = time[1];
        } else {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            min = c.get(Calendar.MINUTE);
        }

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, min,
                false);
    }
}

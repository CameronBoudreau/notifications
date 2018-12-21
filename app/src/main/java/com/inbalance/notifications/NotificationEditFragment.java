package com.inbalance.notifications;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.inbalance.inbalance.R;
import com.inbalance.scheduler.Scheduler;
import com.inbalance.scheduler.SchedulerListFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationEditFragment extends Fragment {

    public Notification notification;
    public ArrayList<Scheduler> schedulerList;
    public NotificationsActivity callback;

    public NotificationEditFragment() {
        // Required empty public constructor
    }

    public static NotificationEditFragment newInstance(Notification notification) {
        NotificationEditFragment frag = new NotificationEditFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("notification", notification);
        frag.setArguments(bundle);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_create_notification);
        item.setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_edit, container, false);

        this.notification = (Notification) getArguments().getSerializable("notification");
        Log.d("NotifEditFrag", "Notification: " + notification.toString());

        Switch switchNotificationActive = (Switch) view.findViewById(R.id.switch_notification_active);
        EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        EditText editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);
        Spinner spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);
        Button buttonDelete = (Button) view.findViewById(R.id.button_edit_notification_delete);

        switchNotificationActive.setChecked(this.notification.getActive());
        editTextName.setText(notification.getName());
        editTextMessage.setText(notification.getMessage());
        int pos = Arrays.asList(Notification.categoryList).indexOf(notification.getCategory());
        if (pos < 0) {
            pos = 0;
        }
        spinnerCategory.setSelection(pos);

        switchNotificationActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.toggleActive();
            }
        });

        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    notification.setName(((EditText) v).getText().toString());
                    Log.d("NameEdit", String.format("Name for notification being saved after focus loss."));
                }
            }
        });

        editTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    notification.setMessage(((EditText) v).getText().toString());
                    Log.d("NameEdit", String.format("Message for notification being saved after focus loss."));
                }
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] categoriesArrary = getResources().getStringArray(R.array.default_categories);
                notification.setCategory(categoriesArrary[position].toLowerCase());
                Log.d("CategoryEdit", String.format("Category for notification being saved on selection."));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonDelete.setTag(notification.getID());
        buttonDelete.setOnClickListener(callback);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment frag =  fm.findFragmentById(R.id.notification_fragment_container);

        SchedulerListFragment newSchedulerListFragment = new SchedulerListFragment().newInstance(notification.getID());

        FragmentTransaction transaction = fm.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment
        transaction.replace(R.id.notification_edit_scheduler_fragment_container, newSchedulerListFragment, "EditSchedulerFrag");

        // Commit the transaction
        transaction.commit();

        return view;
    }

}

package com.inbalance.notifications;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.inbalance.inbalance.R;

import androidx.fragment.app.Fragment;

public class NotificationCreateFragment extends Fragment {

    public String name;
    public String message;
    public String category;

    public NotificationCreateFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View view = inflater.inflate(R.layout.fragment_notification_create, container, false);;

        EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    name = ((EditText) v).getText().toString();
                    Log.d("NameEdit", String.format("Name for notification being saved after focus loss."));
                }
            }
        });

        EditText editTextMessage = (EditText) view.findViewById(R.id.editTextMessage);
        editTextMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    message = ((EditText) v).getText().toString();
                    Log.d("NameEdit", String.format("Message for notification being saved after focus loss."));
                }
            }
        });

        Spinner spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] categoriesArrary = getResources().getStringArray(R.array.default_categories);
                category = categoriesArrary[position].toLowerCase();
                Log.d("CategoryEdit", String.format("Category for notification being saved on selection."));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_create_notification);
        item.setVisible(false);
    }

}

package com.inbalance.scheduler;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.inbalance.inbalance.R;

import java.util.ArrayList;

public class SchedulerListAdapter extends ArrayAdapter<Scheduler> {

    public int resourceLayout;
    private Context context;
    private ArrayList<Scheduler> schedulerList;
    private SchedulerListFragment callback;
    private int notificationID;

    private static class ViewHolder {
        EditText messageView;
        Switch activeSwitch;
        Button daysButton;
        Button timeButton;
        Button deleteButton;
        int position;
    }

    public SchedulerListAdapter(Context context, int resource, ArrayList<Scheduler> schedulerList, SchedulerListFragment callback, int notificationID) {
        super(context, resource, schedulerList);
        this.resourceLayout = resource;
        this.context = context;
        this.schedulerList = schedulerList;
        this.callback = callback;
        this.notificationID = notificationID;
    }

    public interface SchedulerListAdapterCallback {
        void deleteItem(View view);
    }

    @Override
    public int getCount() {
        return schedulerList.size();
    }

    @Override
    public Scheduler getItem(int position) {
        return this.schedulerList.get(position);
    }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("SchedulerListAdap", "Position: " + position);
        Scheduler scheduler = getItem(position);
//        Log.d("SchedulerListAdap", "Position: " + position + "\nItem: " + scheduler.toString());

        ViewHolder holder;

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.scheduler_item_layout, parent, false);

            holder.messageView = (EditText) convertView.findViewById(R.id.editText_message);
            holder.activeSwitch = (Switch) convertView.findViewById(R.id.switch_scheduler_active);
            holder.daysButton = (Button) convertView.findViewById(R.id.button_days);
            holder.timeButton = (Button) convertView.findViewById(R.id.button_time);
            holder.deleteButton = (Button) convertView.findViewById(R.id.button_delete_schedule);
            holder.position = position;

            convertView.setTag(holder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("SchedulerListAdap", "Position " + position + " Active: " + scheduler);

        holder.messageView.setText(scheduler.getMessage());
        holder.activeSwitch.setChecked(scheduler.getActive());
        holder.daysButton.setText(scheduler.getDaysAsString());
        holder.timeButton.setText(scheduler.getTimeString());

        holder.messageView.setTag(position);
        holder.activeSwitch.setTag(position);
        holder.daysButton.setTag(position);
        holder.timeButton.setTag(position);
        holder.deleteButton.setTag(position);

        if (position == 0 && scheduler.getNotificationID() == -1) {
            holder.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(callback);
        }

        holder.activeSwitch.setOnClickListener(callback);
        holder.messageView.setOnFocusChangeListener(callback);
        holder.daysButton.setOnClickListener(callback);
        holder.timeButton.setOnClickListener(callback);

        return convertView;
    }
}

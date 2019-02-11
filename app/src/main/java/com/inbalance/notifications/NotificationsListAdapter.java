package com.inbalance.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.inbalance.inbalance.R;

import java.util.ArrayList;

public class NotificationsListAdapter extends ArrayAdapter {

    public int resourceLayout;
    private Context mContext;
    private ArrayList<Notification> notificationsList;
    private NotificationsListFragment callback;
    private NotificationsActivity activityCallback;

    public NotificationsListAdapter(Context context, int resource, ArrayList<Notification> notifications, NotificationsListFragment callback, NotificationsActivity activityCallback) {
        super(context, resource, notifications);
        this.resourceLayout = resource;
        this.mContext = context;
        this.notificationsList = notifications;
        this.callback = callback;
    }

   private static class ViewHolder {
        ImageView imageView;
        TextView nameView;
        Switch activeSwitch;
        Button deleteButton;
        LinearLayout itemLayout;
        int position;
    }

    @Override
    public int getCount() {
        return notificationsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.notificationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.notificationsList.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = notificationsList.get(position);

        ViewHolder holder;

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notification_item_layout, parent, false);

            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_icon);
            holder.nameView = (TextView) convertView.findViewById(R.id.textView_name);
            holder.activeSwitch = (Switch) convertView.findViewById(R.id.switch_active);
            holder.deleteButton = (Button) convertView.findViewById(R.id.button_delete_notification);
            holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.notification_item);
            holder.position = position;

            holder.activeSwitch.setOnClickListener(callback);
            holder.deleteButton.setOnClickListener(callback);
            holder.itemLayout.setOnClickListener(callback);

            convertView.setTag(holder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(notification.getCategoryImageID());
        holder.nameView.setText(notification.getName());
        holder.activeSwitch.setChecked(notification.getActive());

        holder.imageView.setTag(position);
        holder.nameView.setTag(position);
        holder.activeSwitch.setTag(position);
        holder.deleteButton.setTag(position);
        holder.itemLayout.setTag(position);

        return convertView;
    }
}

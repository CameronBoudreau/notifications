package com.inbalance.inbalance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.inbalance.notifications.NotificationsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onClickNotificationsButton(View view) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }
}

package com.perfectrem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import dependents.com.perfectrem.R;
import java.lang.NumberFormatException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        
        final FloatingActionButton floatingActionButton = (FloatingActionButton)
            findViewById(R.id.floating_action_button_add_alarm),
            min450 = (FloatingActionButton) findViewById(R.id.floating_action_button_450),
            min540 = (FloatingActionButton) findViewById(R.id.floating_action_button_540);
        min450.setVisibility(View.GONE);
        min540.setVisibility(View.GONE);
        
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonVisibility(min450);
                toggleButtonVisibility(min540);
            }
        });
        
        min450.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setAlarm((FloatingActionButton) v);
           }
        });
        
        min540.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setAlarm((FloatingActionButton) v);
           } 
        });
    }

    private void toggleButtonVisibility(FloatingActionButton v) {
        if (v.getVisibility() == View.VISIBLE) {
            v.setVisibility(View.GONE);
        } else if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void setAlarm(FloatingActionButton v) {
        Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.floating_action_button_450:
                calendar.add(Calendar.HOUR, 4);
                calendar.add(Calendar.MINUTE, 30);
                break;
            case R.id.floating_action_button_540:
                calendar.add(Calendar.HOUR, 6);
                break;
            default:
                // Two possible floating action buttons that can call this is min450 and min540.
                // This default case should not be executed. This is just for safeguarding.
                calendar.add(Calendar.HOUR, 6);
        }
        
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 105, intent,
            PendingIntent.FLAG_CANCEL_CURRENT);
        
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(AppCompatActivity.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        
        // Provides confirmation that the alarm has been set.
        final String notificationMsg = "The alarm is set.";
        Toast.makeText(getApplicationContext(), notificationMsg, Toast.LENGTH_LONG).show();
    }
}
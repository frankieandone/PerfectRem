package com.perfectrem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import dependents.com.perfectrem.R;
import java.lang.NumberFormatException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        
        final TextInputEditText textInputEditText = (TextInputEditText)
            findViewById(R.id.text_input_edit_text_hours_input);
        
        Button button = (Button) findViewById(R.id.button_start_alarm);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.add(Calendar.HOUR, Integer.parseInt(textInputEditText.getText().toString()));
                } catch (NumberFormatException ex) {
                    final int defaultHours = 6;
                    calendar.add(Calendar.HOUR, defaultHours);
                }
                
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 105, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
                
                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(AppCompatActivity.ALARM_SERVICE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });
    }
}
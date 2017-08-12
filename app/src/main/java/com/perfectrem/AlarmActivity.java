package com.perfectrem;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import dependents.com.perfectrem.R;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_layout);
        
        FloatingActionButton floatingActionButton = (FloatingActionButton)
            findViewById(R.id.floating_action_button_stop_alarm);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                mVibrator.cancel();
            }
        });
        
        triggerAlarm(null);
    }

    /**
     * 
     * @param soundFileUri if null, then the default ringtone will be used for the alarm.
     */
    private void triggerAlarm(Uri soundFileUri) {
        if (soundFileUri == null) {
            // Use default ringtone for alarms.
            soundFileUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.setDataSource(getApplicationContext(), soundFileUri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            vibrate();
        } catch (Exception ex) {
            Log.e(TAG, "Trouble playing sound.");
        }
    }

    private void vibrate() {
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (!mVibrator.hasVibrator()) {
            Log.e(TAG, "Device doesn't have a vibrator therefore it can't vibrate.");
            return;
        }
        // Vibrate.vibrate(long milliseconds) is deprecated in API level 26.
        // NOTE: This app targets API level 25 thus it should be fine to the ignore API level 26 case.
        if (Build.VERSION.SDK_INT < 26) {
            while (mMediaPlayer.isPlaying()) {
                mVibrator.vibrate(500);
            }
        }
    }
}
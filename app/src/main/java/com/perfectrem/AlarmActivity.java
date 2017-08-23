package com.perfectrem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import dependents.com.perfectrem.R;
import java.lang.Runnable;

public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;
    private Handler mChangeColorHandler;
    private Runnable mChangeColorRunnable;
    private Handler mIncreaseVolumeHandler;
    private VolumeIncrementRunnable mVolumeIncrementRunnable;
    // Arbitrary ID.
    private static final int NOTIFICATION_ID = 105;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity_layout);
        
        FloatingActionButton floatingActionButton = (FloatingActionButton)
            findViewById(R.id.floating_action_button_stop_alarm);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                if (mVibrator != null) {
                    mVibrator.cancel();
                }
                if (mChangeColorHandler != null && mChangeColorRunnable != null) {
                    mChangeColorHandler.removeCallbacks(mChangeColorRunnable);
                }
                if (mIncreaseVolumeHandler != null && mVolumeIncrementRunnable != null) {
                    mIncreaseVolumeHandler.removeCallbacks(mVolumeIncrementRunnable);
                }
                // Cancel the notification with ID: <NOTIFICATION_ID> after alarm has been stopped.
                NotificationManager notifManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
                notifManager.cancel(NOTIFICATION_ID);
                // Navigate back to the MainActivity after alarm has been stopped.
                Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                startActivity(intent);
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
            toggleBackgroundColor();
            mIncreaseVolumeHandler = new Handler();
            mVolumeIncrementRunnable =
            new VolumeIncrementRunnable((AudioManager) getSystemService(Context.AUDIO_SERVICE),
            mIncreaseVolumeHandler);
            triggerNotification();
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
        
        final long[] pattern = { 0, 750, 3000, 1000, 3000, 1250, 3000, 1250, 3000 };
        // Indicates to the mVibrator at which index (of the pattern) to loop the vibration effect.
        int repeatAtIndexOf = 0;
        // Vibrate.vibrate(long milliseconds) is deprecated in API level 26.
        // NOTE: This app targets API level 25 thus it should be fine to the ignore API level 26 case.
        if (Build.VERSION.SDK_INT < 26) {
            mVibrator.vibrate(pattern, repeatAtIndexOf);
        }
    }

    private void toggleBackgroundColor() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.alarm_activity_layout);
        final int mainColor = getResources().getColor(R.color.alarm_activity_background, null),
            secondaryColor = getResources().getColor(R.color.alarm_activity_secondary_background, null);
        mChangeColorHandler = new Handler();
        mChangeColorRunnable = new Runnable() {
            @Override
            public void run() {
                ColorDrawable viewColor = (ColorDrawable) linearLayout.getBackground();
                if (viewColor.getColor() == mainColor) {
                    linearLayout.setBackgroundColor(secondaryColor);
                } else {
                    linearLayout.setBackgroundColor(mainColor);
                }
                mChangeColorHandler.postDelayed(this, 1000);
            }
        };
        mChangeColorHandler.postDelayed(mChangeColorRunnable, 1000);
    }

    private void triggerNotification() {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(getApplicationContext())
            .setSmallIcon(R.mipmap.ic_alarm_add_white_48dp)
            .setContentTitle("PerfectRem")
            .setContentText("Alarm is running!")
            // Removes the notification after single-use.
            .setAutoCancel(true);
        
        Intent resultIntent = new Intent(this, AlarmActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 101, resultIntent , PendingIntent.FLAG_ONE_SHOT);
        notifBuilder.setContentIntent(resultPendingIntent);
        
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    class VolumeIncrementRunnable implements Runnable {
        private AudioManager mAudioManager;
        private Handler mHandler;
        private static final int INCREMENT_INTERVAL_MS = 5000;
        private int mIncrementsIndex;
        private final int[] INCREMENTS_ARR = { 1, 1, 2, 3, 5, 8, 13, 21 };
        private final int MAX_VOLUME;
    
        VolumeIncrementRunnable(AudioManager audioManager, Handler handler) {
            mAudioManager = audioManager;
            mHandler = handler;
            MAX_VOLUME = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        }
    
        @Override
        public void run() {
            int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            int nextVolume = INCREMENTS_ARR[mIncrementsIndex] + volume;
            if (mIncrementsIndex <= INCREMENTS_ARR.length && volume < MAX_VOLUME && nextVolume < MAX_VOLUME) {
                mIncrementsIndex++;
                // Android Studio complains that it can not find AudioManager.FLAG_FIXED_VOLUME.
                // Thus, 1 << 5 is used instead.
                mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, nextVolume, 1 << 5);
                mHandler.postDelayed(this, INCREMENT_INTERVAL_MS);
            }
        }
    }
}
package com.example.radioaktywne;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private AudioPlayerService mService;
    private boolean mBound = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mTimerHandler = new Handler();

    //UI
    private TextView rdsTextView;
    private ListView scheduleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            this.getSupportActionBar().hide();

        ListView listView = (ListView) findViewById(R.id.scheduleListView);
        final ArrayList<Program> arrayList = new ArrayList<>();
        arrayList.add(new Program("Spokojna wooda", "22:00 - 23:00", "Jakub Pyszczak, Jan Klamka, Marcin Lisiecki"));
        arrayList.add(new Program("Spokojna wooda", "22:00 - 23:00", "Jakub Pyszczak, Jan Klamka, Marcin Lisiecki"));
        arrayList.add(new Program("Spokojna wooda", "22:00 - 23:00", "Jakub Pyszczak, Jan Klamka, Marcin Lisiecki"));

        ProgramListAdapter adapter = new ProgramListAdapter(this, R.layout.adapter_view_layout, arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        rdsTextView = (TextView)findViewById(R.id.textView);
        scheduleListView = (ListView)findViewById(R.id.scheduleListView);

        Intent intent = new Intent(this, AudioPlayerService.class);
        Util.startForegroundService(this, intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startTimer() {
        mTimer = new Timer();

        initializeTimerTask();

        mTimer.schedule(mTimerTask, 1000, 100);
    }

    private void initializeTimerTask() {
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mTimerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBound) {
                            String RDS = Html.fromHtml(mService.getRDS()).toString();
                            rdsTextView.setText(RDS);
                        }
                    }
                });
            }
        };
    }

    @Override
    protected void onStop() {
        unbindService(connection);
        mBound = false;
        stopTimer();

        super.onStop();
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public void btnClicked(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void playClicked(View view) {
        if (mBound) {
            mService.play();
        }
    }

    public void pauseClicked(View view) {
        if (mBound) {
            mService.pausePlayer();
        }
    }
}

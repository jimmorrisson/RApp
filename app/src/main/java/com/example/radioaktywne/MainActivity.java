package com.example.radioaktywne;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.radioaktywne.ScheduleDownloadService.EXTRA_OUT_TXT;

public class MainActivity extends AppCompatActivity {
    private AudioPlayerService mService;
    private boolean mBound = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mTimerHandler = new Handler();

    //UI
    private TextView rdsTextView;
    private ListView scheduleListView;
    private ScheduleIntentServiceReceiver intentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null)
            this.getSupportActionBar().hide();

        intentReceiver = new ScheduleIntentServiceReceiver(this, new Handler(Looper.getMainLooper()));
    }

    @Override
    protected void onStart() {
        super.onStart();

        rdsTextView = (TextView)findViewById(R.id.textView);
        scheduleListView = (ListView)findViewById(R.id.scheduleListView);

        startService(new Intent(this, ScheduleDownloadService.class).putExtra(Intent.EXTRA_RESULT_RECEIVER, intentReceiver));

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

    public void onDownloadFinished(Serializable serializable) {
        HashMap<String, ArrayList<Program>> scheduleMap = (HashMap<String, ArrayList<Program>>) serializable;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        ArrayList<Program> arrayList = scheduleMap.get(DayToStringMapper.map(day));
        ProgramListAdapter adapter = new ProgramListAdapter(this, R.layout.adapter_view_layout, arrayList);
        scheduleListView.setAdapter(adapter);
    }

    class ScheduleIntentServiceReceiver extends ResultReceiver {
        private final MainActivity mainActivity;

        public ScheduleIntentServiceReceiver(MainActivity mainActivity, Handler handler) {
            super(handler);
            this.mainActivity = mainActivity;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultData.containsKey(EXTRA_OUT_TXT));
                mainActivity.onDownloadFinished(resultData.getSerializable(EXTRA_OUT_TXT));
        }
    }
}

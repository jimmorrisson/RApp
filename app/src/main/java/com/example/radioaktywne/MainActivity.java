package com.example.radioaktywne;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.radioaktywne.ProgramActivity.programDatetime;
import static com.example.radioaktywne.ProgramActivity.programDescription;
import static com.example.radioaktywne.ProgramActivity.programHost;
import static com.example.radioaktywne.ProgramActivity.programName;
import static com.example.radioaktywne.ScheduleDownloadService.EXTRA_OUT_TXT;

public class MainActivity extends AppCompatActivity {
    private AudioPlayerService mService;
    private boolean mBound = false;
    private Timer mTimerRDS;
    private Timer mTimerConnection;
    private TimerTask mTimerTaskRDS;
    private TimerTask mTimerTaskConnection;
    private Handler mTimerHandlerRDS = new Handler();
    private Handler mTimerHandlerConnection = new Handler();

    //UI
    private TextView rdsTextView;
    private TextView tv3;
    private ListView scheduleListView;
    private ScheduleIntentServiceReceiver intentReceiver;
    private ImageButton btnPlay;
    private Button btnSchedule;
    private ImageView logo;
    private Object AdapterView;

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
        btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnSchedule = (Button) findViewById(R.id.button);
        logo = (ImageView) findViewById(R.id.logoImageView);
        tv3 = (TextView) findViewById(R.id.textView3);

        startService(new Intent(this, ScheduleDownloadService.class).putExtra(Intent.EXTRA_RESULT_RECEIVER, intentReceiver));

        Intent intent = new Intent(this, AudioPlayerService.class);
        Util.startForegroundService(this, intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        if (height < 1600) {
//            logo.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startTimers() {
        startTimerRDS();
//        startTimerConnection();
    }

    private void startTimerConnection() {
        mTimerConnection = new Timer();
        mTimerConnection.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();
                    SocketAddress socketAddress = new InetSocketAddress("www.radioaktywne.pl", 80);
                    socket.connect(socketAddress, 100);
                    socket.close();
                    setGUIInteraction(true);
                } catch (IOException e) {
                    setGUIInteraction(false);
                }
            }
        }, 0, 1000);
    }

    private void startTimerRDS() {
        mTimerRDS = new Timer();

        initializeTimerTask();

        mTimerRDS.schedule(mTimerTaskRDS, 1000, 100);
    }

    private void initializeTimerTask() {
        mTimerTaskRDS = new TimerTask() {
            @Override
            public void run() {
                mTimerHandlerRDS.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mBound) {
                            String RDS = Html.fromHtml(mService.getRDS()).toString();
                            rdsTextView.setText(RDS);
//                            rdsTextView.measure(0, 0);
//                            int rdsTxtHeight = rdsTextView.getMeasuredHeight();
//                            rdsTextView.getLayoutParams().height = rdsTxtHeight;
                        }
                        updateBtnPlay();
                    }
                });
            }
        };
    }

    @Override
    protected void onStop() {
        unbindService(connection);
        mBound = false;
        stopTimers();

        super.onStop();
    }

    private void stopTimers() {
        stopTimerRDS();
//        stopTimerConnection();
    }

    private void stopTimerRDS() {
        if (mTimerRDS != null) {
            mTimerRDS.cancel();
            mTimerRDS.purge();
        }
    }

    private void stopTimerConnection() {
        if (mTimerConnection != null) {
            mTimerConnection.cancel();
            mTimerConnection.purge();
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
            updateBtnPlay();
        }
    }

    private void updateBtnPlay() {
        if (!mBound) {
            btnPlay.setImageResource(R.drawable.ic_pause_medium);
            return;
        }
        if (btnPlay != null) {
            if (mService.getPlayWhenReady()) {
                btnPlay.setImageResource(R.drawable.ic_pause_medium); //@android:drawable/ic_media_play
            } else {
                btnPlay.setImageResource(R.drawable.ic_play_medium);
            }
        }
    }

    private void setGUIInteraction(boolean enable) {
        if (btnPlay != null) {
            btnPlay.setClickable(enable);
        }
        if (btnSchedule != null) {
            btnSchedule.setClickable(enable);
        }
        if (mBound && !enable) {
            mService.pausePlayer();
        }
    }

    public void onDownloadFinished(Serializable serializable) {
        HashMap<String, ArrayList<Program>> scheduleMap = (HashMap<String, ArrayList<Program>>) serializable;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        ArrayList<Program> arrayList = scheduleMap.get(DayToStringMapper.map(day));
        ProgramListAdapterUpdated adapter = new ProgramListAdapterUpdated(this, R.layout.adapter_view_layout_updated, arrayList);
        if (scheduleListView != null) {
            scheduleListView.setAdapter(adapter);
            scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Program program = (Program) parent.getItemAtPosition(position);
                    if (program != null) {
                        Intent intent = new Intent(getBaseContext(), ProgramActivity.class);
                        intent.putExtra(programName, program.getName());
                        intent.putExtra(programDescription, program.getDescription() );
                        intent.putExtra(programDatetime, program.getDateTime());
                        intent.putExtra(programHost, program.getHost());
                        startActivity(intent);
                    }
                }
            });
        }
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

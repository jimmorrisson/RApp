package com.example.radioaktywne;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LogPrinter;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private ScheduleDownloadService mService;
    private boolean mBound = false;
    private int day = 0;
    private ArrayList<Program> arrayList;
    private ProgramListAdapter mAdapter;
    private ListView listView;
    private TextView dayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        listView = (ListView) findViewById(R.id.listView);
        dayTextView = (TextView) findViewById(R.id.textViewDay);

        arrayList = new ArrayList<>();
        arrayList.add(new Program("Gdzie Grabek mówi dzień dobry", "8:00 - 10:00", "Grabek"));
        arrayList.add(new Program("Aktywacja - powtórka", "10:00 - 11:30", ""));
        arrayList.add(new Program("Satisfaction - powtórka", "11:30 - 12:30", "Martyna Matwiejuk"));
        arrayList.add(new Program("Fast Forward Charts - powtórka", "12:30 - 13:30", "Konrad Maciąg"));
        arrayList.add(new Program("WeFUNK - powtórka", "13:30 - 14:30", "Aleksander Orłowski"));
        arrayList.add(new Program("#AMBIENTY - powtórka", "14:30 - 16:30", "Ania Pietrzak"));
        arrayList.add(new Program("Ukryte - powtórka", "16:30 - 18:30", "Michał Lisiewicz"));
        arrayList.add(new Program("Aktywacja", "18:30 - 20:00", ""));
        arrayList.add(new Program("Biuro Podróży", "20:00 - 21:00", "Tomasz Kubik"));
        arrayList.add(new Program("Biurko", "21:00 - 21:30", "Maciej Wałejko, Tomasz Kubik"));
        arrayList.add(new Program("Szuflada dźwięków", "21:30 - 22:30", "Maciej Wałejko"));
        arrayList.add(new Program("Atrocity Exhibition", "22:30 - 23:30", "Kuba Piątkowski"));

        mAdapter = new ProgramListAdapter(this, R.layout.adapter_view_layout, arrayList);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ProgramActivity.class);
                startActivity(intent);
            }
        });

        gestureDetector = new GestureDetector(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ScheduleDownloadService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Right or left swipe
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        }
        return result;
    }

    private void onSwipeLeft() {
        day = (day > 0) ? day - 1 : 6;
        setScheduleFromService();
    }

    private void onSwipeRight() {
        day = (day + 1) % 7;
        setScheduleFromService();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void btnRightClicked(View view) {
        onSwipeRight();
    }

    public void btnLeftClicked(View view) {
        onSwipeLeft();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ScheduleDownloadService.ScheduleDownloadBinder binder = (ScheduleDownloadService.ScheduleDownloadBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void setScheduleFromService() {
        if (mBound) {
            HashMap<String, ArrayList<Program>> scheduleMap = mService.getSchedule();
            if (scheduleMap == null) {
                return;
            }
            if (mAdapter == null) {
                mAdapter = new ProgramListAdapter(this, R.layout.adapter_view_layout, scheduleMap.get("1"));
                listView.setAdapter(mAdapter);
            } else {
                if (scheduleMap.containsKey(Integer.toString(day))) {
                    ArrayList<Program> dailyProgram = scheduleMap.get(Integer.toString(day));
                    if (dailyProgram == null)
                        return;
                    arrayList.clear();
                    arrayList.addAll(dailyProgram);
                    mAdapter.notifyDataSetChanged();
                    setCurrentDayText();
                    Log.d("ScheduleActivity: ", "Day: " + day);
                }
            }
        }
    }

    private void setCurrentDayText() {
        switch (day) {
            case 0:
                dayTextView.setText(R.string.monday);
                break;
            case 1:
                dayTextView.setText(R.string.tuesday);
                break;
            case 2:
                dayTextView.setText(R.string.wednesday);
                break;
            case 3:
                dayTextView.setText(R.string.thursday);
                break;
            case 4:
                dayTextView.setText(R.string.friday);
                break;
            case 5:
                dayTextView.setText(R.string.saturday);
                break;
            case 6:
                dayTextView.setText(R.string.sunday);
                break;
        }
    }
}

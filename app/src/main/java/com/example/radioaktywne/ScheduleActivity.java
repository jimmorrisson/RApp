package com.example.radioaktywne;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {
    private float x1;
    private float x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        final ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<Program> arrayList = new ArrayList<>();
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

        ProgramListAdapter adapter = new ProgramListAdapter(this, R.layout.adapter_view_layout, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ProgramActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
            break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (x2 > x1) {
                        Toast.makeText(this, "Left to Right swipe", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Right to left swipe", Toast.LENGTH_SHORT).show();
                    }
                }
            break;
        }

        return super.onTouchEvent(event);
    }
}

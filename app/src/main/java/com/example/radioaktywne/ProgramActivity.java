package com.example.radioaktywne;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class ProgramActivity extends AppCompatActivity {
    public static final String programDescription = "DESCRIPTION";
    public static final String programName = "NAME";
    public static final String programDatetime = "DATETIME";
    public static final String programHost = "HOST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        if (getSupportActionBar() != null)
            this.getSupportActionBar().hide();

        TextView tvProgramDescription = (TextView) findViewById(R.id.tvProgramDescription);
        TextView tvProgramName = (TextView) findViewById(R.id.tvProgramName);
        TextView tvProgramDatetime = (TextView) findViewById(R.id.tvProgramDateTime);
        TextView tvProgramHost = (TextView) findViewById(R.id.tvProgramHosts);

        String name = getIntent().getStringExtra(this.programName);
        String datetime = getIntent().getStringExtra(this.programDatetime);
        String description = getIntent().getStringExtra(this.programDescription);
        String host = getIntent().getStringExtra(this.programHost);

        if (name != null && tvProgramName != null) {
            tvProgramName.setText(name);
        }

        if (datetime != null && tvProgramDatetime != null) {
            tvProgramDatetime.setText(datetime);
        }

        if (description != null && tvProgramDescription != null) {
            tvProgramDescription.setText(description);
            tvProgramDescription.setMovementMethod(new ScrollingMovementMethod());
        }

        if (host != null && tvProgramHost != null) {
            tvProgramHost.setText(host);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
    }

    public void backClicked(View view) {
        finish();
    }
}

package com.example.radioaktywne;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProgramActivity extends AppCompatActivity {
    public static final String programDescription = "DESCRIPTION";
    public static final String programName = "NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        if (getSupportActionBar() != null)
            this.getSupportActionBar().hide();

        TextView textViewProgramName = (TextView) findViewById(R.id.program_name);
        TextView textViewDescription = (TextView) findViewById(R.id.program_description);

        String programName = getIntent().getStringExtra(this.programName);
        String description = getIntent().getStringExtra(this.programDescription);

        if (programName != null && textViewProgramName != null) {
            textViewProgramName.setText(programName);
        }

        if (description != null && textViewDescription != null) {
            textViewDescription.setText(description);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
    }
}

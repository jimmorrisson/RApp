package com.example.radioaktywne;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProgramListAdapterUpdated extends ArrayAdapter<Program> {
    private static final String TAG = "ProgramListAdapterUpdated";
    private Context mContext;
    private int mResource;

    public ProgramListAdapterUpdated(Context context, int resource, ArrayList<Program> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String dateTime = getItem(position).getDateTime();
        String host = getItem(position).getHost();
        String description = getItem(position).getDescription();

        Program program = new Program(name, dateTime, host, description);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewProgramName = (TextView) convertView.findViewById(R.id.tvProgramName);
        TextView textViewProgramDateTime = (TextView) convertView.findViewById(R.id.tvProgramDateTime);

        textViewProgramDateTime.setText(dateTime);
        textViewProgramName.setText(name);

        return  convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

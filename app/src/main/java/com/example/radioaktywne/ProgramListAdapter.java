package com.example.radioaktywne;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProgramListAdapter extends ArrayAdapter<Program> {
    private static final String TAG = "ProgramListAdapter";
    private Context mContext;
    private int mResource;

    public ProgramListAdapter(Context context, int resource, ArrayList<Program> objects) {
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

        Program program = new Program(name, dateTime, host);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewName = (TextView) convertView.findViewById(R.id.textView1);
        TextView textViewDateTime = (TextView) convertView.findViewById(R.id.textView2);
        TextView textViewHost = (TextView) convertView.findViewById(R.id.textView3);

        textViewName.setText(name);
        textViewDateTime.setText(dateTime);
        textViewHost.setText(host);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

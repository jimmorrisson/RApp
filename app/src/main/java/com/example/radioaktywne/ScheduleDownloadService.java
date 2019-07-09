package com.example.radioaktywne;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ScheduleDownloadService extends IntentService {
    private HashMap<String, ArrayList<Program>> mScheduleMap;
    private String programUrl = "http://34.244.107.237:8080/";

    private final IBinder mBinder = new ScheduleDownloadBinder();
    public static final String EXTRA_OUT_TXT = "hashMap";
    public ScheduleDownloadService() {
        super("ScheduleDownloadService");
    }

    public class ScheduleDownloadBinder extends Binder {
        ScheduleDownloadService getService() {
            return ScheduleDownloadService.this;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER);
            Bundle bundle = new Bundle();
            try {
                URL url = new URL(programUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject mainObj = new JSONObject(convertStreamToString(in));
                if (mScheduleMap == null) {
                    mScheduleMap = new HashMap<>();
                } else {
                    mScheduleMap.clear();
                }
                for (int i = 0; i < 7; i++) {
                    JSONArray jsonDay = mainObj.getJSONArray(Integer.toString(i));

                    ArrayList<Program> arrayList = new ArrayList<>();
                    for (int j = 0; j < jsonDay.length(); j++) {
                        JSONObject jsonProgram = jsonDay.getJSONObject(j);
                        String name = jsonProgram.getString("name");
                        String hours = jsonProgram.getString("hours");
                        String host = jsonProgram.getString("speakers");
                        arrayList.add(new Program(name, hours, host));
                    }

                    mScheduleMap.put(Integer.toString(i), arrayList);
                }
                bundle.putSerializable(EXTRA_OUT_TXT, mScheduleMap);
                receiver.send(0, bundle);
                Log.d("ScheduleDownloadService", mainObj.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public HashMap<String, ArrayList<Program>> getSchedule() {
        Log.d("ScheduleDownloadService", "getSchedule() called");
        return mScheduleMap;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}

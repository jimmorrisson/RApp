package com.example.radioaktywne;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

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

public class AudioPlayerService extends Service {
    private SimpleExoPlayer player;
    private Uri playerUri = Uri.parse("http://listen.radioaktywne.pl:8000/ramp3");
    private PlayerNotificationManager playerNotificationManager;
    private String PLAYBACK_CHANNEL_ID = "playbackId";
    private int PLAYBACK_NOTIFICATION_ID = 12;
    private boolean playerStarted = false;
    private String RDS = "Trwa łączenie";
    private String contentText = "Promieniujemy najlepszą muzyką";
    AudioAttributes audioAttributes;

    private final IBinder binder = new LocalBinder();
    private MusicIntentReceiver musicIntentReceiver;

    public class LocalBinder extends Binder {
        AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    private class MusicIntentReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d("HEADSET", "Headset is unplugged");
                        break;
                    case 1:
                        Log.d("HEADSET", "Headset is plugged");
                        break;
                    default:
                        Log.d("HEADSET", "I have no idea what the headset state is");
                }
            }
        }
    }

    public void pausePlayer() {
        if (player == null) {
            return;
        }
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    public void play() {
        if (player == null) {
            return;
        }
        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
    }

    public boolean getPlayWhenReady() {
        if (player == null) {
            return false;
        }
        return player.getPlayWhenReady();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();
        }
//        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        player = new SimpleExoPlayer.Builder(context).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "Audio"));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(playerUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(false);
        player.setAudioAttributes(audioAttributes, true);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context, PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, 0, PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, MainActivity.class);
                        return PendingIntent.getActivity(context, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return contentText;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return BitmapFactory.decodeResource(context.getResources(), R.drawable.raicon);
                    }
                }, new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        stopSelf();
                    }

                    @Override
                    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                        startForeground(notificationId, notification);
                    }
                });

        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setUseChronometer(false);
        playerNotificationManager.setUseStopAction(true);

        playerStarted = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRDSHandler();
            }
        }).start();

        musicIntentReceiver = new MusicIntentReceiver();
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(musicIntentReceiver, filter);
    }

    private void startRDSHandler() {
        while (playerStarted) {
            try {
                Thread.sleep(100);
                try {
                    URL url = new URL("http://listen.radioaktywne.pl:8000/status-json.xsl");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JSONObject mainObj = new JSONObject(convertStreamToString(in));
                    JSONObject icestatsObj = mainObj.getJSONObject("icestats");
                    JSONObject sourceObj = icestatsObj.getJSONArray("source").getJSONObject(1);
                    RDS = sourceObj.getString("title");
                    urlConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRDSHandler() {
        playerStarted = false;
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

    @Override
    public void onDestroy() {
        player.setPlayWhenReady(false);
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;
        stopRDSHandler();
        unregisterReceiver(musicIntentReceiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    public String getRDS() {
        return RDS;
    }
}

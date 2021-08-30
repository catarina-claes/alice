package com.example.alice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Alice extends Service {

    public static boolean active = false;
    private WindowManager mWindowManager;
    private View mView;
    private InputMethodManager imm;
    private MediaPlayer mediaPlayer;
    private EditText editText;

    public Alice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            myOwnForeground();
        else
            startForeground(1, new Notification());

        active = true;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        floatingWidget(inflater);
    }

    public void floatingWidget(LayoutInflater inflater) {

        //ngeubuat view
        mView = inflater.inflate(R.layout.popup_window, null);

        //parameter window 1
        WindowManager.LayoutParams mParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mParam.gravity = Gravity.TOP | Gravity.END;
        mParam.y = 37;

        //parameter window 2
        WindowManager.LayoutParams mParam2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                0,
                PixelFormat.TRANSLUCENT
        );
        mParam2.gravity = Gravity.TOP | Gravity.END;
        mParam2.y = 37;

        editText = mView.findViewById(R.id.editTextTextPersonName);
        editText.requestFocus();

        //add and display window
        mWindowManager.addView(mView, mParam);

        mView.findViewById(R.id.imageView2).setOnClickListener(view -> {
            if (mView.findViewById(R.id.relative_root).getVisibility() == View.GONE) {
                mView.findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.relative_root).setVisibility(View.VISIBLE);
                editText.setText("");
                mWindowManager.updateViewLayout(mView, mParam2);

                //nunggu windowManagernya ngeupdate windownya dl dah
                mView.postDelayed(() -> imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0), 200);
            }
            else {
                mView.findViewById(R.id.imageView1).setVisibility(View.GONE);
                mView.findViewById(R.id.relative_root).setVisibility(View.GONE);
                mWindowManager.updateViewLayout(mView, mParam);
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
            }
        });

        mView.findViewById(R.id.imageView1).setOnClickListener(View -> {

            //take the player
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //set the header, and the url
            Map<String, String> header = new HashMap<>();
            String text = editText.getText().toString()
            .replaceAll(" ","%20")
            .replaceAll(",", "%2C")
            .replaceAll("\"","%22")
            .replaceAll("/", "%2F")
            .replaceAll("=","%3D");
            String url = "https://voicerss-text-to-speech.p.rapidapi.com/?key=0a8adb54548541a2a09d40cfa06800de&hl=en-us&src="+ text +"&f=8khz_8bit_mono&c=aac&r=0";
            header.put("x-rapidapi-host", "voicerss-text-to-speech.p.rapidapi.com");
            header.put("x-rapidapi-key", "3b18b63be3mshf3e29579172b51cp115d39jsn4d40dbf7cc95");

            //add the the request to the mediaPlayer and play it
            try {
                mediaPlayer.setDataSource(this, Uri.parse(url), header);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void myOwnForeground() {

        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Alice's running")
                .setContentText("we are in sweet home!")
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mView);
        active = false;
    }
}
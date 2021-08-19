package com.example.alice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
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

public class Alice extends Service {

    public static boolean active = false;
    private WindowManager mWindowManager;
    private View mView;
    private LayoutInflater inflater;
    private InputMethodManager imm;

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
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
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
        mParam.gravity = Gravity.TOP | Gravity.RIGHT;
        mParam.y = 37;

        //parameter window 2
        WindowManager.LayoutParams mParam2 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                0,
                PixelFormat.TRANSLUCENT
        );
        mParam2.gravity = Gravity.TOP | Gravity.RIGHT;
        mParam2.y = 37;

        EditText editText = (EditText) mView.findViewById(R.id.editTextTextPersonName);
        editText.requestFocus();

        //add and display window
        mWindowManager.addView(mView, mParam);

        mView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mView.findViewById(R.id.relative_root).getVisibility() == View.GONE) {
                    mView.findViewById(R.id.relative_root).setVisibility(View.VISIBLE);
                    editText.setText("");
                    mWindowManager.updateViewLayout(mView, mParam2);

                    //nunggu windowManagernya ngeupdate windownya dl dah
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    }, 200);
                }
                else {
                    mView.findViewById(R.id.relative_root).setVisibility(View.GONE);
                    mWindowManager.updateViewLayout(mView, mParam);
                    imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                }
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
                .setSmallIcon(R.mipmap.touho)
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
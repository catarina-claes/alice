package com.example.alice;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Activity context;

    @Override
    protected void onCreate(Bundle SavedInstanceSaved) {
        super.onCreate(SavedInstanceSaved);
        context = this;

        this.setContentView(R.layout.activity_main);

        this.findViewById(R.id.button).setOnClickListener(view -> {
            if (!Alice.active) {
                startService();
            } else
                stopService(new Intent(context, Alice.class));
        });

        checkPermission();
        startService();
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
            }
        }
    }

    public void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, Alice.class));
                } else {
                    startService(new Intent(this, Alice.class));
                }
            }
        } else {
            startService(new Intent(this, Alice.class));
        }
    }

}

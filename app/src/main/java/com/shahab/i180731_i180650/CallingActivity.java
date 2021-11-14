package com.shahab.i180731_i180650;

import androidx.appcompat.app.AppCompatActivity;

import static androidx.core.app.ActivityCompat.requestPermissions;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class CallingActivity extends AppCompatActivity {
    Button btn_videoCall, btn_call_disconnect;

    // Fill the App ID of your project generated on Agora Console.
    private String appId = "b6d46e33d71040658795fc6a038133d1";
    // app certificate
    // 9ee1afce77654eda95fef70619aec6a7
    // Fill the channel name.
    private String channelName = "BeyondTheWall";
    // Fill the temp token generated on Agora Console.
    private String token = "006b6d46e33d71040658795fc6a038133d1IAANWyV1YIxoFzsKGvrPa9eX36XFH/aaST+rL1F4cKVtMfyZu/cAAAAAEABr21wCmRKSYQEAAQCYEpJh";
    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
    };

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calling);

        btn_videoCall = findViewById(R.id.calling_videocall);
        btn_videoCall.setOnClickListener(view -> launchVideoCall());

        btn_call_disconnect = findViewById(R.id.calling_disconnect);
        btn_call_disconnect.setOnClickListener(view -> disconnectCall());

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }
    }

    private void disconnectCall() {
        Toast.makeText(this, "Call disconnected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    private void launchVideoCall() {
        Toast.makeText(this, "Video call started", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, VideoCallActivity.class);
        startActivity(intent);
    }


    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error");
        }
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }

    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}
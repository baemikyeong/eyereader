package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;
import com.example.mygirlfriend.action_navigator.eyetoggle.event.LeftEyeClosedEvent;
import com.example.mygirlfriend.action_navigator.eyetoggle.event.NeutralFaceEvent;
import com.example.mygirlfriend.action_navigator.eyetoggle.event.RightEyeClosedEvent;
import com.example.mygirlfriend.action_navigator.eyetoggle.tracker.FaceTracker;
import com.example.mygirlfriend.action_navigator.eyetoggle.util.PlayServicesUtil;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class WebActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERM = 69;      // 카메라 퍼미션을 위한 코드
    private int[] location = new int[2];                    // 사용자가 현재 보고 있는 화면의 위치 저장
    private FaceDetector mFaceDetector;                     // 얼굴 인식
    private CameraSource mCameraSource;                     // 카메라 객체
    private FaceTracker face_tracker;                       // 눈 파악
    private double left_thres = 0;                          // 사용자의 초기값
    private double right_thres = 0;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.webview_main);

        PlayServicesUtil.isPlayServicesAvailable(this, 69);

        // permission granted...?
        if (isCameraPermissionGranted()) {
            // ...create the camera resource
            createCameraResources();
        } else {
            // ...else request the camera permission
            requestCameraPermission();
        }
/*
        // 사용자가 화면을 터치하여 스크롤 뷰의 위치 변경시, 체크
        webView.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                webView.getLocationOnScreen(location);
                return false;
            }
        });
*/
    }

    public void goURL(View view){
        TextView tvURL = (TextView)findViewById(R.id.txtURL);
        String url = tvURL.getText().toString();
        Log.i("URL","Opening URL :"+url);

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl(url);

    }

    //눈깜박임에 따른 페이지 down 함수
    public void change_down_location(){
        // 절대값을 통해 text뷰의 스크롤뷰에서의 위치 파악
        if(location[1] < 0)
            location[1] = (-1)*location[1];

        // 위치 변경
        webView.scrollTo(0, location[1]+60);
        location[1] += 60;

    }

    // 눈깜박임에 따른 페이지 up 함수
    public void change_up_location(){
        // 절대값을 통해 text뷰의 스크롤뷰에서의 위치 파악
        if(location[1] < 0)
            location[1] = (-1)*location[1];
        // 기존의 위치에서 60 이동
        webView.scrollTo(0, location[1]-60);
        location[1] -= 60;
    }

    /**
     * Check camera permission
     *
     * @return <code>true</code> if granted
     */
    private boolean isCameraPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request the camera permission
     */
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraResources();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EyeControl")
                .setMessage("No camera permission")
                .setPositiveButton("Ok", listener)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register the event bus
        EventBus.getDefault().register(this);

        // start the camera feed
        if (mCameraSource != null && isCameraPermissionGranted()) {
            try {
                //noinspection MissingPermission
                mCameraSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //   Log.e(TAG, "onResume: Camera.start() error");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister from the event bus
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        // stop the camera source
        if (mCameraSource != null) {
            mCameraSource.stop();
        } else {
            //    Log.e(TAG, "onPause: Camera.stop() error");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // release them all...
        if (mFaceDetector != null) {
            mFaceDetector.release();
        } else {
            // Log.e(TAG, "onDestroy: FaceDetector.release() error");
        }
        if (mCameraSource != null) {
            mCameraSource.release();
        } else {
            // Log.e(TAG, "onDestroy: Camera.release() error");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLeftEyeClosed(LeftEyeClosedEvent e) {
        change_down_location();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRightEyeClosed(RightEyeClosedEvent e) {
        // change_up_location();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNeutralFace(NeutralFaceEvent e) {

        change_down_location();

    }

    private void createCameraResources() {
        Context context = getApplicationContext();

        // create and setup the face detector
        mFaceDetector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build();

        // now that we've got a detector, create a processor pipeline to receive the detection
        // results
        mFaceDetector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType(/* eyes open and smile */ FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build();

        // now that we've got a detector, create a processor pipeline to receive the detection
        // results

        mFaceDetector.setProcessor(new LargestFaceFocusingProcessor(mFaceDetector, face_tracker = new FaceTracker()));

        if(left_thres == 0 && right_thres == 0){

        }
        else{
            Toast.makeText(this, "개인화가 이미 되어 있습니다.", Toast.LENGTH_SHORT).show();
            face_tracker.set_indi(left_thres, right_thres);
        }



        // operational...?
        if (!mFaceDetector.isOperational()) {
            //  Log.w(TAG, "createCameraResources: detector NOT operational");
        } else {
            //   Log.d(TAG, "createCameraResources: detector operational");
        }

        // Create camera source that will capture video frames
        // Use the front camera
        mCameraSource = new CameraSource.Builder(this, mFaceDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30f)
                .build();
    }
}

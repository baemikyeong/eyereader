package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;
import com.google.android.gms.vision.CameraSource;

//import static android.support.v4.app.ActivityCompatJB.startActivityForResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CameraSource mCameraSource;
    private double left_thres = 0.0;
    private double right_thres = 0.0;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    boolean permissionToRecordAccepted = false;
    String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private SharedPreferences intPref;//이거
    private SharedPreferences.Editor editor1; //이거
    boolean light;//초기상태는 불이 꺼진 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intPref = getSharedPreferences("mPred", Activity.MODE_PRIVATE);//이거
        editor1 = intPref.edit();//이거
        float LeftV = intPref.getFloat("LValue",0);
        float RightV = intPref.getFloat("RValue", 0);
        float blink_time = intPref.getLong("time_blink", 0);
        boolean light = false; //초기상태는 불이 꺼진 상태

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_light) {

            Intent service = new Intent( this, ScreenFilterService.class );
            if(Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                }
                if (!light) {
                    startService(service); //false면 불을 킨다
                    light = true;
                } else {
                    stopService(service);
                    light = false;
                }
            }

        } else if (id == R.id.action_bookmark) {

        } else if(id == R.id.action_plus){
            Intent intent = new Intent(this, FileListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_mic) {
            Intent intent = new Intent(this, AudioService.class);
            // Requesting permission to RECORD_AUDIO

            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

         if(isRecording == false) {
            startService(intent);
            isRecording = true;
        }
        else{
            stopService(intent);
            isRecording = false;
        }


        } else if (id == R.id.action_initialize) {
            Intent intent = new Intent(MainActivity.this, FaceTrackerActivity.class);

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            Toast.makeText(this, "초기화를 시작합니다", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "눈을 감고 Blink_Size 버튼을 두 번 눌러주세요", Toast.LENGTH_SHORT).show();

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    //네비게이터를 클릭했을 때 일어날 명령들 여기다가 추가!
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_document) {
            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }
            Intent intent = new Intent(this, Textview_activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_bookmark) {
            Intent intent = new Intent(this, BookmarkActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_webview) {

            //Intent intent = new Intent(this, WebActivity.class);
            Intent intent = new Intent(this, NewWebView_Activity.class);

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }


            startActivity(intent);

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_voicmemo) {

        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

}

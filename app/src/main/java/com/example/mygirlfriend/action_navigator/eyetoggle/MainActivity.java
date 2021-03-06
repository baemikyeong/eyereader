package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;
import com.google.android.gms.vision.CameraSource;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static com.example.mygirlfriend.action_navigator.R.id.subMenu_medium;

//import static android.support.v4.app.ActivityCompatJB.startActivityForResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CameraSource mCameraSource;
    private double left_thres = 0.0;
    private double right_thres = 0.0;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    boolean permissionToRecordAccepted = false;
    String [] permissions = {RECORD_AUDIO};

    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    private SharedPreferences intPref;
    private SharedPreferences.Editor editor1;
    boolean light;//초기상태는 불이 꺼진 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intPref = getSharedPreferences("mPred", Activity.MODE_PRIVATE);//이거
        editor1 = intPref.edit();
        float LeftV = intPref.getFloat("LValue",0);
        float RightV = intPref.getFloat("RValue", 0);
        float blink_time = intPref.getLong("time_blink", 0);
        boolean light = false;

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

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("블링클링");
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //getActionBar().setTitle("BLINK KLING");

        int id = item.getItemId();

        switch(id){
            case R.id.action_settings :
                return true;

            case R.id.action_light :
                Intent service = new Intent( this, ScreenFilterService.class );
                if(Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    }
                    if (!light) {
                        startService(service);
                        light = true;
                    } else {
                        stopService(service);
                        light = false;
                    }
                }
                break;

            case R.id.action_bookmark :
                break;

            case R.id.action_plus :
                Intent intent = new Intent(this, FileListActivity.class);
                startActivity(intent);

                break;

            case R.id.action_mic :
                intent = new Intent(this, AudioService.class);

                // Requesting permission to RECORD_AUDIO

                //ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
                checkPermission(RECORD_AUDIO);

                if(isRecording == false) {
                    startService(intent);
                    isRecording = true;
                }
                else{
                    stopService(intent);
                    isRecording = false;
                }

                break;

            case R.id.action_initialize :
                intent = new Intent(MainActivity.this, FaceTrackerActivity.class);

                if (mCameraSource != null) {
                    mCameraSource.release();
                    mCameraSource = null;
                }

                Toast.makeText(this, "초기화를 시작합니다", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "눈을 감고 Blink_Size 버튼을 두 번 눌러주세요", Toast.LENGTH_SHORT).show();

                startActivity(intent);
                break;

            case R.id.subMenu_Large :
                if(!item.isChecked()) {
                    item.setChecked(true);

                }
                break;

            case subMenu_medium:
                if(!item.isChecked())
                    item.setChecked(true);
                break;

            case R.id.subMenu_small :
                if(!item.isChecked())
                    item.setChecked(true);
                break;

        }


        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected 메서드 종료



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





    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(String requestCode) {
       // Log.i(TAG, "CheckPermission : " +  ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE));
        switch (requestCode){
            case READ_EXTERNAL_STORAGE:
                if (checkSelfPermission(READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to write the permission.
                        Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                    }

                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_STORAGE);

                    // MY_PERMISSION_REQUEST_STORAGE is an
                    // app-defined int constant

                } else {
                    //실행

                }


            case RECORD_AUDIO :
                if (checkSelfPermission(RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){


                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
                        // Explain to the user why we need to write the permission.
                        Toast.makeText(this, "Record audio", Toast.LENGTH_SHORT).show();
                    }

                    requestPermissions(new String[]{RECORD_AUDIO, android.Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSION_REQUEST_STORAGE);

                    // MY_PERMISSION_REQUEST_STORAGE is an
                    // app-defined int constant

                } else {
                    //실행

                }


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    break;

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);


        return true;

    }




}

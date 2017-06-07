package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;
import com.example.mygirlfriend.action_navigator.eyetoggle.tracker.FaceTracker;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CameraSource mCameraSource;
    private double left_thres = 0.0;
    private double right_thres = 0.0;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 초기화 버튼을 통해 사용자가 값을 입력한 경우 그 값을 저장
        try {
            Intent intent = this.getIntent();
            if (intent != null) {
                left_thres = intent.getExtras().getDouble("Left_thred");
                right_thres = intent.getExtras().getDouble("Right_thred");
                Toast.makeText(this, left_thres + " " + right_thres, Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

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
            boolean light = false;//초기상태는 불이 꺼진 상태
            Intent service = new Intent( this, ScreenFilterService.class );
            if (!light) {
                startService(service);//false면 불을 킨다
                light = true;
            } else {
                stopService(service);
                light = false;
            }
        } else if (id == R.id.action_bookmark) {

        } else if (id == R.id.action_mic) {

            if(isRecording == false) {
                startService(new Intent(this, AudioService.class));
                isRecording = true;
            }
            else{
                stopService(new Intent(this,AudioService.class));
                isRecording = false;
            }

        } else if (id == R.id.action_initialize) {
            Intent intent = new Intent(MainActivity.this, FaceTrackerActivity.class);

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            Toast.makeText(this, "초기화를 시작합니다", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Initialize 버튼을 누르고 약 3초간 눈을 감아주세요", Toast.LENGTH_SHORT).show();

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
            Intent intent = new Intent(this, Textview_activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_bookmark) {

        } else if (id == R.id.nav_webview) {
            Intent intent = new Intent(this, WebActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_voicmemo) {

        }
        return true;
    }



}

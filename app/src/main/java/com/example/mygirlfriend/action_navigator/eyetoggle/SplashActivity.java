package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by mac on 2017. 6. 5..
 */

public class SplashActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(1000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
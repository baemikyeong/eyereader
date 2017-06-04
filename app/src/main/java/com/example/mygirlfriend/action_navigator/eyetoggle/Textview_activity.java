package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Textview_activity extends AppCompatActivity {

   private TextView helloTxt;
    private ScrollView scrollView;
    private int cnt=0;
    private int lineHeight;
    private int scrollViewHeight;
    private int visibleTextLineCount;
    private int itemPosition;
    private int x;
    private int[] location = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);

        helloTxt = (TextView) findViewById(R.id.hellotxt);
        helloTxt.setText(readTxt());
        scrollView = (ScrollView) findViewById(R.id.scroll_text);

        scrollView.setOnTouchListener(new View.OnTouchListener(){   //터치 이벤트 리스너 등록(누를때와 뗐을때를 구분)

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(scrollView.getClass()==v.getClass()){
                        change_location();
                    }
                }

                return true;
            }
        });

    }


    private String readTxt() {

        InputStream inputStream = getResources().openRawResource(R.raw.mytext);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }


    public void change_location(){

        helloTxt.getLocationOnScreen(location);
        if(location[1] <= 0)
            location[1] = (-1)*location[1];
        if(location[1]+1000 <= helloTxt.getBottom())
            scrollView.scrollTo(0, location[1]+1000);
        else
            Toast.makeText(this,"더 이상 못내려가요",Toast.LENGTH_SHORT).show();

    }

}








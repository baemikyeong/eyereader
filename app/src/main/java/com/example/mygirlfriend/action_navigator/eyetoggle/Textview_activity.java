package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.mygirlfriend.action_navigator.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Textview_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);

        helloTxt.setText(readTxt());
       // ScrollView scrollView = (ScrollView)findViewById(R.id.)

        Button btn = (Button) findViewById(R.id.button3);
      //  btn.setOnClickListener(mClickListener);



    }


    TextView helloTxt = (TextView) findViewById(R.id.hellotxt);


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


   /* Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            //이곳에 버튼 클릭시 일어날 일을 적습니다.
            int lineHeight = helloTxt.getLineHeight();
            int itemPosition = 10;
            scrollView.scrollTo(0, lineHeight * itemPosition);





        }
    };
*/

}








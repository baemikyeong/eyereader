package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.webview_main);

//        getActionBar().setTitle("Titleeeeee");
  //      getActionBar().setIcon(R.drawable.ic_action_bookmark);
    }

    public void goURL(View view){
        TextView tvURL = (TextView)findViewById(R.id.txtURL);
        String url = tvURL.getText().toString();
        Log.i("URL","Opening URL :"+url);

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl(url);

    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){

            case R.id.action_bookmark:
                Toast.makeText(this,"bookmark is clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_light:
                Toast.makeText(this,"light is clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    */


}

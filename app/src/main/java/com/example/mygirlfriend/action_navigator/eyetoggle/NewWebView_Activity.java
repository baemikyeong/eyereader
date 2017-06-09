package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mygirlfriend.action_navigator.R;

public class NewWebView_Activity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar mPBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_new_web_view_);

        webView = (WebView) findViewById(R.id.webView1);
        mPBar = (ProgressBar) findViewById(R.id.progress01);
        WebSettings set = webView.getSettings();


        webView.getSettings().setJavaScriptEnabled(true);
       // webView.loadUrl("http://newhouse.tistory.com/"); // 보여주고자 하는 주소
        goURL(webView);


        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setSupportZoom(false);


        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if (progress < 100)

                {

                    mPBar.setVisibility(ProgressBar.VISIBLE);

                } else if (progress == 100)

                {

                    mPBar.setVisibility(ProgressBar.GONE);

                }

                mPBar.setProgress(progress);

            }


        });
    }

    public void goURL(View view){
        TextView tvURL = (TextView)findViewById(R.id.txtURL);
        String url = tvURL.getText().toString();
        Log.i("URL","Opening URL :"+url);

      //  WebView webView = (WebView)findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl(url);

    }




    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (webView.canGoBack()) {

                webView.goBack();

            } else {

                webView.clearCache(false);

                finish();

            }

            return true;

        }

        return super.onKeyDown(keyCode, event);


    }


private class WebClient extends WebViewClient {

    @Override

    public boolean shouldOverrideUrlLoading(WebView view, String url) {


        if (url.startsWith("sms:")) {

            Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));

            startActivity(i);

            return true;

        }


        if (url.startsWith("kakaolink:")) {

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            startActivity(i);

            return true;

        }


        if (url.startsWith("tel")) {

            Intent i = new Intent(Intent.ACTION_DIAL);

            i.setData(android.net.Uri.parse(url));

            startActivity(i);


        } else {

            view.loadUrl(url);


        }


        return true;


    }
}

}

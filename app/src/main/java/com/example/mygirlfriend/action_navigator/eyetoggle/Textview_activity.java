package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mygirlfriend.action_navigator.R;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Textview_activity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERM = 69;      // 카메라 퍼미션을 위한 코드
    private TextView helloTxt;                              // 텍스트 뷰를 띄워줄 뷰
    private ScrollView scrollView;                          // 텍스트 뷰를 스크롤 뷰를 이용해 화면에 출력
    private int[] location = new int[2];                    // 사용자가 현재 보고 있는 화면의 위치 저장
    private FaceDetector mFaceDetector;                     // 얼굴 인식
    private CameraSource mCameraSource;                     // 카메라 객체
    private FaceTracker face_tracker;                       // 눈 파악
    private double left_thres = 0;                          // 사용자의 초기값
    private double right_thres = 0;
    private SharedPreferences bookmarkPref;
    private SharedPreferences.Editor bookEdit;
    private int book_mark;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);

        Map<String, Integer> map = new HashMap<String, Integer>();


        helloTxt = (TextView) findViewById(R.id.hellotxt);
        helloTxt.setText(readTxt());
        scrollView = (ScrollView) findViewById(R.id.scroll_text);
        helloTxt.getLocationOnScreen(location);

        bookmarkPref = getSharedPreferences("bookPred", Activity.MODE_PRIVATE);
        bookEdit = bookmarkPref.edit();

        PlayServicesUtil.isPlayServicesAvailable(this, 69);

        /*참고
           editor1.putFloat("LValue",left_thred1);
            editor1.putFloat("RValue",right_thred1);
            editor1.commit();

            float LV = intPref.getFloat("LValue",0);
            float RV = intPref.getFloat("RValue",0);
         */


        // permission granted...?
        if (isCameraPermissionGranted()) {
            // ...create the camera resource
            createCameraResources();
        } else {
            // ...else request the camera permission
            requestCameraPermission();
        }

        // 사용자가 화면을 터치하여 스크롤 뷰의 위치 변경시, 체크
        scrollView.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event){
                helloTxt.getLocationOnScreen(location);
                return false;
            }
        });

        /* 툴바로 북마크 저장 버튼? 누르면 저장되게하는 부분
        saveRankInfoToSharedPreferences(String inFile, String fileType, int position) 이함수를 사용하면
        "NAME"파일에 저장됨
        +
        리스트 뷰가 하나씩 생기게 하기
         getRankInfoItemsFromSharedPreferences()
        */
    }
//수정 필요
 /*   public void getRankInfoItemsFromSharedPreferences()
    {
        bookEdit.clear();

        //SharedPreferences 파일을 가져온다(파일이 없는 경우 자동 생성)
        SharedPreferences prefs = this.getSharedPreferences("NAME", Context.MODE_PRIVATE);

        //SharedPreferences에 저장된 모든 데이터 추출
        Map<String, ?> values = prefs.getAll();
        Iterator<String> iterator = sortByValue(values).iterator(); //추가한 부분-순서대로 정렬

        String recordA=null;
        String userMode=null;
        String userName=null;

        while (iterator.hasNext())
        {
            key = (String) iterator.next();

            //KEY 판별
            if(key != null );
            {
                try{

                    //데이터 추출
                    String RankInfoData = (String)values.get(key);
                    String[] RankInfos = RankInfoData.split("_@#@_" *//* 구분자 *//*);

                    recordA = RankInfos[0];
                    userMode = RankInfos[1];
                    userName = RankInfos[2];

                    if (recordA!=null && userMode!=null && userName !=null) {
                        //리스트 아이템 정보 출력으로 변환 저장
                        ListView_Rank i = new ListView_Rank(recordA + " / " + userMode + " / " + userName);


                        adapter.add(i);

                        adapter.notifyDataSetChanged();
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }*/

    // txt 파일 읽어오는 함수
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

    //눈깜박임에 따른 페이지 down 함수
    public void change_down_location(){
        // 절대값을 통해 text뷰의 스크롤뷰에서의 위치 파악
        if(location[1] < 0)
            location[1] = (-1)*location[1];

        // 위치 변경
            scrollView.scrollTo(0, location[1]+60);
            location[1] += 60;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // 눈깜박임에 따른 페이지 up 함수
    public void change_up_location(){
        // 절대값을 통해 text뷰의 스크롤뷰에서의 위치 파악
        if(location[1] < 0)
            location[1] = (-1)*location[1];
        // 기존의 위치에서 60 이동
        scrollView.scrollTo(0, location[1]-60);
        location[1] -= 60;
    }

    // 책갈피 추가함수
    public void book_mark_add(){

        //book_mark의 전역변수 설정 후, db에 저장 필요
        // book_mark는 int형 변수
        helloTxt.getLocationOnScreen(location);
         book_mark = location[1];
        if(book_mark < 0 ) book_mark = (-1)*book_mark;

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

 /*   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLeftEyeClosed(LeftEyeClosedEvent e) {
        change_down_location();
    }
*/
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

    //sharedPreferences에 저장하는 메소드~ 나중에 저장할 "NAME"을 사용자에게 입력받는 것으로 수정하자
    private boolean saveRankInfoToSharedPreferences(String inFile, String fileType, int position) {
        //SharedPreferences 파일을 가져온다(파일이 없는 경우 자동 생성)
        SharedPreferences prefs = this.getSharedPreferences("NAME", Context.MODE_PRIVATE);

        //SharedPreferences 에디터 생성
        SharedPreferences.Editor editor = prefs.edit();

        //정보 저장을 위한 KEY
        String RankKey = String.valueOf(System.currentTimeMillis());//저장 시간을 string으로 바꿔서 그 값을 키값으로 저장

        //정보를 위한 Value
        String pos = String.valueOf(position);//저장할 포지션을 string으로 변환
        String RankInfoValue = inFile + "_@#@_" + fileType + "_@#@_" + pos;//string으로 변환된 pos를 저장

        //에디터에 보관
        editor.putString(RankKey, RankInfoValue);

        ///파일에 에디터 내용 적용
        return editor.commit();
    }

    //정렬하는 메소드
    public static List sortByValue(final Map values) {
        List<String> list = new ArrayList();
        list.addAll(values.keySet());//해쉬맵에 저장한 키 값들을 새로운 어레이리스트에 저장함

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {

                Object v1 = values.get(o1);
                Object v2 = values.get(o2);
                return ((Comparable) v1).compareTo(v2);
            }
        });
        // Collections.reverse(list); // 주석시 오름차순 //이걸 안쓰면 숫자작은거부터, 쓰면 숫자 큰거부터!

        return list;
    }

    /*   private void editRankInfoItemsFromSharedPreferences() {
        Intent in = getIntent();
        String Nusername = in.getStringExtra("newname");

        if (Nusername != null) {
            SharedPreferences prefs = this.getSharedPreferences("NAME", Context.MODE_PRIVATE);

            //SharedPreferences에 저장된 모든 데이터 추출
            Map<String, ?> values = prefs.getAll();
            Iterator<String> iterator = sortByValue(values).iterator(); //추가한 부분-순서대로 정렬


            //KEY 판별
            if (key != 0)
            {
                try {
                    //데이터 추출
                    String RankInfoData = (String) values.get(sortByValue(values).get(a).toString());
                    String[] RankInfos = RankInfoData.split("_@#@_" *//* 구분자 *//*);

                    String inFile = RankInfos[0];
                    String fileType = RankInfos[1];
                    // String userName = RankInfos[2]; //name만 수정되어 새로운 값이 들어올거니까 이건 필요없음
                    if (inFile != null && fileType != null && Nusername != null) {
                        saveRankInfoToSharedPreferences(inFile, fileType, Nusername);


                        ///////sharedpreferences에서 삭제되는 부분,
                        removePrefs(sortByValue(values).get(a).toString());

                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/

}

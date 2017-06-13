package com.example.mygirlfriend.action_navigator.eyetoggle;

/**
 * Created by dayeon on 2017. 6. 13..
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygirlfriend.action_navigator.R;

import java.io.File;
import java.util.ArrayList;


public class FileListActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "AppPermission";
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;


    String mRoot = "";
    String mPath = "";
    TextView mTextMsg;
    ListView mListFile;
    ArrayList<String> mArFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);


        checkPermission();

    }


    public void start(){
        // SD 카드가 장착되어 있지 않다면 앱 종료
        if( isSdCard() == false )
            finish();
        mTextMsg = (TextView)findViewById(R.id.textMessage);
        // SD 카드 루트 폴더의 경로를 구한다
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mTextMsg.setText(mRoot);
        String[] fileList = getFileList(mRoot);
        for(int i=0; i < fileList.length; i++)
            Log.d("tag", fileList[i]);
        // ListView 초기화
        initListView();
        fileList2Array(fileList);
    }


    // ListView 초기화
    public void initListView() {
        mArFile = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mArFile);

        mListFile = (ListView)findViewById(R.id.listFile);
        mListFile.setAdapter(adapter);
        mListFile.setOnItemClickListener(this);
    }

    // ListView 항목 선택 이벤트 함수
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        String strItem = mArFile.get(position);

        String strPath = getAbsolutePath(strItem);// 선택된 폴더의 전체 경로를 구한다

        String[] fileList = getFileList(strPath);// 선택된 폴더에 존재하는 파일 목록을 구한다

        fileList2Array(fileList);// 파일 목록을 ListView 에 표시
    }

    // 폴더명을 받아서 전체 경로를 반환하는 함수
    public String getAbsolutePath(String strFolder) {
        String strPath;
        // 이전 폴더일때
        if( strFolder == ".." ) {
            // 전체 경로에서 최하위 폴더를 제거
            int pos = mPath.lastIndexOf("/");
            strPath = mPath.substring(0, pos);
        }
        else
            strPath = mPath + "/" + strFolder;
        return strPath;
    }

    // SD 카드 장착 여부를 반환
    public boolean isSdCard() {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED) == false) {
            Toast.makeText(this, "SD Card does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 특정 폴더의 파일 목록을 구해서 반환
    public String[] getFileList(String strPath) {
        // 폴더 경로를 지정해서 File 객체 생성
        File fileRoot = new File(strPath);
        // 해당 경로가 폴더가 아니라면 함수 탈출
        if( fileRoot.isDirectory() == false ) {
            //sd카드에는 텍스트 파일만 있다고 가정,   strPath이름의 텍스트 파일을 읽을거야
            int pos = strPath.lastIndexOf("/");
            strPath = strPath.substring(pos);
            Intent intent = new Intent(this, TextviewSdcardActivity.class);
            intent.putExtra("value",strPath);
            startActivity(intent);



            //Toast.makeText(this, "해당 경로가 폴더가 아님", Toast.LENGTH_SHORT).show();
            return null;
        }
        mPath = strPath;
        mTextMsg.setText(mPath);
        // 파일 목록을 구한다
        String[] fileList = fileRoot.list();
        return fileList;
    }

    // 파일 목록을 ListView 에 표시
    public void fileList2Array(String[] fileList) {
        if( fileList == null )
            return;
        mArFile.clear();
        // 현재 선택된 폴더가 루트 폴더가 아니라면
        if( mRoot.length() < mPath.length() )
            // 이전 폴더로 이동하기 위해서 ListView 에 ".." 항목을 추가
            mArFile.add("..");

        for(int i=0; i < fileList.length; i++) {
            Log.d("tag", fileList[i]);
            mArFile.add(fileList[i]);
        }
        ArrayAdapter adapter = (ArrayAdapter)mListFile.getAdapter();
        adapter.notifyDataSetChanged();
    }




    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " +  ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE));
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {

            start();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    start();

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


}

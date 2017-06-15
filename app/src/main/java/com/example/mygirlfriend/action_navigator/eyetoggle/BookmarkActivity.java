package com.example.mygirlfriend.action_navigator.eyetoggle;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mygirlfriend.action_navigator.R;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity {

    ArrayList<BookTag> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        ListView listview;
        ListViewAdapter adapter;

        //북마크 저장할 리스트를 동적 생성성
       list = new ArrayList<BookTag>();
   //     ArrayList<HashMap<Integer,String, String>>mapList = new ArrayList<HashMap <Integer,String,String>>();

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bookm),"혜민", "문서1");
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.voice),"미경", "문서2");
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.bookm),"다연", "문서1");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.voice),"미경", "문서2");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position) ;

                String titleStr = item.getTitle() ;
                String descStr = item.getDesc() ;
                Drawable iconDrawable = item.getIcon() ;

                // TODO : use item data.
            }
        }) ;
    }

    class BookTag{
        String tagID;
        String inFile;
        int type;
        public BookTag(int typeW,String id, String f){
            this.tagID = id;
            this.inFile = f;
            this.type = typeW;

        }

    }

}




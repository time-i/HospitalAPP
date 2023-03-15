package com.hospital.app.doctor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hospital.app.LoginActivity;
import com.hospital.app.R;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.entity.User;
import com.hospital.app.patient.ViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorMainActivity extends AppCompatActivity {

    @BindView(R.id.lv)
    ListView lv;
    SimpleAdapter adapter;
    List<Map<String,Object>> list;
    Map<String,Object> map;
    Context context;//上下文对象
    User user;//用户
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_doctor_main);
        ButterKnife.bind(this);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        user = (User)getIntent().getSerializableExtra("user");
        initData();
    }

    private void initData(){
        list = new ArrayList<>();
        cursor = db.rawQuery("select ID,PATIENT,HEARTDATA,UPLOADTIME,ISCOMMENT from HeartTable order by ID desc",null);
        while(cursor.moveToNext()){
            map = new HashMap<>();
            map.put("id",cursor.getInt(0));
            map.put("patient",cursor.getString(1));
            map.put("heartdata",cursor.getString(2));
            map.put("uploadtime",cursor.getString(3));
            map.put("iscomment",cursor.getInt(4));
            list.add(map);
        }
        cursor.close();
        adapter = new SimpleAdapter(context,list,R.layout.activity_heart_listitem,
                new String[]{"patient","uploadtime"},new int[]{R.id.list_patient,R.id.list_uploadtime});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemClickListener);
    }

    //列表点击事件
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String heartid = list.get(position).get("id").toString();
            String heartdata = list.get(position).get("heartdata").toString();
            String iscomment = list.get(position).get("iscomment").toString();
            Intent intent = new Intent(context, CommentActivity.class);
            if(iscomment.equals("1")){
                intent = new Intent(context, ViewActivity.class);
            }
            intent.putExtra("user",user);
            intent.putExtra("heartid",heartid);
            intent.putExtra("heartdata",heartdata);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
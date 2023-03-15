package com.hospital.app.patient;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hospital.app.LoginActivity;
import com.hospital.app.R;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientMainActivity extends AppCompatActivity {

    @BindView(R.id.add)
    Button addBtn;
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
        setContentView(R.layout.activity_patient_main);
        ButterKnife.bind(this);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        user = (User)getIntent().getSerializableExtra("user");
        addBtn.setOnClickListener(clickListener);
        initData();
    }

    private void initData(){
        list = new ArrayList<>();
        cursor = db.rawQuery("select ID,PATIENT,HEARTDATA,UPLOADTIME from HeartTable where PATIENT = ?",new String[]{user.getUsername()});
        while(cursor.moveToNext()){
            map = new HashMap<>();
            map.put("id",cursor.getInt(0));
            map.put("patient",cursor.getString(1));
            map.put("heartdata",cursor.getString(2));
            map.put("uploadtime",cursor.getString(3));
            list.add(map);
        }
        cursor.close();
        adapter = new SimpleAdapter(context,list,R.layout.activity_heart_listitem,
                new String[]{"patient","uploadtime"},new int[]{R.id.list_patient,R.id.list_uploadtime});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemClickListener);
    }

    //按钮点击事件
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,UploadActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
        }
    };

    //列表点击事件
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String heartid = list.get(position).get("id").toString();
            String heartdata = list.get(position).get("heartdata").toString();
            Intent intent = new Intent(context,ViewActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("heartid",heartid);
            intent.putExtra("heartdata",heartdata);
            intent.putExtra("flag","0");
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
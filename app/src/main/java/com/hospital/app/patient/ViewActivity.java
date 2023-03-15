package com.hospital.app.patient;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import com.hospital.app.R;
import com.hospital.app.common.LineChartView;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.doctor.DoctorMainActivity;
import com.hospital.app.entity.ChartEntity;
import com.hospital.app.entity.User;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {

    LineChartView lineChartView;
    TextView comment_tv;
    String heartid;
    String smsg = "";
    String comment="";
    String commentman="";
    String commenttime="";
    Context context;
    User user;//用户
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        lineChartView= (LineChartView) findViewById(R.id.lineChartView);
        comment_tv = findViewById(R.id.comment);
        heartid = getIntent().getStringExtra("heartid");
        smsg = getIntent().getStringExtra("heartdata");
        user = (User)getIntent().getSerializableExtra("user");
        initData();
        initComment();
    }

    private void initData(){
        // 下面进行初始的显示，属于test代码，可以不用看
        ArrayList<ChartEntity> data = new ArrayList<>();
        ArrayList<ChartEntity> data2 = new ArrayList<>();
        // smsg组成：send +待画波形(点数不限) + end ,示例如下：
        String[] sArray = smsg.split(",");
        data = new ArrayList<>();
        for (int i = 1; i < sArray.length-1 ; i++) {
            data.add(new ChartEntity(String.valueOf(i), Float.parseFloat(sArray[i])));
        }
        lineChartView.setUnitText("mv");
        lineChartView.setDataChart(data);
    }

    private void initComment(){
        cursor = db.rawQuery("select COMMENT,COMMENTMAN,COMMENTTIME from CommentTable where HEARTID = ?",new String[]{heartid});
        if(cursor.moveToNext()){
            comment = cursor.getString(0);
            commentman = cursor.getString(1);
            commenttime = cursor.getString(2);
        }
        cursor.close();

        String info = comment+"\n"+commentman+" "+commenttime;
        comment_tv.setText(info);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            String flag = getIntent().getStringExtra("flag");
            Intent intent = new Intent(context, DoctorMainActivity.class);
            if("0".equals(flag)){
                intent = new Intent(context, PatientMainActivity.class);
            }
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
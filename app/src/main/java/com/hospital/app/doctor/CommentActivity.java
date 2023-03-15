package com.hospital.app.doctor;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.hospital.app.R;
import com.hospital.app.common.LineChartView;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.entity.ChartEntity;
import com.hospital.app.entity.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;

public class CommentActivity extends AppCompatActivity {

    LineChartView lineChartView;
    EditText comment_et;
    Button saveBtn;
    String heartid;
    String smsg = "";
    String comment;
    Context context;
    User user;//用户
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_comment);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        lineChartView= (LineChartView) findViewById(R.id.lineChartView);
        comment_et = findViewById(R.id.comment);
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(clickListener);
        heartid = getIntent().getStringExtra("heartid");
        smsg = getIntent().getStringExtra("heartdata");
        user = (User)getIntent().getSerializableExtra("user");
        initData();
    }


    private void initData(){
        // 下面进行初始的显示，属于test代码，可以不用看
        ArrayList<ChartEntity> data = new ArrayList<>();
        ArrayList<ChartEntity> data2 = new ArrayList<>();
        // smsg组成：send +待画波形(点数不限) + end ,示例如下：
        String[] sArray = smsg.split(",");
        // draw
        data = new ArrayList<>();
        for (int i = 1; i < sArray.length-1 ; i++) {
            data.add(new ChartEntity(String.valueOf(i), Float.parseFloat(sArray[i])));
        }
        lineChartView.setUnitText("mv");
        lineChartView.setDataChart(data);
    }

    //按钮点击事件
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comment = comment_et.getText().toString();
            if(comment.equals("")){
                Toast.makeText(context,"请输入评价！",Toast.LENGTH_SHORT).show();
                return;
            }
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            db.execSQL("update HeartTable set ISCOMMENT = ? where ID = ?",new Object[]{1,heartid});
            db.execSQL("insert into CommentTable(HEARTID,COMMENT,COMMENTMAN,COMMENTTIME) values (?,?,?,?)",
                    new Object[]{heartid,comment,user.getUsername(),sdf.format(date)});
            Toast.makeText(context,"评价成功！",Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(context, DoctorMainActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
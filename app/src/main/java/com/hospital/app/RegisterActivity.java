package com.hospital.app;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.hospital.app.db.DatabaseHelper;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.loginname)
    EditText loginname_et;
    @BindView(R.id.password)
    EditText password_et;
    @BindView(R.id.password2)
    EditText password2_et;
    @BindView(R.id.username)
    EditText username_et;
    @BindView(R.id.telephone)
    EditText telephone_et;
    @BindView(R.id.address)
    EditText address_et;
    @BindView(R.id.nanRb)
    RadioButton nanRb;
    @BindView(R.id.nvRb)
    RadioButton nvRb;
    @BindView(R.id.hzRb)
    RadioButton hzRb;
    @BindView(R.id.ysRb)
    RadioButton ysRb;
    @BindView(R.id.registerBtn)
    Button registerBtn;
    @BindView(R.id.loginBtn)
    Button loginBtn;
    String loginname,password,password2,username,telephone,address,sex,usertype;
    Context context;
    //数据库
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        registerBtn.setOnClickListener(clickListener);
        loginBtn.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.registerBtn://注册按钮
                    loginname = loginname_et.getText().toString().trim();
                    password = password_et.getText().toString().trim();
                    password2 = password2_et.getText().toString().trim();
                    username = username_et.getText().toString().trim();
                    telephone = telephone_et.getText().toString().trim();
                    address = address_et.getText().toString().trim();
                    sex = nanRb.isChecked()?"男":"女";
                    usertype = hzRb.isChecked()?"0":"1";
                    if(loginname.equals("")){
                        Toast.makeText(context,"请输入用户名！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(password.equals("")){
                        Toast.makeText(context,"请输入密码！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(password2.equals("")){
                        Toast.makeText(context,"请确认密码！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!password.equals(password2)){
                        Toast.makeText(context,"两次密码输入不一致！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(username.equals("")){
                        Toast.makeText(context,"请输入姓名！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.execSQL("insert into UserTable(LOGINNAME,PASSWORD,USERNAME,SEX,TELEPHONE,ADDRESS,USERTYPE) values (?,?,?,?,?,?,?)"
                            ,new Object[]{loginname,password,username,sex,telephone,address,usertype});
                    Toast.makeText(context,"注册成功！",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.loginBtn://登录按钮
                    Intent intent = new Intent(context,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }

        }
    };


}
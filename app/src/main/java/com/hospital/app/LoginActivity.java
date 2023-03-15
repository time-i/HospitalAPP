package com.hospital.app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.doctor.DoctorMainActivity;
import com.hospital.app.entity.User;
import com.hospital.app.patient.PatientMainActivity;
import com.hospital.app.util.PermissionsUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.loginname)
    EditText loginname_et;//用户名
    @BindView(R.id.password)
    EditText password_et;//密码
    @BindView(R.id.hzRb)
    RadioButton hzRb;
    @BindView(R.id.ysRb)
    RadioButton ysRb;
    @BindView(R.id.savename)
    CheckBox savename_cb;//记住密码
    @BindView(R.id.loginBtn)
    Button loginBtn;//登录按钮
    @BindView(R.id.registerBtn)
    Button registerBtn;//注册按钮
    String loginname,password,usertype;
    Context context;//Context上下文对象
    SharedPreferences sp;//缓存用户名和密码
    User user;//用户对象
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();

        registerBtn.setOnClickListener(clickListener);
        loginBtn.setOnClickListener(clickListener);

        sp = getSharedPreferences("savename", MODE_PRIVATE);
        savename_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    loginname = loginname_et.getText().toString().trim();
                    password = password_et.getText().toString().trim();
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("loginname", loginname);
                    ed.putString("password", password);
                    ed.commit();
                }
            }
        });

        String loginname1 = sp.getString("loginname", "");
        String password1 = sp.getString("password", "");
        if (!loginname1.equals("") || !password1.equals("")) {
            savename_cb.setChecked(true);
        }
        loginname_et.setText(loginname1);
        password_et.setText(password1);

        permission();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.loginBtn://登录按钮
                    loginname = loginname_et.getText().toString().trim();
                    password = password_et.getText().toString().trim();
                    usertype = hzRb.isChecked()?"0":"1";
                    if(loginname.equals("")){
                        Toast.makeText(context,"请输入用户名！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(password.equals("")){
                        Toast.makeText(context,"请输入密码！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cursor = db.rawQuery("select USERNAME,SEX,TELEPHONE,ADDRESS from UserTable where LOGINNAME = ? and PASSWORD = ? and USERTYPE = ?", new String[]{loginname,password,usertype});
                    if(cursor.moveToNext()) {
                        if (savename_cb.isChecked()) {
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("loginname", loginname);
                            ed.putString("password", password);
                            ed.commit();
                        }
                        String username = cursor.getString(0);
                        String sex = cursor.getString(1);
                        String telephone = cursor.getString(2);
                        String address = cursor.getString(3);
                        user = new User(loginname,password,username,sex,telephone,address,usertype);
                        Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = null;
                        if(hzRb.isChecked()){
                            intent = new Intent(context, PatientMainActivity.class);
                        }else{
                            intent = new Intent(context, DoctorMainActivity.class);
                        }
                        intent.putExtra("user",user);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(context, "用户名或密码或类别错误！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.registerBtn://注册按钮
                    Intent intent1 = new Intent(context,RegisterActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this).setTitle("确定退出系统？")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 动态授权
     */
    public void permission(){
        //一个拍照权限和一个数据读写权限
        String[] permissions = new String[]{
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH
        };
        //PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    //创建监听权限的接口对象
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            //Toast.makeText(MainActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
            //Toast.makeText(MainActivity.this, "权限不通过!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
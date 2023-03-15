package com.hospital.app.patient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hospital.app.R;
import com.hospital.app.common.LineChartView;
import com.hospital.app.db.DatabaseHelper;
import com.hospital.app.entity.ChartEntity;
import com.hospital.app.entity.HeartRate;
import com.hospital.app.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //获取蓝牙实例

    private EditText sendEditText; //创建发送 句柄
    private TextView tv_in; //接收显示句柄
    private ScrollView scrollView; //翻页句柄
    private String smsg = ""; //显示用数据缓存

    BluetoothDevice mBluetoothDevice = null; //蓝牙设备
    BluetoothSocket mBluetoothSocket = null; //蓝牙通信Socket

    boolean bRun = true; //运行状态
    boolean bThread = false; //读取线程状态
    private InputStream is;    //输入流，用来接收蓝牙数据
    private LineChartView lineChartView;
    HeartRate hr = new HeartRate();

    private TextView tv_THDx,tv_x2,tv_x3,tv_x4,tv_x5,tv_A,tv_F; //显示句柄
    private String smsg_THDx = "", smsg_x1 = "", smsg_x2 = "", smsg_x3 = "", smsg_x4 = "", smsg_x5 = "", smsg_A = "", smsg_F = ""; //显示用数据缓存

    Context context;//上下文对象
    User user;//用户
    DatabaseHelper mOpenHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_upload);
        context = this;
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        user = (User)getIntent().getSerializableExtra("user");
        lineChartView= (LineChartView) findViewById(R.id.lineChartView);
        //获取控件ID
        scrollView = findViewById(R.id.receiveScrolView);
        tv_in = findViewById(R.id.in);

        TextView tv = findViewById(R.id.textView3);
        tv.setText("心率："+hr.show);


        //如果打不开蓝牙提示信息，结束程序
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //连接按钮响应
        final Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothAdapter.isEnabled() == false) {
                    Toast.makeText(getApplicationContext(), " 请先打开蓝牙", Toast.LENGTH_LONG).show();
                    return;
                }

                //如果未连接设备则  打开DevicesListActivity搜索设备
                if (mBluetoothSocket == null) {
                    Intent serveIntent = new Intent(getApplicationContext(), DevicesListActivity.class); //跳转活动
                    startActivityForResult(serveIntent, 1); //设置返回宏定义
                } else {
                    //关闭连接socket
                    try {
                        bRun = false;
                        Thread.sleep(2000);

                        is.close();
                        mBluetoothSocket.close();
                        mBluetoothSocket = null;

                        connectButton.setText("连接");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //开始

        final Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsg = "";
                if (bThread == false) {
                    readThread.start();
                    bThread = true;
                } else {
                    bRun = true;
                }
            }
        });

        //结束
        final Button endButton = findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                db.execSQL("insert into HeartTable(PATIENT,HEARTDATA,UPLOADTIME,ISCOMMENT) values (?,?,?,?)",
                        new Object[]{user.getUsername(),smsg,sdf.format(date),0});
                Toast.makeText(UploadActivity.this, "数据保存成功！", Toast.LENGTH_SHORT).show();
                smsg = "";
                bThread = false;
                //关闭连接socket
//                try {
//                    bRun = false;
//                    Thread.sleep(2000);
//
//                    is.close();
//                    mBluetoothSocket.close();
//                    mBluetoothSocket = null;
//
//                    connectButton.setText("连接");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

        // 设置设备可以被搜索
        new Thread() {
            public void run() {
                if (mBluetoothAdapter.isEnabled() == false) {
                    mBluetoothAdapter.enable();
                }
            }
        }.start();


        // 下面进行初始的显示，属于test代码，可以不用看
//        ArrayList<ChartEntity> data = new ArrayList<>();
//        ArrayList<ChartEntity> data2 = new ArrayList<>();
//        // smsg组成：send +待画波形(点数不限) + end ,示例如下：
//        smsg = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,6,7,8,9,10,11,12,13,14";
//        String[] sArray = smsg.split(",");
//        // draw
//        data = new ArrayList<>();
//        for (int i = 1; i < sArray.length - 1; i++) {
//            data.add(new ChartEntity(String.valueOf(i), Float.parseFloat(sArray[i])));
//        }
//        lineChartView.setUnitText("mv");
//        lineChartView.setDataChart(data);
    }

    //接收活动结果，响应startActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:     //连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras().getString(DevicesListActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

                    // 用服务号得到socket
                    try {
                        mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                    }
                    //连接socket
                    Button connectButton = findViewById(R.id.connectButton);
                    try {
                        mBluetoothSocket.connect();
                        Toast.makeText(this, "连接" + mBluetoothDevice.getName() + "成功！", Toast.LENGTH_SHORT).show();
                        connectButton.setText("断开");
                    } catch (IOException e) {
                        try {
                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                            mBluetoothSocket.close();
                            mBluetoothSocket = null;
                        } catch (IOException ee) {
                            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    }

                    //打开接收线程
                    try {
                        is = mBluetoothSocket.getInputStream();   //得到蓝牙数据输入流
                    } catch (IOException e) {
                        Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (bThread == false) {
                        readThread.start();
                        bThread = true;
                    } else {
                        bRun = true;
                    }
                }
                break;
            default:
                break;
        }
    }

    //接收数据线程
    Thread readThread=new Thread(){
        public void run(){
            int num = 0;
            byte[] buffer = new byte[1024];
            byte[] buffer_new = new byte[1024];
            int i = 0;
            int n = 0;
            bRun = true;

            //接收线程
            while(true){
                try{
                    while(is.available()==0){
                        while(bRun == false){}
                    }

                    while(true){
                        if(!bThread)//跳出循环
                            return;
                        num = is.read(buffer);         //读入数据
                        n=0;

//                        if (smsg.indexOf("end")!=-1){
//                            //存在包含关系，因为返回的值不等于-1
//                            smsg = "";
//                        }

                        String s0 = new String(buffer,0,num);
                        for(i=0;i<num;i++){
                            if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
                                buffer_new[n] = 0x0a;
                                i++;
                            }else{
                                buffer_new[n] = buffer[i];
                            }
                            n++;

                            hr.show++;
                        }

                        String s = new String(buffer_new,0,n);
                        smsg+=s;   //写入接收缓存
                        if(is.available()==0)break;  //短时间没有数据才跳出进行显示
                    }

                    //发送显示消息，进行显示刷新
                    handler.sendMessage(handler.obtainMessage());
                }catch(IOException e){
                }
            }
        }
    };

    //消息处理队列
    Handler handler= new Handler() {
        ArrayList<ChartEntity> data = new ArrayList<>();
        ArrayList<ChartEntity> data2 = new ArrayList<>();

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_in.setText(smsg);   //显示数据
            scrollView.scrollTo(0, tv_in.getMeasuredHeight()); //跳至数据最后一页

            // show valves
            String[] sArray = smsg.split(",");
            // draw
            data = new ArrayList<>();
            for (int i = 1; i < sArray.length-1 ; i++) {
                data.add(new ChartEntity(String.valueOf(i), Float.parseFloat(sArray[i])));
            }
//            lineChartView.setShadow(true);
            lineChartView.setUnitText("mv");
            lineChartView.setDataChart(data);

//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            db.execSQL("insert into HeartTable(PATIENT,HEARTDATA,UPLOADTIME,ISCOMMENT) values (?,?,?,?)",
//                    new Object[]{user.getUsername(),smsg,sdf.format(date),0});
        }
    };

    //关闭程序掉用处理部分
    public void onDestroy(){
        super.onDestroy();
        if(mBluetoothSocket!=null)  //关闭连接socket
            try{
                mBluetoothSocket.close();
            }catch(IOException e){}
        //	_bluetooth.disable();  //关闭蓝牙服务
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(context, PatientMainActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
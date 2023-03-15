package com.hospital.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库助手
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	//数据库名字
	public static String DATABASE_NAME = "hospital.db";
	//数据库版本
    private static final int DATABASE_VERSION = 1;
    
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		//用户表（编号，用户名，密码，姓名，性别，电话，地址，类别（0患者1医生））
		db.execSQL("DROP TABLE IF EXISTS UserTable");
		db.execSQL("CREATE TABLE UserTable(ID INTEGER PRIMARY KEY AUTOINCREMENT,LOGINNAME VARCHAR,PASSWORD VARCHAR,USERNAME VARCHAR,SEX VARCHAR,TELEPHONE VARCHAR,ADDRESS VARCHAR,USERTYPE INTEGER)");

		//心音数据表（编号，患者用户名，心音数据，上传时间，是否评价(0否1是)）
		db.execSQL("DROP TABLE IF EXISTS HeartTable");
		db.execSQL("CREATE TABLE HeartTable(ID INTEGER PRIMARY KEY AUTOINCREMENT,PATIENT VARCHAR,HEARTDATA VARCHAR,UPLOADTIME VARCHAR,ISCOMMENT INTEGER)");


		//医生评价表（编号，心音编号，评价内容，评价人，评价时间）
		db.execSQL("DROP TABLE IF EXISTS CommentTable");
		db.execSQL("CREATE TABLE CommentTable(ID INTEGER PRIMARY KEY AUTOINCREMENT,HEARTID INTEGER,COMMENT VARCHAR,COMMENTMAN VARCHAR,COMMENTTIME VARCHAR)");
	}

	/**
	 * 升级数据库
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		
	}
	
}

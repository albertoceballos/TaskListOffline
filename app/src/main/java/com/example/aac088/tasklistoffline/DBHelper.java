package com.example.aac088.tasklistoffline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aac088 on 9/28/2017.
 */
//asd
public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context){
        super(context,TaskContract.DB_NAME,null,TaskContract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+TaskContract.TaskEntry.TABLE+" ( "+
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TaskContract.TaskEntry.COL_USER_DATA_LIST+ " TEXT NOT NULL, "+
                TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}

package com.example.aac088.tasklistoffline;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView add_imgvw;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private DBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TaskList");
        add_imgvw = (ImageView) findViewById(R.id.main_add_imgvw);
        listView = (ListView) findViewById(R.id.main_listview);
        mHelper = new DBHelper(this);

        updateUI();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent taskIntent = new Intent(MainActivity.this,TaskActivity.class);
                startActivity(taskIntent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder editBuilder = new AlertDialog.Builder(MainActivity.this);
                editBuilder.setTitle("Edit List Name");
                editBuilder.setMessage("Type new list name");
                final EditText editText = new EditText(MainActivity.this);
                editText.setText(listView.getItemAtPosition(position).toString());
                editBuilder.setView(editText);
                editBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item_name_change = editText.getText().toString();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST,item_name_change);
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        String selectoin = TaskContract.TaskEntry.COL_USER_DATA_LIST + " LIKE ?";
                        String[] selectionArgs = { adapter.getItem(position).toString()};
                        db.update(TaskContract.TaskEntry.TABLE,values,selectoin,selectionArgs);
                        updateUI();
                    }
                });
                editBuilder.setNegativeButton("Cancel",null);
                editBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        db.delete(TaskContract.TaskEntry.TABLE,TaskContract.TaskEntry.COL_USER_DATA_LIST + " = ?",new String[]{items.get(position).toString()});
                        db.close();
                        updateUI();
                    }
                });
                editBuilder.create().show();
                return true;
            }
        });


        add_imgvw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder addListBuilder = new AlertDialog.Builder(MainActivity.this);
                addListBuilder.setTitle("Enter new list");
                addListBuilder.setMessage("Enter New List Name");
                final EditText newList = new EditText(MainActivity.this);
                newList.setText("");
                addListBuilder.setView(newList);
                addListBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_list_name = newList.getText().toString();
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST,new_list_name);
                        values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK,"initial");
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateUI();
                    }
                });
                addListBuilder.setNegativeButton("Cancel",null);
                addListBuilder.create().show();
            }
        });
    }

    private void updateUI() {
        items = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_USER_DATA_LIST,TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK},
                null,null,null,null,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(TaskContract.TaskEntry.COL_USER_DATA_LIST);
            items.add(cursor.getString(index));
        }

        if(adapter == null){
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
            listView.setAdapter(adapter);
        }else{
            adapter.clear();
            adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();

    }
}
package com.example.aac088.tasklistoffline;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Model> model;
    private ImageView add_task;
    private DBHelper mHelper;
    private String list_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        add_task = (ImageView) findViewById(R.id.task_add_imgvw);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tasks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.task_listview);

        mHelper = new DBHelper(TaskContract.context);

        Bundle bundle = getIntent().getExtras();
        list_name = bundle.getString("list_name");

        updateUI();

        //model = new ArrayList<>();
        //model.add(0,new Model("task 1",false));
        //model.add(1,new Model("task 2",false));
        //customAdapter = new CustomAdapter(model);


       // listView.setAdapter(customAdapter);

        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder editTask = new AlertDialog.Builder(TaskActivity.this);
                editTask.setTitle("Add new Task");
                editTask.setMessage("Add new Task");
                final EditText editText = new EditText(TaskActivity.this);
                editText.setText("");
                editTask.setView(editText);
                editTask.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_task = editText.getText().toString();
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK,new_task);
                        values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST,list_name);
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateUI();
                    }
                });//njn
                editTask.setNegativeButton("CANCEL",null);
                editTask.create().show();
            }
        });




    }

    private void updateUI() {
        model = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String query = "SELECT * FROM "+TaskContract.TaskEntry.TABLE +" WHERE "+TaskContract.TaskEntry.COL_USER_DATA_LIST+" ='" + list_name+"'";

        Cursor cursor = db.rawQuery(query,null);
        while(cursor.moveToNext()){
            System.out.println("RUNNIg");
            int index = cursor.getColumnIndex(TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK);
            System.out.println(cursor.getString(index));
            model.add(0,new Model(cursor.getString(index),false));
        }

        if(customAdapter == null){
            customAdapter = new CustomAdapter(model);
            listView.setAdapter(customAdapter);
        }else{
            customAdapter.modelArrayList.clear();
            customAdapter.modelArrayList.addAll(model);
            customAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public class CustomAdapter extends BaseAdapter{
        private ArrayList<Model> modelArrayList;


        public CustomAdapter(ArrayList<Model> modelArrayList){
            this.modelArrayList=modelArrayList;
        }

        public int getViewTypeCount(){
            return getCount();
        }

        public int getItemViewType(int position){
            return position;
        }

        public int getCount(){
            return modelArrayList.size();
        }

        public Object getItem(int position){
            return modelArrayList.get(position);
        }

        public long getItemId(int position){
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent){
            final CustomAdapter.ViewHolder holder;

            if(convertView == null){
                holder = new CustomAdapter.ViewHolder();
                LayoutInflater inflater = (LayoutInflater) TaskActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.lv_item,null,true);

                holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
                holder.task = (TextView) convertView.findViewById(R.id.task_textview);

                convertView.setTag(holder);
            }else{
                holder = (CustomAdapter.ViewHolder) convertView.getTag();
            }

            holder.task.setText(modelArrayList.get(position).getTask());

            final String task = modelArrayList.get(position).getTask().toString();
            holder.task.setTag(R.integer.btnplusview,convertView);
            holder.task.setTag(position);

            holder.task.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    View tempView = (View) holder.task.getTag(R.integer.btnplusview);
                    TextView tv = (TextView) tempView.findViewById(R.id.task_textview);
                    final Integer pos = (Integer) holder.task.getTag();
                    AlertDialog.Builder editTask = new AlertDialog.Builder(TaskActivity.this);
                    editTask.setTitle("Change task name");
                    editTask.setMessage("Change name and update");
                    final EditText editText = new EditText(TaskActivity.this);
                    editText.setText(modelArrayList.get(pos).getTask().toString());
                    final String old_task = modelArrayList.get(pos).getTask().toString();
                    editTask.setView(editText);
                    editTask.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String new_task_name = editText.getText().toString();
                            //Update method
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK,new_task_name);
                            db.update(TaskContract.TaskEntry.TABLE,values,TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK + " = ?",new String[]{old_task});
                            db.close();
                            updateUI();
                        }
                    });
                    editTask.setNegativeButton("CANCEL",null);
                    editTask.create().show();
                }
            });

            holder.checkBox.setChecked(modelArrayList.get(position).getSelected());

            holder.checkBox.setTag(R.integer.btnplusview,convertView);
            holder.checkBox.setTag(position);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View tempView = (View) holder.checkBox.getTag(R.integer.btnplusview);
                    TextView tv = (TextView) tempView.findViewById(R.id.task_textview);
                    Integer pos = (Integer) holder.checkBox.getTag();

                    Toast.makeText(TaskActivity.this,"Checkbox "+pos+" clicked!",Toast.LENGTH_SHORT).show();

                    if(!modelArrayList.get(pos).getSelected()){
                        modelArrayList.get(pos).setSelected(true);
                        String old_task = modelArrayList.get(pos).getTask().toString();
                        //Add method to erase from database
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        db.delete(TaskContract.TaskEntry.TABLE,TaskContract.TaskEntry.COL_USER_DATA_LIST_TASK + " = ?",new String[]{old_task});
                        db.close();
                        updateUI();
                    }
                }
            });
            return convertView;
        }

        private class ViewHolder{
            protected CheckBox checkBox;
            private TextView task;
        }
    }


}

package com.example.aac088.tasklistoffline;

import android.provider.BaseColumns;

/**
 * Created by aac088 on 9/28/2017.
 */

public class TaskContract {//asd

    public static final String DB_NAME = "com.example.aac088.tasklistoffline";

    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns{
        public static final String TABLE = "user_data";

        public static final String COL_USER_DATA_LIST = "user_list";

        public static final String COL_USER_DATA_LIST_TASK="user_list_task";
    }
}

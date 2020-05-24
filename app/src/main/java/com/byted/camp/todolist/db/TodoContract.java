package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量
    public static final String SQL_CREATE_LIST =
            "CREATE TABLE " + todo.TABLE_NAME + " (" +
                    todo._ID + " INTEGER PRIMARY KEY," +
                    todo.THING + " TEXT," +
                    todo.TIME + " TEXT," +
                    todo.STATE + " TEXT)";

    public static final String SQL_DELETE_LIST =
            "DROP TABLE IF EXISTS " + todo.TABLE_NAME;
    private TodoContract() {
    }

    public static class todo implements BaseColumns {
        public static final String TABLE_NAME = "todo_list";
        public static final String THING = "thing";
        public static final String TIME = "time";
        public static final String STATE = "state";
    }
}

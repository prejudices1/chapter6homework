package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;

    private TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        todoDbHelper = new TodoDbHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        todoDbHelper.close();
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoContract.todo.THING, content);
        SimpleDateFormat df = new SimpleDateFormat("E,d MMM yyyy HH:mm:ss");
        Timestamp Time = new Timestamp(System.currentTimeMillis());
        String time;
        time = df.format(Time);
        values.put(TodoContract.todo.TIME,time);
        State state = State.TODO;
        values.put(TodoContract.todo.STATE,state.intValue);
        long newRowID = db.insert(TodoContract.todo.TABLE_NAME,null,values);
        if(newRowID < 0)//新行id小于0失败
            return false;
        else
            return true;
    }
}

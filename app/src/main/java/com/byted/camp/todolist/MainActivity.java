package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper todoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        todoDbHelper = new TodoDbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());//更新或删除时不refresh标签不会变
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
               notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        todoDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        List<Note> result = new ArrayList<Note>();
        Cursor cursor = db.query(false, "todo_list", null, null,null,null,null,null,null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.todo._ID));
            Note temp = new Note(id);
            String thing = cursor.getString(cursor.getColumnIndex(TodoContract.todo.THING));
            temp.setContent(thing);
            SimpleDateFormat df = new SimpleDateFormat("E,d MMM yyyy HH:mm:ss");//时间格式
            String Time = cursor.getString(cursor.getColumnIndex(TodoContract.todo.TIME));
            try {
                java.util.Date time = df.parse(Time);
                temp.setDate(time);}
            catch (ParseException e){
                e.printStackTrace();
            }
            String State = cursor.getString(cursor.getColumnIndex(TodoContract.todo.STATE));
            int Int_State = Integer.valueOf(State).intValue();
            State state = State.from(Int_State);
            temp.setState(state);
            result.add(temp);
        }
        cursor.close();
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        String selection = TodoContract.todo._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int deletedRows = db.delete(TodoContract.todo.TABLE_NAME, selection, selectionArgs);

    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = todoDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoContract.todo.STATE,note.getState().intValue);
        String selection = TodoContract.todo._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(note.id)};
        int count = db.update(
                TodoContract.todo.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

}

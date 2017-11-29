package nov.me.kanmodel.notes;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 存放各种操作方法的助手类
 * Created by KanModel on 2017/11/26.
 */

public class Aid {

    private static final String TAG = "AidClass";

    /**
     * @param time 字符串类型的时间戳
     * @return 时间字符串
     */
    static String stampToDate(String time) {
        String res;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = Long.valueOf(time);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * @param time long类型的时间戳
     * @return 时间字符串
     */
    static String stampToDate(long time) {
        return stampToDate(String.valueOf(time));
    }

    /**
     * @param dbHelper 数据库操作类
     * @return Note类
     */
    public static Note addSQLNote(DatabaseHelper dbHelper) {
        return addSQLNote(dbHelper, "content", "title");
    }

    /**
     * @param dbHelper 数据库操作类
     * @param note Note类
     */
    public static void addSQLNote(DatabaseHelper dbHelper, Note note) {
        addSQLNote(dbHelper, note.getContent(), note.getTitle());
    }

    /**
     * @param dbHelper 数据库操作类
     * @param content 内容
     * @param title 标题
     * @return 返回新添加的Note类
     */
    static Note addSQLNote(DatabaseHelper dbHelper, String content, String title) {
        long timeStamp = new Date().getTime();
        return addSQLNote(dbHelper, content, title, timeStamp);
    }

    static Note addSQLNote(DatabaseHelper dbHelper, String content, String title, long timeStamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Note note;
        ContentValues values = new ContentValues();
        values.put("content", content);
        values.put("title", title);
        values.put("time", timeStamp);
        db.insert("Note", null, values);
        //获取数据库最后一条信息
        Cursor cursor1 = db.rawQuery("select * from Note", null);
        if (cursor1.moveToLast()) {
            String logtime = cursor1.getString(cursor1.getColumnIndex("logtime"));
            long time = cursor1.getLong(cursor1.getColumnIndex("time"));
            note = new Note(title, content, logtime, time);
        } else {
            note = null;
        }
        cursor1.close();
        return note;
    }

    /**
     * @param title 标题
     * @param content 内容
     * @param time 时间戳
     * @param pos 在RecyclerView中的位置
     */
    static void updateSQLNote(String title, String content, Long time, int pos) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
        NoteAdapter.getNotes().get(pos).setTitle(title);
        NoteAdapter.getNotes().get(pos).setContent(content);
        MainActivity.getNoteAdapter().refreshData(pos);
    }

    /**
     * 根据时间戳搜索数据库中的内容设置isDeleted为1代表删除
     * @param time 时间戳
     */
    static void deleteSQLNote(long time) {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isDeleted", 1);
        db.update("Note", values, "time = ?", new String[]{String.valueOf(time)});
    }

    /**
     * 清空数据库
     */
    static void deleteSQLNoteForced() {
        SQLiteDatabase db = MainActivity.getDbHelper().getWritableDatabase();
        db.delete("Note", "time > ?", new String[]{"0"});
    }
}

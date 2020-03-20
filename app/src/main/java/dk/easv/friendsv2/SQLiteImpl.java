package dk.easv.friendsv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public class SQLiteImpl implements IDataAccess {

    private static final String DATABASE_NAME = "sqlite.mDatabase";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "Friend";

    private SQLiteDatabase mDatabase;
    private SQLiteStatement insertStmt;

    public SQLiteImpl(Context c) {
        OpenHelper openHelper = new OpenHelper(c);
        mDatabase = openHelper.getWritableDatabase();
    }

    public long insert(BEFriend f) {
        return 0;
    }


    public void deleteAll() {
        mDatabase.delete(TABLE_NAME, null, null);
    }


    public List<BEFriend> selectAll() {
        return null;
    }


    public void update(BEFriend f) {

    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME
                    + "(id INTEGER PRIMARY KEY, name TEXT, phone TEXT, isFavorit BOOLEAN, photoUrl TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

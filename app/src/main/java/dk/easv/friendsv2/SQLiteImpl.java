package dk.easv.friendsv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public class SQLiteImpl implements IDataAccess {

    private static final String DATABASE_NAME = "sqlite.mDatabase";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "Friend";

    private SQLiteDatabase mDatabase;
    private SQLiteStatement insertStmt;
    private SQLiteStatement updateStmt;
    private SQLiteStatement deleteStmt;

    public SQLiteImpl(Context c) {
        OpenHelper openHelper = new OpenHelper(c);
        mDatabase = openHelper.getWritableDatabase();
        String INSERT = "insert into " + TABLE_NAME
                + "(name, phone, isFavorite, photoUrl) values (?,?, ? , ?)";
        String UPDATE = "update " + TABLE_NAME  +" SET " + "name = (?)" + ", phone = (?)" +
                ", isFavorite = (?)" + ", photoUrl = (?)" + "WHERE id = (?)";
        String DELETE = "delete from " + TABLE_NAME + "WHERE id = (?)";
        insertStmt = mDatabase.compileStatement(INSERT);
        updateStmt = mDatabase.compileStatement(UPDATE);
        deleteStmt =mDatabase.compileStatement(DELETE);

    }

    public long insert(BEFriend f) {
        insertStmt.bindString(1, f.getName());
        insertStmt.bindString(2, f.getPhone());
        insertStmt.bindString(3, f.isFavorite().toString());
        insertStmt.bindString(4, f.getPhotoUrl());
        long id = this.insertStmt.executeInsert();
        f.setId(id);
        return id;
    }


    public void deleteAll() {
        mDatabase.delete(TABLE_NAME, null, null);
    }


    public List<BEFriend> selectAll() {
        List<BEFriend> list = new ArrayList<BEFriend>();
        Cursor cursor = mDatabase.query(TABLE_NAME, new String[] { "id", "name", "phone", "isFavorite", "photoUrl" },
                null, null, null, null, "name");
        if (cursor.moveToFirst()) {
            do {
                list.add(new BEFriend(cursor.getInt(0), cursor.getString(1), cursor.getString(2), Boolean.parseBoolean(cursor.getString(3)), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }


    public void update(BEFriend f) {
        updateStmt.bindString(1, f.getName());
        updateStmt.bindString(2, f.getPhone());
        updateStmt.bindString(3, f.isFavorite().toString());
        updateStmt.bindString(4, f.getPhotoUrl());
        updateStmt.bindLong(5,f.getId());
        updateStmt.executeUpdateDelete();
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME
                    + "(id INTEGER PRIMARY KEY, name TEXT, phone TEXT, isFavorite BOOLEAN, photoUrl TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

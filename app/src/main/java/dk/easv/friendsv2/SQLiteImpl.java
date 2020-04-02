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
    // The name of the database
    private static final String DATABASE_NAME = "sqlite.mDatabase";

    // The version of the database. This should be changed whenever the tables need to be updated
    // Since changing this calls the onUpgrade method of the OpenHelper Class
    private static final int DATABASE_VERSION = 7;

    // A static string for the table name  which is used to define the name of the table in various places
    // having it as a instance variable allows easy editing of the table name without having to
    // change it in several locations
    private static final String TABLE_NAME = "Friend";

    // The database on which statements are called
    private SQLiteDatabase mDatabase;
    // Insert statement for creating friends
    private SQLiteStatement insertStmt;
    // Update statement for updating a specific friend
    private SQLiteStatement updateStmt;
    // Delete statement for deleting a specific friend
    private SQLiteStatement deleteStmt;

    public SQLiteImpl(Context c) {
        OpenHelper openHelper = new OpenHelper(c);
        mDatabase = openHelper.getWritableDatabase();
        String INSERT = "insert into " + TABLE_NAME
                + "(name, phone, isFavorite, photoUrl, homeLatitude, homeLongitude)" +
                " values (?,?, ? , ?,?,?)";
        String UPDATE = "update " + TABLE_NAME  +" SET " + "name = (?), phone = (?)," +
                " isFavorite = (?), photoUrl = (?), homeLatitude = (?), homeLongitude = (?)"
                + "WHERE id = (?)";
        String DELETE = "delete from " + TABLE_NAME + " WHERE id = (?)";
        insertStmt = mDatabase.compileStatement(INSERT);
        updateStmt = mDatabase.compileStatement(UPDATE);
        deleteStmt =mDatabase.compileStatement(DELETE);

    }

    // Creates a new friend in the database
    public long insert(BEFriend f) {
        insertStmt.bindString(1, f.getName());
        insertStmt.bindString(2, f.getPhone());
        insertStmt.bindString(3, f.isFavorite().toString());
        insertStmt.bindString(4, f.getPhotoUrl());
        insertStmt.bindDouble(5, f.getHomeLatitude());
        insertStmt.bindDouble(6, f.getHomeLongitude());
        long id = this.insertStmt.executeInsert();
        f.setId(id);
        return id;
    }

    // This method deletes all friends currently stored in the database
    public void deleteAll() {
        mDatabase.delete(TABLE_NAME, null, null);
    }

    // Delete a friend based on their ID
    public void delete(BEFriend f) {
        deleteStmt.bindLong(1, f.getId());
        deleteStmt.executeUpdateDelete();
    }

    // Selects all friends in the database by getting all the values from the friends table
    // with a Cursor which creates a new friend and puts it in a list which is then returned.
    public List<BEFriend> selectAll() {
        List<BEFriend> list = new ArrayList<BEFriend>();
        Cursor cursor = mDatabase.query(TABLE_NAME, new String[] { "id", "name", "phone", "isFavorite", "photoUrl", "homeLatitude", "homeLongitude"},
                null, null, null, null, "name");
        if (cursor.moveToFirst()) {
            do {
                list.add(new BEFriend(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), Boolean.parseBoolean(cursor.getString(3)),
                        cursor.getString(4), cursor.getDouble(5), cursor.getDouble(6)));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }

        return list;
    }

    // Updates a friend using the update statement string defined in the constructor
    public void update(BEFriend f) {
        updateStmt.bindString(1, f.getName());
        updateStmt.bindString(2, f.getPhone());
        updateStmt.bindString(3, f.isFavorite().toString());
        updateStmt.bindString(4, f.getPhotoUrl());
        updateStmt.bindDouble(5, f.getHomeLatitude());
        updateStmt.bindDouble(6, f.getHomeLongitude());
        updateStmt.bindLong(7, f.getId());
        Log.d("Friend2", "update: " + f);
        updateStmt.executeUpdateDelete();
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Initial setup of the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME
                    + "(id INTEGER PRIMARY KEY, name TEXT, phone TEXT, isFavorite BOOLEAN, " +
                    "photoUrl TEXT, homeLatitude TEXT, homeLongitude TEXT)");
        }

        // Refreshes the database by dropping the friends table if it exists and creates it anew
        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}

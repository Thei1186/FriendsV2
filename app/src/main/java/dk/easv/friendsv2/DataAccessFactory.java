package dk.easv.friendsv2;

import android.content.Context;

public class DataAccessFactory {

    // Returns an instance of the SQLite implementation class
    public static IDataAccess getInstance(Context c ) {
        return new SQLiteImpl(c);
    }
}

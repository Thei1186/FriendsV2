package dk.easv.friendsv2;

import android.content.Context;

public class DataAccessFactory {

    public static IDataAccess getInstance(Context c ) {
        return new SQLiteImpl(c);
    }
}

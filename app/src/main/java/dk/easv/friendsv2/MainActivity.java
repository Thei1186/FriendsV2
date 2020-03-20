package dk.easv.friendsv2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public class MainActivity extends ListActivity {
    private IDataAccess mDataAccess;
    public static String TAG = "Friend2";
    int SECOND_ACTIVITY = 2;

    List<BEFriend> friends;
    ArrayAdapter adapter;
    FriendAdapter friendAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Friends v2");

        mDataAccess = DataAccessFactory.getInstance(this);
        friends = mDataAccess.selectAll();

        friendAdapter = new FriendAdapter(this, R.layout.cell , friends);

        setListAdapter(friendAdapter);

    }


    @Override
    public void onListItemClick(ListView parent, View v, int position,
                                long id) {

        Intent x = new Intent(this, DetailActivity.class);
        Log.d(TAG, "Detail activity will be started");
        BEFriend friend = friends.get(position);
        addData(x, friend);
        x.putExtra("position",position);
        startActivityForResult(x,SECOND_ACTIVITY);
        Log.d(TAG, "Detail activity is started");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SECOND_ACTIVITY) {
            switch (resultCode) {
                case RESULT_OK:
                    BEFriend updatedFriend = (BEFriend)data.getExtras().getSerializable("updatedFriend");

                    int position = data.getExtras().getInt("position");
                    friends.set(position, updatedFriend);



                    Log.d("XYZ", updatedFriend.getName());
                    friendAdapter = new FriendAdapter(this,
                            R.layout.cell,
                            friends);

                    setListAdapter(adapter);

                    break;
                case RESULT_CANCELED:
                    break;
                default:

            }
        }


    }

    private void addData(Intent x, BEFriend f)
    {
        x.putExtra("friend", f);
    }



}

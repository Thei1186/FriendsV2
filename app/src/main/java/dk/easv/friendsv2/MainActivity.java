package dk.easv.friendsv2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dk.easv.friendsv2.Model.BEFriend;
import dk.easv.friendsv2.Model.Friends;

public class MainActivity extends ListActivity {

    public static String TAG = "Friend2";
    int SECOND_ACTIVITY = 2;
    Friends m_friends;
    String[] friends;
    ArrayAdapter adapter;
    FriendAdapter friendAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Friends v2");
        m_friends = new Friends();

        friends = m_friends.getNames();

        friendAdapter = new FriendAdapter(this, R.layout.cell ,m_friends.getAll());

        adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        friends);

        setListAdapter(friendAdapter);

    }


    @Override
    public void onListItemClick(ListView parent, View v, int position,
                                long id) {

        Intent x = new Intent(this, DetailActivity.class);
        Log.d(TAG, "Detail activity will be started");
        BEFriend friend = m_friends.getAll().get(position);
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
                    m_friends.getAll().set(position, updatedFriend);


                    friends = m_friends.getNames();
                    Log.d("XYZ", updatedFriend.getName());
                    adapter = new FriendAdapter(this,
                            R.layout.cell,
                            m_friends.getAll());

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

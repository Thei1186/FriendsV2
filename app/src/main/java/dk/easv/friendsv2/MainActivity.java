package dk.easv.friendsv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import dk.easv.friendsv2.Model.BEFriend;

public class MainActivity extends AppCompatActivity {
    // Provides access to database
    private IDataAccess mDataAccess;
    // Tag for logging
    public static String TAG = "Friend2";
    // Used for opening the details activity
    int SECOND_ACTIVITY = 2;

    // The list of friends fetched from the database
    List<BEFriend> friends;
    // The adapter for presenting the list of friends
    FriendAdapter friendAdapter;
    // The list view that contains the friends
    ListView friendList;
    // The context menu used for creating a new friend or deleting all friends
    TextView menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Friends v2");

        menu = findViewById(R.id.menu);
        friendList = findViewById(R.id.list_friends);

        registerForContextMenu(menu);

        mDataAccess = DataAccessFactory.getInstance(this);
        friends = mDataAccess.selectAll();

        friendAdapter = new FriendAdapter(this, R.layout.cell, friends);

        friendList.setAdapter(friendAdapter);

        // Sets a listener on the friend list and opens the details page for
        // the chosen friend.
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                Intent x = new Intent(MainActivity.this, DetailActivity.class);
                Log.d(TAG, "Detail activity will be started");
                BEFriend friend = friends.get(position);
                Log.d(TAG, "Detail friend Homelatitude: " + friend.getHomeLatitude() + "Homelongitude: " + friend.getHomeLongitude() );
                x.putExtra("friend", friend);
                x.putExtra("position",position);
                startActivityForResult(x,SECOND_ACTIVITY);
                Log.d(TAG, "Detail activity is started");
            }
        });

        // Sets a long click listener on the friend list. Get the position of a selected friend.
        // If the click is long the friend will be deleted.
        friendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                friendList.setLongClickable(true);
                BEFriend friend = friends.get(position);
                mDataAccess.delete(friend);
                fillList();
                return true;
            }
        });
    }

    // creates the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu1, menu);
    }

    // Triggers the selected item in the menu.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create1:
                Intent x = new Intent(this, DetailActivity.class);
                x.putExtra("position", friends.size());
                startActivityForResult(x, SECOND_ACTIVITY);
                return true;
            case R.id.delete1:
                mDataAccess.deleteAll();
                fillList();
            default:
                return super.onContextItemSelected(item);
        }
    }

    // Get result from Details Activity. If a user exists the user will be updated, if a user does not exist a new user will be created.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY) {
            switch (resultCode) {
                case RESULT_OK:
                    BEFriend updatedFriend = (BEFriend) data.getExtras().getSerializable("updatedFriend");
                    mDataAccess.update(updatedFriend);
                    Log.d("fff", "main: filepath: " + updatedFriend.getPhotoUrl());
                    fillList();
                    break;

                case RESULT_FIRST_USER:
                    BEFriend newFriend = (BEFriend) data.getExtras().getSerializable("newFriend");

                    mDataAccess.insert(newFriend);
                    Log.d("fff", "onActivityResult: id =" + newFriend.getId());
                    Log.d("fff", "onActivityResult: PhotoUrl = " + newFriend.getPhotoUrl());
                    fillList();
                case RESULT_CANCELED:
                    break;
                default:
            }
        }
    }

    // Fills the friend list. Used for updating the friend list after changes happen.
    void fillList() {
        friends = mDataAccess.selectAll();
        ArrayAdapter<BEFriend> a =
                new FriendAdapter(this, R.layout.cell, friends);
        friendList.setAdapter(a);
    }
}

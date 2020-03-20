package dk.easv.friendsv2;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public class MainActivity extends AppCompatActivity {
    private IDataAccess mDataAccess;
    public static String TAG = "Friend2";
    int SECOND_ACTIVITY = 2;

    List<BEFriend> friends;
    ArrayAdapter adapter;
    FriendAdapter friendAdapter;
    ListView friendList;
    TextView txt1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Friends v2");

        txt1 = findViewById(R.id.txt1);
        friendList = findViewById(R.id.list_friends);


        registerForContextMenu(txt1);

        mDataAccess = DataAccessFactory.getInstance(this);
        friends = mDataAccess.selectAll();

        friendAdapter = new FriendAdapter(this, R.layout.cell, friends);

        friendList.setAdapter(friendAdapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu1, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.create1:
                Intent x = new Intent(this, DetailActivity.class);
                Log.d(TAG, "Detail activity will be started");
                x.putExtra("position", friends.size());
                startActivityForResult(x, SECOND_ACTIVITY);
                return true;
            case R.id.delete1:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

  /*  @Override
    public void onListItemClick(ListView parent, View v, int position,
                                long id) {

        Intent x = new Intent(this, DetailActivity.class);
        Log.d(TAG, "Detail activity will be started");
        BEFriend friend = friends.get(position);
        addData(x, friend);
        x.putExtra("position",position);
        startActivityForResult(x,SECOND_ACTIVITY);
        Log.d(TAG, "Detail activity is started");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SECOND_ACTIVITY) {
            switch (resultCode) {
                case RESULT_OK:
                    BEFriend updatedFriend = (BEFriend) data.getExtras().getSerializable("updatedFriend");
                    int position = data.getExtras().getInt("position");
                    friends.set(position, updatedFriend);

                    Log.d("XYZ", updatedFriend.getName());
                    friendAdapter = new FriendAdapter(this,
                            R.layout.cell,
                            friends);

                    friendList.setAdapter(adapter);

                    break;
                case RESULT_FIRST_USER:
                    BEFriend newFriend = (BEFriend) data.getExtras().getSerializable("newFriend");
                    friends.add(newFriend);

                    friendAdapter = new FriendAdapter(this,
                            R.layout.cell,
                            friends);

                    friendList.setAdapter(adapter);
                case RESULT_CANCELED:
                    break;
                default:

            }
        }


    }

    private void addData(Intent x, BEFriend f) {
        x.putExtra("friend", f);
    }


}

package dk.easv.friendsv2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public class FriendAdapter extends ArrayAdapter<BEFriend> {
    // List of BEFriends gotten from the database
    private List<BEFriend> friendList;

    public FriendAdapter(Context context, int resource, List<BEFriend> friendList) {
        super(context, resource, friendList);
        this.friendList = friendList;
    }

    // Constructs the cells that will be added by the adapter
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.cell,null);
            Log.d("XYZ", "Position: " + position + " View created");
        } else {
            Log.d("XYZ", "Position: " + position + " View created");
        }

        BEFriend f = friendList.get(position);
        TextView friendLayout = v.findViewById(R.id.tv_friends);
        friendLayout.setText("Name: " + f.getName());
        ImageView friendImage = v.findViewById(R.id.civ_image);
        if (!f.getPhotoUrl().isEmpty()) {
        friendImage.setImageURI(Uri.parse(f.getPhotoUrl()));
        } else {
            friendImage.setImageResource(R.drawable.qmark);
        }

        return v;
    }
}

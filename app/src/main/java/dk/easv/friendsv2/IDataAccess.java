package dk.easv.friendsv2;

import java.util.List;

import dk.easv.friendsv2.Model.BEFriend;

public interface IDataAccess {
    long insert(BEFriend f);

    void deleteAll();

    List<BEFriend> selectAll();

    void update(BEFriend f);

    void delete(BEFriend f);
}

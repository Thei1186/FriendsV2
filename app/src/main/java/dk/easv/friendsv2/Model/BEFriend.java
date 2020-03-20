package dk.easv.friendsv2.Model;



import java.io.Serializable;

public class BEFriend implements Serializable {
    private int m_id;
    private String m_name;
    private String m_phone;
    private Boolean m_isFavorite;
    private String m_photoUrl;

    public BEFriend(int id, String name, String phone) {
        this(id,name, phone, false, "");
    }

    public BEFriend(int id, String name, String phone, boolean isFavorite) {
        this(id, name, phone, isFavorite, "");
    }

    public BEFriend(int id, String name, String phone, Boolean isFavorite, String photoUrl) {
        m_name = name;
        m_phone = phone;
        m_isFavorite = isFavorite;
        m_photoUrl = photoUrl;
    }
    public String getPhone() {
        return m_phone;
    }
    public String getPhotoUrl() { return m_photoUrl;}
    public void setPhotoUrl(String newUrl) { m_photoUrl = newUrl;}
    public int getId() {return m_id;}
    public String getName() {
        return m_name;
    }

    public Boolean isFavorite() { return m_isFavorite; }


}

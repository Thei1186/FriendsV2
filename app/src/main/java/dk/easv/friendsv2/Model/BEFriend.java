package dk.easv.friendsv2.Model;



import java.io.Serializable;

public class BEFriend implements Serializable {
    private long m_id;
    private String m_name;
    private String m_phone;
    private Boolean m_isFavorite;
    private String m_photoUrl;

    public BEFriend(long id, String name, String phone) {
        this(id,name, phone, false, "");
    }

    public BEFriend(long id, String name, String phone, boolean isFavorite) {
        this(id, name, phone, isFavorite, "");
    }

    public BEFriend(long id, String name, String phone, Boolean isFavorite, String photoUrl) {
        m_id = id;
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
    public String getName() {
        return m_name;
    }
    public void setId(long id) { m_id = id;}
    public long getId() { return m_id;}
    public Boolean isFavorite() { return m_isFavorite; }

    @Override
    public String toString() {
        return
                "id=" + m_id +
                ", name='" + m_name + '\'' +
                ", phone='" + m_phone + '\'' +
                ", isFavorite=" + m_isFavorite +
                ", photoUrl='" + m_photoUrl + '\'';
    }
}

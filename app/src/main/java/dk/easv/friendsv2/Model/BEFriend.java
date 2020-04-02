package dk.easv.friendsv2.Model;



import java.io.Serializable;

public class BEFriend implements Serializable {
    private long m_id;
    private String m_name;
    private String m_phone;
    private Boolean m_isFavorite;
    private String m_photoUrl;
    private String m_Url;
    private double m_longitude;
    private double m_latitude;
    private double m_homeLatitude;
    private double m_homeLongitude;

    public BEFriend(long id, String name, String phone, Boolean isFavorite, String photoUrl) {
        this(id,name,phone,isFavorite,photoUrl, 0.0,0.0);
    }

    public BEFriend(long id, String name, String phone, Boolean isFavorite, String photoUrl,
                    double homeLatitude, double homeLongitude) {

        m_id = id;
        m_name = name;
        m_phone = phone;
        m_isFavorite = isFavorite;
        m_photoUrl = photoUrl;
        m_homeLatitude = homeLatitude;
        m_homeLongitude = homeLongitude;
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
        return "BEFriend{" +
                "m_id=" + m_id +
                ", m_name='" + m_name + '\'' +
                ", m_phone='" + m_phone + '\'' +
                ", m_isFavorite=" + m_isFavorite +
                ", m_photoUrl='" + m_photoUrl + '\'' +
                ", m_Url='" + m_Url + '\'' +
                ", m_longitude=" + m_longitude +
                ", m_latitude=" + m_latitude +
                ", m_homeLatitude=" + m_homeLatitude +
                ", m_homeLongitude=" + m_homeLongitude +
                '}';
    }

    public String getUrl() {
        return m_Url;
    }

    public void setUrl(String m_Url) {
        this.m_Url = m_Url;
    }

    public double getLongitude() {
        return m_longitude;
    }

    public void setLongitude(double longitude) {
        this.m_longitude = longitude;
    }

    public double getLatitude() {
        return m_latitude;
    }

    public void setLatitude(double latitude) {
        this.m_latitude = latitude;
    }

    public double getHomeLatitude() {
        return m_homeLatitude;
    }

    public void setHomeLatitude(double m_homeLatitude) {
        this.m_homeLatitude = m_homeLatitude;
    }

    public double getHomeLongitude() {
        return m_homeLongitude;
    }

    public void setHomeLongitude(double m_homeLongitude) {
        this.m_homeLongitude = m_homeLongitude;
    }
}

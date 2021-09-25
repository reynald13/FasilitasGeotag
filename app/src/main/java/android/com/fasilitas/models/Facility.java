package android.com.fasilitas.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Facility implements Parcelable {

    //Facility Objek

    private String title;
    private String content;
    private @ServerTimestamp
    Date timestamp;
    private GeoPoint geo_point;
    private String facility_id;
    private String user_id;
    private User user;

    public Facility(String title, String content, Date timestamp, GeoPoint geo_point, String facility_id, String user_id, User user) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.geo_point = geo_point;
        this.facility_id = facility_id;
        this.user_id = user_id;
        this.user = user;
    }

    public Facility() {

    }

    protected Facility(Parcel in) {
        title = in.readString();
        content = in.readString();
        facility_id = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Facility> CREATOR = new Creator<Facility>() {
        @Override
        public Facility createFromParcel(Parcel in) {
            return new Facility(in);
        }

        @Override
        public Facility[] newArray(int size) {
            return new Facility[size];
        }
    };

    public static Creator<Facility> getCREATOR() {
        return CREATOR;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(facility_id);
        parcel.writeString(user_id);
    }

    @Override
    public String toString() {
        return "Facility{" +
                "title=" + title +'\'' +
                ", content=" + content + '\'' +
                ", geopoint=" + geo_point + '\'' +
                ", facility_id=" + facility_id + '\'' +
                ", user_id=" + user_id + '\'' +
                ", user=" + user + '\'' +
                '}';
    }
}

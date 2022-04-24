package com.barmej.streetissues.Objects;

 import android.os.Parcel;
 import android.os.Parcelable;

 import com.google.firebase.firestore.GeoPoint;

public class Issue implements Parcelable {

    private String Title;
    private String details;
    private GeoPoint location;
    private String photo;

    public Issue(String title, String details, GeoPoint location, String photo) {
        Title = title;
        this.details = details;
        this.location = location;
        this.photo = photo;
    }

    public Issue() {
    }

    protected Issue(Parcel in) {
        Title = in.readString();
        details = in.readString();
        photo = in.readString();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Title);
        parcel.writeString(details);
        parcel.writeString(photo);
    }
}

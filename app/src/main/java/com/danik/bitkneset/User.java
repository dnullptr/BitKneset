package com.danik.bitkneset;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String username;
    String password;
    int accessLevel;
    String fullName;
    boolean Connected;

    public User(String username, String password, int accessLevel, String fullName) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
        this.fullName = fullName;
    }

    public User(String username, String password, int accessLevel) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }


    protected User(Parcel in) {
        username = in.readString();
        password = in.readString();
        accessLevel = in.readInt();
        fullName = in.readString();
        Connected = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isConnected() { return Connected; }

    public boolean compareTo(User comp)
    {
        return this.username.equals(comp.username) && this.password.equals(comp.password) && this.accessLevel==comp.accessLevel; //this func will help checking login with two "users" - one from DB and other claimed by user form
        //if it's true , login is successful
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(accessLevel);
        dest.writeString(fullName);
        dest.writeByte((byte) (Connected ? 1 : 0));
    }
}

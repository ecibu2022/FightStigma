package com.example.fightstigma;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class PostModal implements Parcelable {
    String key, postID, userID, date, imageURL, title, body;
    private long likeCount;
    private HashMap<String, Comment> comments;

    public PostModal() {
        this.comments = null;
    }

    public PostModal(String postID, String userID, String date, String imageURL, String title, String body) {
        this.postID=postID;
        this.userID=userID;
        this.date=date;
        this.imageURL = imageURL;
        this.title = title;
        this.body = body;
        this.likeCount=0;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    protected PostModal(Parcel in) {
        postID=in.readString();
        title = in.readString();
        body = in.readString();
        userID = in.readString();
        date = in.readString();
        imageURL = in.readString();
        likeCount = in.readLong();
    }

    public static final Creator<PostModal> CREATOR = new Creator<PostModal>() {
        @Override
        public PostModal createFromParcel(Parcel in) {
            return new PostModal(in);
        }

        @Override
        public PostModal[] newArray(int size) {
            return new PostModal[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(userID);
        dest.writeString(date);
        dest.writeString(imageURL);
        dest.writeLong(likeCount);
    }

    public HashMap<String, Comment> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

}

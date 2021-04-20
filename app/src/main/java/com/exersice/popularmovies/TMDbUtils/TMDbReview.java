package com.exersice.popularmovies.TMDbUtils;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TMDbReview implements Parcelable {
    private static final String TAG = TMDbReview.class.getSimpleName();

    private final String id;
    private final String author;
    private final String content;
    private final String url;

    protected TMDbReview(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        author = jsonObject.getString("author");
        content = jsonObject.getString("content");
        url = jsonObject.getString("url");
    }

    protected TMDbReview(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TMDbReview> CREATOR = new Creator<TMDbReview>() {
        @Override
        public TMDbReview createFromParcel(Parcel in) {
            return new TMDbReview(in);
        }

        @Override
        public TMDbReview[] newArray(int size) {
            return new TMDbReview[size];
        }
    };

    public String getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getContent() {
        return content;
    }
    public String getUrl() {
        return url;
    }
}
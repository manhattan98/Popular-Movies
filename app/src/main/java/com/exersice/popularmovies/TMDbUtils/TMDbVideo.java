package com.exersice.popularmovies.TMDbUtils;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class TMDbVideo implements Parcelable {
    private static final String TAG = TMDbVideo.class.getSimpleName();

    private final String id;
    private final String iso_639_1;
    private final String iso_3166_1;
    private final String key;
    private final String name;
    private final String site;
    private final int size;
    private final String type;

    protected TMDbVideo(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        iso_639_1 = jsonObject.getString("iso_639_1");
        iso_3166_1 = jsonObject.getString("iso_3166_1");
        key = jsonObject.getString("key");
        name = jsonObject.getString("name");
        site = jsonObject.getString("site");
        size = jsonObject.getInt("size");
        type = jsonObject.getString("type");
    }

    protected TMDbVideo(Parcel in) {
        id = in.readString();
        iso_639_1 = in.readString();
        iso_3166_1 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(iso_639_1);
        dest.writeString(iso_3166_1);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TMDbVideo> CREATOR = new Creator<TMDbVideo>() {
        @Override
        public TMDbVideo createFromParcel(Parcel in) {
            return new TMDbVideo(in);
        }

        @Override
        public TMDbVideo[] newArray(int size) {
            return new TMDbVideo[size];
        }
    };

    public String getId() {
        return id;
    }
    public String getIso_639_1() {
        return iso_639_1;
    }
    public String getIso_3166_1() {
        return iso_3166_1;
    }
    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getSite() {
        return site;
    }
    public int getSize() {
        return size;
    }
    public String getType() {
        return type;
    }
}
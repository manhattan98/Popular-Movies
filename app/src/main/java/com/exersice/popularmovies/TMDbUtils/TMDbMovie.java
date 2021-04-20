package com.exersice.popularmovies.TMDbUtils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class TMDbMovie implements Parcelable {
    private static final String TAG = TMDbMovie.class.getSimpleName();

    @Nullable
    private final String poster_path;
    private final boolean adult;
    private final String overview;
    private final String release_date;
    private final int[] genre_ids;
    private final int id;
    private final String original_title;
    private final String original_language;
    private final String title;
    @Nullable
    private final String backdrop_path;
    private final int popularity;
    private final int vote_count;
    private final boolean video;
    private final int vote_average;

    protected TMDbMovie(JSONObject jsonObject) throws JSONException {
        poster_path = jsonObject.getString("poster_path");
        adult = jsonObject.getBoolean("adult");
        overview = jsonObject.getString("overview");
        release_date = jsonObject.getString("release_date");

        JSONArray json_genre_ids = jsonObject.getJSONArray("genre_ids");
        genre_ids = new int[json_genre_ids.length()];
        for (int i = 0; i < json_genre_ids.length(); i++)
            genre_ids[i] = json_genre_ids.getInt(i);

        id = jsonObject.getInt("id");
        original_title = jsonObject.getString("original_title");
        original_language = jsonObject.getString("original_language");
        title = jsonObject.getString("title");
        backdrop_path = jsonObject.getString("backdrop_path");
        popularity = jsonObject.getInt("popularity");
        vote_count = jsonObject.getInt("vote_count");
        video = jsonObject.getBoolean("video");
        vote_average = jsonObject.getInt("vote_average");
    }

    protected TMDbMovie(Parcel parcel) {
        poster_path = parcel.readString();
        adult = parcel.readInt() == 1;
        overview = parcel.readString();
        release_date = parcel.readString();

        genre_ids = new int[parcel.readInt()];
        parcel.readIntArray(genre_ids);

        id = parcel.readInt();
        original_title = parcel.readString();
        original_language = parcel.readString();
        title = parcel.readString();
        backdrop_path = parcel.readString();
        popularity = parcel.readInt();
        vote_count = parcel.readInt();
        video = parcel.readInt() == 1;
        vote_average = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeInt(adult ? 1 : 0);
        dest.writeString(overview);
        dest.writeString(release_date);

        dest.writeInt(genre_ids.length);
        dest.writeIntArray(genre_ids);

        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeInt(popularity);
        dest.writeInt(vote_count);
        dest.writeInt(video ? 1 : 0);
        dest.writeInt(vote_average);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TMDbMovie> CREATOR = new Creator<TMDbMovie>() {
        @Override
        public TMDbMovie createFromParcel(Parcel source) {
            return new TMDbMovie(source);
        }

        @Override
        public TMDbMovie[] newArray(int size) {
            return new TMDbMovie[size];
        }
    };

    @Nullable
    public String getPosterPath() {
        return poster_path;
    }
    public boolean isAdult() {
        return adult;
    }
    public String getOverview() {
        return overview;
    }
    public String getReleaseDate() {
        return release_date;
    }
    public int[] getGenreIds() {
        return genre_ids;
    }
    public int getId() {
        return id;
    }
    public String getOriginalTitle() {
        return original_title;
    }
    public String getOriginalLanguage() {
        return original_language;
    }
    public String getTitle() {
        return title;
    }
    @Nullable
    public String getBackdropPath() {
        return backdrop_path;
    }
    public int getPopularity() {
        return popularity;
    }
    public int getVoteCount() {
        return vote_count;
    }
    public boolean isVideo() {
        return video;
    }
    public int getVoteAverage() {
        return vote_average;
    }
    public int getYear() {
        return Integer.parseInt(release_date.substring(0, 4));
    }
    public int getMonth() {
        return Integer.parseInt(release_date.substring(5, 7));
    }
    public int getDayOfMonth() {
        return Integer.parseInt(release_date.substring(8, 10));
    }
}
package com.exersice.popularmovies;

import android.net.Uri;

public abstract class YouTubeUtils {
    private final static String YOUTUBE_BASE_IMG_URL = "https://img.youtube.com/vi";
    private final static String YOUTUBE_BASE_WATCH_URL = "https://youtube.com/watch/";

    private final static String YOUTUBE_QUERY_VIDEO = "v";

    public enum THUMBNAIL_SIZE {
        hqdefault, mqdefault, sddefault, maxresdefault;
    }


    public static Uri getImageUri(String videoId) {
        return Uri.parse(YOUTUBE_BASE_IMG_URL).buildUpon()
                .appendPath(videoId)
                .appendPath("default.jpg")
                .build();
    }
    public static Uri getImageUri(String videoId, THUMBNAIL_SIZE size) {
        return Uri.parse(YOUTUBE_BASE_IMG_URL).buildUpon()
                    .appendPath(videoId)
                    .appendPath(size.toString() + ".jpg")
                    .build();
    }

    public static Uri getWatchUri(String videoId) {
        return Uri.parse(YOUTUBE_BASE_WATCH_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_QUERY_VIDEO, videoId)
                .build();
    }

}

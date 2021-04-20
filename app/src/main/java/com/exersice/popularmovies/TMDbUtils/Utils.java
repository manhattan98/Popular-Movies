package com.exersice.popularmovies.TMDbUtils;

import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public abstract class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private static final String API_KEY_V3 = "29b1a5c8be0fa23b8155f30f84c81ce5";
    private static final String API_READ_ACCESS_TOKEN_V4 = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyOWIxYTVjOGJlMGZhMjNiODE1NWYzMGY4NGM4MWNlNSIsInN1YiI6IjVlOWVjOWUwMjc5MGJmMDAyNTA3YjE1ZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XEUTJWSktMm4z8x0EkKrxVjmcURCuoNXlUI-4mu8xxM";

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String QUERY_API_KEY = "api_key";
    private static final String QUERY_LANGUAGE = "language";
    private static final String QUERY_PAGE = "page";
    private static final String QUERY_REGION = "region";

    public static final String LANGUAGE_DEFAULT = "en-US";
    public static final int PAGE_DEFAULT = 1;
    public static final String REGION_DEFAULT = "US";

    public static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/";

    public static class TMDbPopularResponse {
        private final int page;
        private final TMDbMovie[] results;
        private final int total_results;
        private final int total_pages;

        protected TMDbPopularResponse(JSONObject jsonObject) throws JSONException {
            this.page = jsonObject.getInt("page");

            JSONArray jsonResults = jsonObject.getJSONArray("results");
            this.results = new TMDbMovie[jsonResults.length()];
            for (int i = 0; i < results.length; i++)
                this.results[i] = new TMDbMovie(jsonResults.getJSONObject(i));

            this.total_results = jsonObject.getInt("total_results");
            this.total_pages = jsonObject.getInt("total_pages");
        }

        public int getPage() {
            return this.page;
        }
        public TMDbMovie[] getResults() {
            return this.results;
        }
        public int getTotalResults() {
            return this.total_results;
        }
        public int getTotalPages() {
            return this.total_pages;
        }
    }

    public static class TMDbTopRatedResponse {
        private final int page;
        private final TMDbMovie[] results;
        private final int total_results;
        private final int total_pages;

        protected TMDbTopRatedResponse(JSONObject jsonObject) throws JSONException {
            this.page = jsonObject.getInt("page");

            JSONArray jsonResults = jsonObject.getJSONArray("results");
            this.results = new TMDbMovie[jsonResults.length()];
            for (int i = 0; i < results.length; i++)
                this.results[i] = new TMDbMovie(jsonResults.getJSONObject(i));

            this.total_results = jsonObject.getInt("total_results");
            this.total_pages = jsonObject.getInt("total_pages");
        }

        public int getPage() {
            return this.page;
        }
        public TMDbMovie[] getResults() {
            return this.results;
        }
        public int getTotalResults() {
            return this.total_results;
        }
        public int getTotalPages() {
            return this.total_pages;
        }
    }

    public static class TMDbReviewsResponse {
        private final int id;
        private final int page;
        private final TMDbReview[] results;
        private final int total_pages;
        private final int total_results;

        protected TMDbReviewsResponse(JSONObject jsonObject) throws JSONException {
            this.id = jsonObject.getInt("id");
            this.page = jsonObject.getInt("page");

            JSONArray jsonResults = jsonObject.getJSONArray("results");
            this.results = new TMDbReview[jsonResults.length()];
            for (int i = 0; i < results.length; i++)
                this.results[i] = new TMDbReview(jsonResults.getJSONObject(i));

            this.total_pages = jsonObject.getInt("total_pages");
            this.total_results = jsonObject.getInt("total_results");
        }

        public int getId() {
            return this.id;
        }
        public int getPage() {
            return this.page;
        }
        public TMDbReview[] getResults() {
            return this.results;
        }
        public int getTotalPages() {
            return this.total_pages;
        }
        public int getTotalResults() {
            return this.total_results;
        }
    }

    public static class TMDbVideosResponse {
        private final int id;
        private final TMDbVideo[] results;

        protected TMDbVideosResponse(JSONObject jsonObject) throws JSONException {
            this.id = jsonObject.getInt("id");

            JSONArray jsonResults = jsonObject.getJSONArray("results");
            this.results = new TMDbVideo[jsonResults.length()];
            for (int i = 0; i < results.length; i++)
                this.results[i] = new TMDbVideo(jsonResults.getJSONObject(i));
        }

        public int getId() {
            return this.id;
        }
        public TMDbVideo[] getResults() {
            return this.results;
        }
    }

    private static String buildErrorMsg(Exception E, String methodName) {
        return "ERROR while " + methodName + ": " + E.getClass().getSimpleName() + " (" + E.getMessage() + ")";
    }

    private static JSONObject getObjectFromURL(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        Scanner scanner = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            scanner = new Scanner(urlConnection.getInputStream());

            //scanner.useDelimiter("\\A");
            if (scanner.hasNext())
                return new JSONObject(scanner.nextLine());
            else
                return null;

        } catch (JSONException E) {
            Log.e(TAG, buildErrorMsg(E, "getObjectFromURL()"));
            return null;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (scanner != null) scanner.close();
        }
    }

    public static TMDbReviewsResponse getReviews(int movieId, int page) throws IOException {
        return getReviews(movieId, LANGUAGE_DEFAULT, page);
    }
    public static TMDbReviewsResponse getReviews(int movieId, String language, int page) throws IOException {
        final String requestPath = "reviews";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(requestPath)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3)
                .appendQueryParameter(QUERY_LANGUAGE, language)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(page))
                .build();
        try {
            URL url = new URL(builtUri.toString());

            JSONObject receivedJson = getObjectFromURL(url);

            return new TMDbReviewsResponse(receivedJson);
        } catch (MalformedURLException | JSONException E) {
            Log.e(TAG, buildErrorMsg(E, "getReviews()"));
            return null;
        }
    }


    public static TMDbVideosResponse getVideos(int movieId) throws IOException {
        return getVideos(movieId, LANGUAGE_DEFAULT);
    }
    public static TMDbVideosResponse getVideos(int movieId, String language) throws IOException {
        final String requestPath = "videos";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(requestPath)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3)
                .appendQueryParameter(QUERY_LANGUAGE, language)
                .build();
        try {
            URL url = new URL(builtUri.toString());

            JSONObject receivedJson = getObjectFromURL(url);

            return new TMDbVideosResponse(receivedJson);
        } catch (MalformedURLException | JSONException E) {
            Log.e(TAG, buildErrorMsg(E, "getVideos()"));
            return null;
        }
    }


    public static TMDbPopularResponse getPopular(int page) throws IOException {
        return getPopular(LANGUAGE_DEFAULT, page, REGION_DEFAULT);
    }
    public static TMDbPopularResponse getPopular(String language, int page) throws IOException {
        return getPopular(language, page, REGION_DEFAULT);
    }
    public static TMDbPopularResponse getPopular(String language, int page, String region) throws IOException {
        final String requestPath = "popular";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(requestPath)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3)
                .appendQueryParameter(QUERY_LANGUAGE, language)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(page))
                .appendQueryParameter(QUERY_REGION, region)
                .build();
        try {
            URL url = new URL(builtUri.toString());

            JSONObject receivedJson = getObjectFromURL(url);

            return new TMDbPopularResponse(receivedJson);
        } catch (MalformedURLException | JSONException E) {
            Log.e(TAG, buildErrorMsg(E, "getPopular()"));
            return null;
        }
    }


    public static TMDbTopRatedResponse getTopRated(int page) throws IOException {
        return getTopRated(LANGUAGE_DEFAULT, page, REGION_DEFAULT);
    }
    public static TMDbTopRatedResponse getTopRated(String language, int page) throws IOException {
        return getTopRated(language, page, REGION_DEFAULT);
    }
    public static TMDbTopRatedResponse getTopRated(String language, int page, String region) throws IOException {
        final String requestPath = "top_rated";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(requestPath)
                .appendQueryParameter(QUERY_API_KEY, API_KEY_V3)
                .appendQueryParameter(QUERY_LANGUAGE, language)
                .appendQueryParameter(QUERY_PAGE, String.valueOf(page))
                .appendQueryParameter(QUERY_REGION, region)
                .build();
        try {
            URL url = new URL(builtUri.toString());

            JSONObject receivedJson = getObjectFromURL(url);

            return new TMDbTopRatedResponse(receivedJson);
        } catch (MalformedURLException | JSONException E) {
            Log.e(TAG, buildErrorMsg(E, "getPopular()"));
            return null;
        }
    }

    public static Uri getPosterUri(String path) {
        return getPosterUri(path, POSTER_SIZE.original);
    }
    public static Uri getPosterUri(String path, POSTER_SIZE size) {
        return Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendPath(size.toString())
                // substring for deleting slash at start position
                .appendPath(path.substring(1))
                .build();
    }

    public enum POSTER_SIZE {
        w92, w154, w185, w342, w500, w780, original;
    }

    public static String formatDateToString(TMDbMovie movie) {
        String pattern = "LLLL d, yyyy";
        return formatDateToString(pattern, movie);
    }
    public static String formatDateToString(String pattern, TMDbMovie movie) {
        Calendar calendar = new GregorianCalendar(movie.getYear(), movie.getMonth(), movie.getDayOfMonth());

        return DateFormat.format(pattern, calendar.getTime()).toString();
    }

    public static String formatVoteString(int TMDbVote) {
        String voteString = TMDbVote + ".0/10";
        return voteString;
    }
}

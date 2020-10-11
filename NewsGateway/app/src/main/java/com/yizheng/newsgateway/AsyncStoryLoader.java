package com.yizheng.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncStoryLoader extends AsyncTask<Source, Void, ArrayList<Story>> {

    private static final String apiBase = "https://newsapi.org/v2/top-headlines";
    private static final String key = "e4df0b6ea89c4d3f8f0d45f82313f952";
    private MainActivity mainActivity;

    private static HashMap<String, ArrayList<Story>> cachedStories = new HashMap<>();

    public AsyncStoryLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(ArrayList<Story> storyList) {
        mainActivity.setStories(storyList);
    }

    @Override
    protected ArrayList<Story> doInBackground(Source... sources) {
        String id = sources[0].getId();

        if (cachedStories.containsKey(id))
            return cachedStories.get(id);

        Uri.Builder builder = Uri.parse(apiBase).buildUpon();
        builder.appendQueryParameter("sources", id);
        builder.appendQueryParameter("apiKey", key);
        String urlToUse = builder.build().toString();

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        ArrayList<Story> storyList = parseJSON(sb.toString());
        cachedStories.put(id, storyList);
        return storyList;

    }

    private ArrayList<Story> parseJSON(String s){
        ArrayList<Story> storyList = new ArrayList<>();
        try{
            JSONObject jo = new JSONObject(s);
            JSONArray jsonArray = jo.getJSONArray("articles");
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jStory = jsonArray.getJSONObject(i);
                String author = jStory.getString("author");
                String title = jStory.getString("title");
                String description = jStory.getString("description");
                String url = jStory.getString("url");
                String urlToImage = jStory.getString("urlToImage");
                String publishedAt = jStory.getString("publishedAt");
                storyList.add(new Story(author, title, description, url, urlToImage, publishedAt));
            }
            return storyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

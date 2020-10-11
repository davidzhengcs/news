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
import java.util.HashSet;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncSourceLoader extends AsyncTask<String, Void, String> {

    //static final String key = "e4df0b6ea89c4d3f8f0d45f82313f952";

    private static final String sourceUrl = "https://newsapi.org/v2/sources?apiKey=e4df0b6ea89c4d3f8f0d45f82313f952";

    MainActivity mainActivity;

    static HashSet<String> topics = new HashSet<>();
    static HashSet<String> languages = new HashSet<>();
    static HashSet<String> countries = new HashSet<>();

    static HashMap<String, Integer> topicToColor = new HashMap<>();

    public AsyncSourceLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPostExecute(String s){
//        if (s == null) {
//            mainActivity.dataDownloadFailed();
//            return;
//        }
        ArrayList<Source> sources = parseJSON(s);
        if (sources != null){
            mainActivity.setupSources(sources);
        }

    }

    @Override
    protected String doInBackground(String... strings) {
        Uri uri = Uri.parse(sourceUrl);
        String urlTOUse = uri.toString();
         StringBuilder sb = new StringBuilder();
         try{
             URL url = new URL(urlTOUse);
             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
             conn.setRequestMethod("GET");

             if (conn.getResponseCode() == HTTP_OK){
                 InputStream is = conn.getInputStream();
                 BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                 String line;
                 while ((line = reader.readLine()) != null) {
                     sb.append(line).append('\n');
                 }
             } else {
                 return null;
             }
         }catch (Exception e) {
             e.printStackTrace();
             return null;
         }
         return sb.toString();
    }

    private ArrayList<Source> parseJSON(String s){
        ArrayList<Source> sources = new ArrayList<>();

        try{
            JSONObject jo = new JSONObject(s);
            JSONArray jSources = jo.getJSONArray("sources");
            for(int i=0; i<jSources.length(); i++){
                JSONObject jSource = jSources.getJSONObject(i);
                String id = jSource.getString("id");
                String name = jSource.getString("name");
                String category = jSource.getString("category");
                topicToColor.put(category, Utility.generateRandomColor());
                String lang = jSource.getString("language");
                String country = jSource.getString("country");
                Source source = new Source(id,name,category,lang,country);
                sources.add(source);
                topics.add(category);
                languages.add(lang);
                countries.add(country);
            }
            return sources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

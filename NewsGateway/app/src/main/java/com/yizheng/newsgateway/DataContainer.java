package com.yizheng.newsgateway;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DataContainer {

    private static HashMap<String, String> codesToCountries = new HashMap<>();
    private static HashMap<String, String> countriesToCodes = new HashMap<>();
    private static HashMap<String, String> codesToLang = new HashMap<>();
    private static HashMap<String, String> langToCodes = new HashMap<>();

//    static HashMap<String, String> getCodesToCountries() {
//        return codesToCountries;
//    }

    static String getCountry(String s){
        return codesToCountries.get(s);
    }

    static String getLang(String s){
        return codesToLang.get(s);
    }

    static String getCountryCode(String countryName){
        if (countriesToCodes.containsKey(countryName)) {
            return countriesToCodes.get(countryName);
        }
        return null;
    }

    static String getLangCode(String langName){
        if (langToCodes.containsKey(langName)){
            return langToCodes.get(langName);
        }
        return null;
    }

//    static HashMap<String, String> getCountriesToCodes() {
//        return countriesToCodes;
//    }

    static void loadData(MainActivity context){
        try{
            JSONObject jsonObjectC = loadJSONData(context, "country");
            JSONObject jsonObjectL = loadJSONData(context, "language");
            parseJSON(jsonObjectC, "country");
            parseJSON(jsonObjectL, "language");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static void parseJSON(JSONObject jobj, String fname) throws JSONException{
        JSONArray jsonArray = null;
        if(fname.equals("country")) {
            jsonArray = jobj.getJSONArray("countries");
        }
        else if (fname.equals("language")){
            jsonArray = jobj.getJSONArray("languages");
        }
        for (int i=0; i<jsonArray.length(); i++){
            JSONObject jo = jsonArray.getJSONObject(i);
            String code = jo.getString("code");
            String name = jo.getString("name");
            if (fname.equals("country")) {
                codesToCountries.put(code.toUpperCase(), name);
                countriesToCodes.put(name, code.toUpperCase());
            }
            else if (fname.equals("language")){
                codesToLang.put(code, name);
                langToCodes.put(name, code);
            }
        }
    }

    private static JSONObject loadJSONData(Context context, String fname) throws IOException, JSONException {
        InputStream is = null;
        if (fname.equals("country")) {
            is = context.getResources().openRawResource(R.raw.country_codes);
        }
        else if (fname.equals("language")){
            is = context.getResources().openRawResource(R.raw.language_codes);
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        if (is != null) {
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        }
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        return new JSONObject(sb.toString());
    }

}

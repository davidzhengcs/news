package com.yizheng.newsgateway;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Menu opt_menu;
    private SubMenu topicsMenu, countriesMenu, languagesMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Fragment> fragments = new ArrayList<>();
    private NewsSourcePagerAdapter newsSourcePagerAdapter;
    private ViewPager pager;

    private ArrayList<Source> sources;
    private ArrayList<Source> displayedSources = new ArrayList<>();
    private ArrayList<SpannableString> displayedStrings = new ArrayList<>();
    private ArrayList<Source> sourcesSameTopic = new ArrayList<>();

    private String currentSubMenuString;

    private Source currentSource;
    private int currentPosition;
    //int currentItem = 0;

    String topic = "all";
    String country = "all";
    String lang = "all";


    static ArrayList<String> topics, countries, languages;

    static HashMap<String, ArrayList<Source>> sourceData = new HashMap<>();

    private HashMap<String, String> codesToCountries, countriesToCodes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataContainer.loadData(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        newsSourcePagerAdapter = new NewsSourcePagerAdapter(getSupportFragmentManager(), fragments);
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(newsSourcePagerAdapter);


        new AsyncSourceLoader(this).execute();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("topic", topic);
        outState.putString("country", country);
        outState.putString("lang", lang);
        outState.putInt("currentPosition", currentPosition);

        //currentItem = pager.getCurrentItem();
        //outState.putInt("currentItem", currentItem);

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        topic = savedInstanceState.getString("topic");
        country = savedInstanceState.getString("country");
        lang = savedInstanceState.getString("lang");
        currentPosition = savedInstanceState.getInt("currentPosition");
        //currentItem = savedInstanceState.getInt("currentItem");
    }

    public void setStories(ArrayList<Story> storyList) {
        setTitle(currentSource.getName());

        for (int i = 0; i < newsSourcePagerAdapter.getCount(); i++) {
            newsSourcePagerAdapter.notifyChangeInPosition(i);
        }

        fragments.clear();

        for (int i = 0; i < storyList.size(); i++) {
            fragments.add(StoryFragment.newInstance(storyList.get(i), i + 1, storyList.size()));
        }

        newsSourcePagerAdapter.notifyDataSetChanged();

        //pager.setCurrentItem(currentItem);/////////////
        pager.setCurrentItem(0);
        //this.currentItem = 0;////////////////
    }

    private void selectItem(int position) {
        pager.setBackground(null);

        currentPosition = position;
        currentSource = displayedSources.get(position);

        new AsyncStoryLoader(this).execute(currentSource);

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        if (item.hasSubMenu()) {
            currentSubMenuString = item.getSubMenu().getItem().toString();
            return super.onOptionsItemSelected(item);
        }
        if (sourcesSameTopic == null || sourcesSameTopic.isEmpty()) {
            sourcesSameTopic = sourceData.get("all");
        }
        String selection = item.getTitle().toString();
        if (currentSubMenuString.equals("Topics")) {
            topic = item.getTitle().toString();
            sourcesSameTopic = sourceData.get(selection);
            displayedSources = new ArrayList<>(sourceData.get(selection));

            if (!country.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getCountry().toUpperCase().equals(country)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
            if (!lang.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getLang().toUpperCase().equals(lang)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
        } else if (currentSubMenuString.equals("Countries")) {
            country = DataContainer.getCountryCode(selection);
            if (country == null && selection.equals("all")) {
                country = "all";
            }
            displayedSources = new ArrayList<>(sourcesSameTopic);

            if (!country.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getCountry().toUpperCase().equals(country)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
            if (!lang.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getLang().toUpperCase().equals(lang)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
        } else if (currentSubMenuString.equals("Languages")) {
            lang = DataContainer.getLangCode(selection);
            if (lang == null && selection.equals("all")) {
                lang = "all";
            }
            displayedSources = new ArrayList<>(sourcesSameTopic);

            if (!country.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getCountry().toUpperCase().equals(country)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
            if (!lang.equals("all")) {
                ArrayList<Source> found = new ArrayList<>();
                for (Source s : sourcesSameTopic) {
                    if (!s.getLang().toUpperCase().equals(lang)) {
                        found.add(s);
                    }
                }
                displayedSources.removeAll(found);
            }
        }

        displayedStrings.clear();
        for (Source s : displayedSources) {
            displayedStrings.add(s.getColoredName());
        }
        setTitle(getString(R.string.app_name) + " (" + displayedStrings.size() + ")");

        //dialog box warning
        if (displayedStrings.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                        }

                    });
            builder.setMessage("No match for " + topic + ", " + DataContainer.getCountry(country) + ", " + DataContainer.getLang(lang));
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();

//        Log.d(TAG, "onOptionsItemSelected: "+selection);

        return super.onOptionsItemSelected(item);
    }

    void setupSources(ArrayList<Source> allsources) {
        this.sources = allsources;
        displayedSources.clear();
        displayedStrings.clear();
        displayedSources.addAll(allsources);
        for (Source s : displayedSources) {
            displayedStrings.add(s.getColoredName());
        }

        for (Source s : sources) {
            if (!sourceData.containsKey(s.getTopic())) {
                sourceData.put(s.getTopic(), new ArrayList<Source>());
            }
            ArrayList<Source> sublist = sourceData.get(s.getTopic());
            if (sublist != null) {
                sublist.add(s);
            }
        }
        sourceData.put("all", new ArrayList<Source>(sources));

        if (!topic.equals("all") || !country.equals("all") || !lang.equals("all")) {
            sourcesSameTopic = sourceData.get(topic);
            ArrayList<Source> found = new ArrayList<>();
            for (Source s : sourcesSameTopic) {
                if (!s.getCountry().toUpperCase().equals(country) || !s.getLang().toUpperCase().equals(lang)) {
                    found.add(s);
                }
            }
            displayedSources.removeAll(found);
            displayedStrings.clear();
            for (Source s : displayedSources) {
                displayedStrings.add(s.getColoredName());
            }
            selectItem(currentPosition);
        }

        topics = new ArrayList<>(AsyncSourceLoader.topics);
        countries = new ArrayList<>(AsyncSourceLoader.countries);
        languages = new ArrayList<>(AsyncSourceLoader.languages);
        Collections.sort(topics);

        topicsMenu.add("all");
        for (String s : topics) {
            SpannableString coloredS = new SpannableString(s);
            coloredS.setSpan(new ForegroundColorSpan(AsyncSourceLoader.topicToColor.get(s)), 0, coloredS.length(), 0);
            topicsMenu.add(coloredS);
        }

        ArrayList<String> tempList = new ArrayList<>();
        for (String s : countries) {
            String country = DataContainer.getCountry(s.toUpperCase());
            tempList.add(country);
        }
        Collections.sort(tempList);
        countriesMenu.add("all");
        for (String s : tempList) {
            countriesMenu.add(s);
        }
        tempList.clear();
        for (String s : languages) {
            tempList.add(DataContainer.getLang(s.toUpperCase()));
        }
        Collections.sort(tempList);
        languagesMenu.add("all");
        for (String s : tempList) {
            languagesMenu.add(s);
        }

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_item, displayedStrings));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setTitle(getString(R.string.app_name) + " (" + displayedStrings.size() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        topicsMenu = opt_menu.addSubMenu("Topics");
        countriesMenu = opt_menu.addSubMenu("Countries");
        languagesMenu = opt_menu.addSubMenu("Languages");

        return true;
    }
}

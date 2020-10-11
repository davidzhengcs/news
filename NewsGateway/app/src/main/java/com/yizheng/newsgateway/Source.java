package com.yizheng.newsgateway;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class Source {

    private String id, name, topic, lang, country;

    public Source(String id, String name, String topic, String lang, String country) {
        this.id = id;
        this.name = name;
        this.topic = topic;
        this.lang = lang;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    SpannableString getColoredName(){
        SpannableString ss = new SpannableString(getName());
        ss.setSpan(new ForegroundColorSpan(AsyncSourceLoader.topicToColor.get(getTopic())), 0, ss.length(), 0);
        return ss;

    }

    public String getTopic() {
        return topic;
    }

    public String getLang() {
        return lang;
    }

    public String getCountry() {
        return country;
    }
}

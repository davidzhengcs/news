package com.yizheng.newsgateway;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class Story implements Serializable {
    private String author, title, description, url, urlToImage, publishedAt;
    //private Drawable drawable;


    Story(String author, String title, String description, String url, String urlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    String getAuthor() {
        return author;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    String getUrl() {
        return url;
    }

    String getPublishedAt() {
        return publishedAt;
    }

    String getUrlToImage() {
        return urlToImage;
    }
    //    Drawable getDrawable() {
//        return drawable;
//    }
}

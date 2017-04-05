package com.codepath.apps.tweeter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ian_sibner on 4/4/17.
 */
public class User {
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("screen_name")
    @Expose
    public String screenName;


    @SerializedName("profile_image_url")
    @Expose
    public String profileImageUrl;

    @SerializedName("id")
    @Expose
    public long id;

    public String getFullScreenName() {
        return "@" + screenName;
    }
}

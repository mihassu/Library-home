package ru.mihassu.libraryhw.lesson04;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrofitModel {

    @SerializedName("login")
    @Expose
    private String login;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;

    @SerializedName("name")
    @Expose
    private String name;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }
}

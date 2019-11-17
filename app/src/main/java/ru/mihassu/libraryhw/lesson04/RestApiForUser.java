package ru.mihassu.libraryhw.lesson04;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestApiForUser {

    @GET("users/{user}/repos")
    Call<List<RetrofitModel>> loadUser(@Path("user") String user);
}

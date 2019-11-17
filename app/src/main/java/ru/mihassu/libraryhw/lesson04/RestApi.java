package ru.mihassu.libraryhw.lesson04;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestApi {

    @GET("users")
    Call<List<RetrofitModel>> loadUsers();
}

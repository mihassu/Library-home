package ru.mihassu.libraryhw.lesson06.di;


import java.util.List;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mihassu.libraryhw.lesson04.RestApiForUser;
import ru.mihassu.libraryhw.lesson04.RetrofitModel;


@Module
public class AppComponentModule {

    private final String BASE_URL = "https://api.github.com/";
    private final String USER_NAME = "mihassu";

    @RetrofitScope
    @Provides
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides

    RestApiForUser provideApi(Retrofit retrofit) {
        return retrofit.create(RestApiForUser.class);
    }

    @Provides
    Call<List<RetrofitModel>> provideCall(Retrofit retrofit) {
        RestApiForUser restApiForUser = retrofit.create(RestApiForUser.class);
        return restApiForUser.loadUser(USER_NAME);
    }
}

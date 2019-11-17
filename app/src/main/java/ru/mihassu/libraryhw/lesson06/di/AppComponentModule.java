package ru.mihassu.libraryhw.lesson06.di;




import android.content.Context;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mihassu.libraryhw.lesson04.RestApiForUser;


@Module
public class AppComponentModule {

    private final String BASE_URL = "https://api.github.com/";
    private final String USER_NAME = "mihassu";

    private Context context;

    public AppComponentModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext(){
        return context;
    }

    @ApplicationScope
    @Provides
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @ApplicationScope
    @Provides
    RestApiForUser provideApi(Retrofit retrofit) {
        return retrofit.create(RestApiForUser.class);
    }
}

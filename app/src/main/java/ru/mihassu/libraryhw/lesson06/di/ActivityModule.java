package ru.mihassu.libraryhw.lesson06.di;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ActivityModule {

    private Activity context;

    public ActivityModule(Activity context) {
        this.context = context;
    }

    @Provides
    Activity provideActivity() {
        return context;
    }

    @Provides
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

}

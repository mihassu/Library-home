package ru.mihassu.libraryhw.lesson06.di;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkInfoModule {

    @ActivityScope
    @Provides
    ConnectivityManager provideConnectivityManager(Activity context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @ActivityScope
    @Provides
    NetworkInfo provideNetworkInfo(ConnectivityManager cm) {
        return cm.getActiveNetworkInfo();
    }
}

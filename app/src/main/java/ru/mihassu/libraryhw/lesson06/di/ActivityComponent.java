package ru.mihassu.libraryhw.lesson06.di;


import android.app.Activity;
import android.net.NetworkInfo;

import dagger.Component;
import okhttp3.OkHttpClient;

@ActivityScope
@Component (
            modules = {ActivityModule.class, NetworkInfoModule.class})

public interface ActivityComponent {

    Activity getActivityContext();
    NetworkInfo getNetworkInfo();
    OkHttpClient getOkHttpClient();
}

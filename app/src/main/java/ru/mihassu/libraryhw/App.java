package ru.mihassu.libraryhw;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();
    }

    void initRealm() {
        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("myrealmbase.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}

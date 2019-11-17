package ru.mihassu.libraryhw;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.mihassu.libraryhw.lesson06.di.AppComponent;
import ru.mihassu.libraryhw.lesson06.di.AppComponentModule;
import ru.mihassu.libraryhw.lesson06.di.DaggerAppComponent;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        initRealm();

        component = DaggerAppComponent.builder()
                .appComponentModule(new AppComponentModule(getApplicationContext()))
                .build();
    }

    void initRealm() {
        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("myrealmbase.realm")
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static AppComponent getComponent() {
        return component;
    }
}

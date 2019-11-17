package ru.mihassu.libraryhw.lesson06.di;


import android.content.Context;

import dagger.Component;
import ru.mihassu.libraryhw.lesson04.RestApiForUser;
import ru.mihassu.libraryhw.lesson05.databases.RealmDbImpl;

@ApplicationScope
@Component (modules = {AppComponentModule.class, DbModule.class})
public interface AppComponent {

    Context getContext();
    RestApiForUser getRestApi();
    RealmDbImpl getRealmDbImpl();
//    void inject(MyActivity activity);
}

package ru.mihassu.libraryhw.lesson06.di;

import dagger.Module;
import dagger.Provides;
import ru.mihassu.libraryhw.lesson05.databases.DbProvider;
import ru.mihassu.libraryhw.lesson05.databases.RealmDbImpl;

@Module
public class DbModule {

    @ApplicationScope
    @Provides
    DbProvider provideRealmDbImpl() {
        return new RealmDbImpl();
    }
}

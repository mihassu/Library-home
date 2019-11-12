package ru.mihassu.libraryhw.lesson06.di;


import dagger.Component;
import ru.mihassu.libraryhw.lesson04.RetrofitActivity;

@RetrofitScope
@Component (modules = AppComponentModule.class)
public interface AppComponent {

    void inject(RetrofitActivity activity);
}

package ru.mihassu.libraryhw.lesson04;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mihassu.libraryhw.App;
import ru.mihassu.libraryhw.lesson05.databases.DbProvider;
import ru.mihassu.libraryhw.lesson05.entity.NoteRealmData;
import ru.mihassu.libraryhw.lesson05.model.MyNote;

public class MyActivityViewModel extends ViewModel {

    public MutableLiveData<String> singleData = new MutableLiveData<>();
    public MutableLiveData<String> operationData = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private List<RetrofitModel> modelList = new ArrayList<>();

    private final String COUNT_KEY = "count";
    private final String MSEK_KEY = "msek";

    private DbProvider dbRealm = App.getComponent().getRealmDbImpl();
    private OkHttpClient okHttpClient = MyActivity.getActivityComponent().getOkHttpClient();

    public void downloadOneUrl(String userName) throws IOException {

        Call<List<RetrofitModel>> call = App.getComponent().getRestApi().loadUser(userName);

        call.enqueue(new Callback<List<RetrofitModel>>() {
            @Override
            public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {

                if (response.isSuccessful()) {
                    if (response != null) {

                        RetrofitModel retrofitModel = null;

//                        textView.setText("\nSize: " + response.body().size());
                        singleData.setValue("\nSize: " + response.body().size());

                        for (int i = 0; i < response.body().size(); i++) {

                            retrofitModel = response.body().get(i);

                            //сохраняем в ArrayList, из которого затем будем загружать в БД
                            modelList.add(retrofitModel);

                            singleData.setValue("\nname: " + retrofitModel.getName() +
                                    "\nfull name: " + retrofitModel.getFullName() +
                                    "\nprivate: " + retrofitModel.getPrivateType() +
                                    "\n------------------------");

                        }
                    } else singleData.setValue("response = null");
                } else {
                    singleData.setValue("Ошибка: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                singleData.setValue("Ошибка: " + t.getMessage());
            }
        });
    }

    public void insertToDbRealm() {

        Single<Object> singleInsert = Single.create((emitter) -> {
            Date first = new Date();

            for (RetrofitModel curModel : modelList) {
                dbRealm.insert(new NoteRealmData(curModel.getName(),
                        curModel.getFullName(),
                        curModel.getPrivateType()));
            }

            Date second = new Date();
            Bundle bundle = new Bundle();
            bundle.putInt(COUNT_KEY, modelList.size());
            bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

            emitter.onSuccess(bundle);

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleInsert.subscribeWith(createObserver());
    }


    public void readFromDbRealm1() {

        Disposable read = Single.just((List<MyNote>) dbRealm.select())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    for (MyNote curNote : result) {
                        singleData.setValue("\nИмя: " + curNote.getName() +
                                "\nПолное имя: " + curNote.getFullName() +
                                "\nПриват: " + curNote.getPrivateType() +
                                "\n-----------------------------");
                    }
                }, throwable -> singleData.setValue("Ошибка: " + throwable));

        compositeDisposable.add(read);
    }

    public void deleteAllFromDbRealm() {

        Disposable deleteAll = Completable.fromAction(() -> dbRealm.deleteAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> singleData.setValue("Удалено"),
                        throwable -> singleData.setValue("Ошибка при удалении: " + throwable));

        compositeDisposable.add(deleteAll);
    }

    //Observer который выводит время операции
    private DisposableSingleObserver<Object> createObserver() {
        return new DisposableSingleObserver<Object>() {

            @Override
            protected void onStart() {
                super.onStart();
//                progressBar.setVisibility(View.VISIBLE);
//                editText.setText("Observer: \n");
                operationData.setValue("Observer: \n");
            }

            @Override
            public void onSuccess(Object bundle) {

                Bundle bundle1 = (Bundle) bundle;
//                progressBar.setVisibility(View.GONE);
                operationData.setValue("Количество: " + bundle1.getInt(COUNT_KEY) +
                        "\nВремя: " + bundle1.getLong(MSEK_KEY) + "мс");
            }

            @Override
            public void onError(Throwable e) {
//                progressBar.setVisibility(View.GONE);
                operationData.setValue("Ошибка БД " + e.getMessage());
            }
        };
    }


    private Request createRequest(String userName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/users/mihassu/repos").
                newBuilder();
        urlBuilder.addQueryParameter("login", userName);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        return request;
    }

    public void downloadUserJson(String userName) throws IOException {
//        progressBar.setVisibility(View.VISIBLE);
        Request request = createRequest(userName);

        //т.к. без AsyncTask, то используем enqueue()
        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Ошибка. Код: " + response);
                } else {

                    // Тело запроса
                    final String responseData = response.body().string();
                    MyActivity.getActivityComponent().getActivityContext().runOnUiThread(() ->
                            singleData.setValue(responseData));

//                    MyActivity.this.runOnUiThread(() -> {
//                        textView.setText(responseData);
//                        progressBar.setVisibility(View.GONE);
//                    });

                }
            }
        });

    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }
}

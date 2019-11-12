package ru.mihassu.libraryhw.lesson04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mihassu.libraryhw.App;
import ru.mihassu.libraryhw.R;
import ru.mihassu.libraryhw.lesson05.databases.DbProvider;
import ru.mihassu.libraryhw.lesson05.databases.RealmDbImpl;
import ru.mihassu.libraryhw.lesson05.entity.NoteRealmData;
import ru.mihassu.libraryhw.lesson05.entity.SugarModel;
import ru.mihassu.libraryhw.lesson05.model.MyNote;
import ru.mihassu.libraryhw.lesson06.di.AppComponent;


public class RetrofitActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText editText;
    private Button buttonLoad;
    private Button buttonLoadJson;
    private Button btnSaveAllSugar;
    private Button btnSelectAllSugar;
    private Button btnDeleteAllSugar;
    private TextView textView;
//    private Retrofit retrofit; //надо делать Singletone

//    private RestApiForUser restApiForUser;
    private OkHttpClient okHttpClient;
    private List<RetrofitModel> modelList = new ArrayList<>();
    private DbProvider<NoteRealmData, List<MyNote>> dbRealm;

    private final String BASE_URL = "https://api.github.com/";
    private final String USER_NAME = "mihassu";
    private final String COUNT_KEY = "count";
    private final String MSEK_KEY = "msek";

    @Inject
    Call<List<RetrofitModel>> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson04_main);

        initViews();

//        initRetrofit();
        okHttpClient = new OkHttpClient();

        SugarContext.init(getApplicationContext());

        dbRealm = new RealmDbImpl();

        //Dagger
        AppComponent component = App.getComponent();
        component.inject(this);
    }

    private void insertToDbRealm() {


        Single<Object> singleInsert = Single.create((emitter) -> {
            Date first = new Date();

            for (RetrofitModel curModel : modelList) {
                dbRealm.insert(new NoteRealmData(curModel.getName(), curModel.getFullName(), curModel.getPrivateType()));
            }

            Date second = new Date();
            Bundle bundle = new Bundle();
            bundle.putInt(COUNT_KEY, modelList.size());
            bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

            emitter.onSuccess(bundle);

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleInsert.subscribeWith(createObserver());

    }

    private void readFromDbRealm() {

        Single<Object> singleRead = Single.create((emitter) -> {

            Date first = new Date();

            List<MyNote> myNoteList = dbRealm.select();

            textView.setText("");
            for (MyNote curNote : myNoteList) {
                textView.append("\nИмя: " + curNote.getName() +
                        "\nПолное имя: " + curNote.getFullName() +
                        "\nПриват: " + curNote.getPrivateType() +
                        "\n-----------------------------");
            }
            //Отправить время операции
            Date second = new Date();
            Bundle bundle = new Bundle();
            bundle.putInt(COUNT_KEY, myNoteList.size());
            bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

            emitter.onSuccess(bundle);

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleRead.subscribeWith(createObserver());
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar_main);
        editText = findViewById(R.id.edit_text_main);
        buttonLoad = findViewById(R.id.button_load);
        buttonLoadJson = findViewById(R.id.button_load_json);
        textView = findViewById(R.id.text_view_main);
        btnSaveAllSugar = findViewById(R.id.button_btnSaveAllSugar);
        btnSelectAllSugar = findViewById(R.id.button_btnSelectAllSugar);
        btnDeleteAllSugar = findViewById(R.id.button_btnDeleteAllSugar);

        buttonLoad.setOnClickListener((v) -> load());
        buttonLoadJson.setOnClickListener((v) -> loadJson());

//        btnSaveAllSugar.setOnClickListener((v) -> saveAllSugar());
        btnSaveAllSugar.setOnClickListener((v) -> insertToDbRealm());
//        btnSelectAllSugar.setOnClickListener((v -> selectAllSugar()));
        btnSelectAllSugar.setOnClickListener((v -> readFromDbRealm()));

//        btnDeleteAllSugar.setOnClickListener((v -> deleteAllSugar()));
    }

//    private void initRetrofit() {
//        retrofit = null;
//        try {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
////            restApi = retrofit.create(RestApi.class);
//        } catch (Exception e) {
//            textView.setText("Не создался retrofit" + e.getMessage());
//            return;
//        }
//    }

    private void load() {
        textView.setText("");
//        retrofit = null;
//        try {
//            restApiForUser = retrofit.create(RestApiForUser.class);
//        } catch (Exception e) {
//            textView.setText("Не создался retrofit" + e.getMessage());
//            return;
//        }

        //Вызов на сервер
//        Call<List<RetrofitModel>> call = restApi.loadUsers();
//        Call<List<RetrofitModel>> call = restApiForUser.loadUser(USER_NAME);

        if (internetConnected()) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadOneUrl(call);
            } catch (IOException e) {
                e.printStackTrace();
                textView.setText(e.getMessage());
            }
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Call<List<RetrofitModel>> call) throws IOException {

        call.enqueue(new Callback<List<RetrofitModel>>() {
            @Override
            public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {

                if (response.isSuccessful()) {
                    if (response != null) {

                        RetrofitModel retrofitModel = null;

                        textView.setText("\nSize: " + response.body().size());

                        for (int i = 0; i < response.body().size(); i++) {

                            retrofitModel = response.body().get(i);

                            //сохраняем в ArrayList, из которого затем будем загружать в БД
                            modelList.add(retrofitModel);

//                            textView.append("\nLogin: " + retrofitModel.getLogin() +
//                                    "\nid: " + retrofitModel.getId() +
//                                    "\nURL: " + retrofitModel.getAvatarUrl() +
//                                    "\n------------------------");
                            textView.append("\nname: " + retrofitModel.getName() +
                                    "\nfull name: " + retrofitModel.getFullName() +
                                    "\nprivate: " + retrofitModel.getPrivateType() +
                                    "\n------------------------");

                        }
                    }
                } else {
                    textView.setText("Ошибка: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                textView.setText("Ошибка: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    private void loadJson() {
        if (internetConnected()) {

            try {
                downloadUserJson(createRequest());
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }
    }

    private Request createRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/users/mihassu/repos").
                newBuilder();
        urlBuilder.addQueryParameter("login", USER_NAME);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        return request;
    }

    private void downloadUserJson(Request request) throws IOException {
        progressBar.setVisibility(View.VISIBLE);
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
                    RetrofitActivity.this.runOnUiThread(() -> {
                        textView.setText(responseData);
                        progressBar.setVisibility(View.GONE);
                    });

                }
            }
        });

    }

    //Сохранить из ArrayList в базу
    private void saveAllSugar() {
        Single<Object> singleSaveAll = Single.create((emitter) -> {

            try {
                String curName = "";
                String curFullName = "";
                String curPrivateType = "";
                Date first = new Date();

                //Сохранить из ArrayList в БД
                for (RetrofitModel currentModel : modelList) {
                    curName = currentModel.getName();
                    curFullName = currentModel.getFullName();
                    curPrivateType = currentModel.getPrivateType();
                    SugarModel sugarModel = new SugarModel(curName, curFullName, curPrivateType);
                    sugarModel.save();
                }

                //Отправить время операции
                Date second = new Date();
                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                Bundle bundle = new Bundle();
                bundle.putInt(COUNT_KEY, tempList.size());
                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

                emitter.onSuccess(bundle);
            } catch (Exception e) {
                emitter.onError(e);
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleSaveAll.subscribeWith(createObserver());
    }

    //Прочитать все из БД
    private void selectAllSugar() {
        Single<Object> singleSelectAll = Single.create((emitter) -> {

            textView.setText("");
            try {
                Date first = new Date();
                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);

                //Отправить время операции
                Date second = new Date();
                Bundle bundle = new Bundle();
                bundle.putInt(COUNT_KEY, tempList.size());
                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

                SugarModel curModel;
                for (int i = 0; i < tempList.size(); i++) {
                    curModel = tempList.get(i);
                    textView.append("Имя: " + curModel.getName() +
                            "\nПолное имя: " + curModel.getFullName() +
                            "\nПриватность: " + curModel.getPrivateType());
                }

                emitter.onSuccess(bundle);
            } catch (Exception e) {
                emitter.onError(e);
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleSelectAll.subscribeWith(createObserver());
    }

    //Удалить все из БД
    private void deleteAllSugar() {
        Single<Object> singleDeleteAll = Single.create((emitter) -> {

            textView.setText("");
            try {
                Date first = new Date();
                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
                SugarModel.deleteAll(SugarModel.class);

                //Отправить время операции
                Date second = new Date();
                Bundle bundle = new Bundle();
                bundle.putInt(COUNT_KEY, tempList.size());
                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());

                emitter.onSuccess(bundle);
            } catch (Exception e) {
                emitter.onError(e);
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        singleDeleteAll.subscribeWith(createObserver());
    }

    //Observer который выводит время операции
    private DisposableSingleObserver<Object> createObserver() {
        return new DisposableSingleObserver<Object>() {

            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                editText.setText("Observer: \n");
            }

            @Override
            public void onSuccess(Object bundle) {

                Bundle bundle1 = (Bundle) bundle;
                progressBar.setVisibility(View.GONE);
                editText.append("Количество: " + bundle1.getInt(COUNT_KEY) +
                        "\nВремя: " + bundle1.getLong(MSEK_KEY) + "мс");
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                editText.setText("Ошибка БД " + e.getMessage());
            }
        };
    }


    private boolean internetConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}

package ru.mihassu.libraryhw.lesson04;

import androidx.appcompat.app.AppCompatActivity;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;


import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ru.mihassu.libraryhw.R;
import ru.mihassu.libraryhw.lesson06.di.ActivityComponent;
import ru.mihassu.libraryhw.lesson06.di.ActivityModule;
import ru.mihassu.libraryhw.lesson06.di.DaggerActivityComponent;


public class MyActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText editText;
    private Button buttonLoad;
    private Button buttonLoadJson;
    private Button btnSaveToDb;
    private Button btnSelectFromDb;
    private Button btnDeleteFromDb;
    private TextView textView;
    private TextView textViewOperationInfo;

//    private List<RetrofitModel> modelList = new ArrayList<>();

    private final String BASE_URL = "https://api.github.com/";
    private final String USER_NAME = "mihassu";
    private final String COUNT_KEY = "count";
    private final String MSEK_KEY = "msek";

    private MyActivityViewModel retrofitActivityViewModel;
    private static ActivityComponent activityComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson04_main);

        initActivityComponent();
        initViews();
        initViewModel();

        retrofitActivityViewModel.singleData.observe(this, data -> {
            textView.append(data);
        });
        retrofitActivityViewModel.operationData.observe(this, data -> {
            textViewOperationInfo.append(data);
        });

//        SugarContext.init(getApplicationContext()); //terminate

//        //Dagger
//        AppComponent component = App.getComponent();
//        component.inject(this);
    }

    private void initActivityComponent() {
        activityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .build();
    }

    public static ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar_main);
        editText = findViewById(R.id.edit_text_main);
        buttonLoad = findViewById(R.id.button_load);
        buttonLoadJson = findViewById(R.id.button_load_json);
        textView = findViewById(R.id.text_view_main);
        textViewOperationInfo = findViewById(R.id.text_view_operation_info);

        btnSaveToDb = findViewById(R.id.button_btnSaveToDb);
        btnSelectFromDb = findViewById(R.id.button_btnSelectFromDb);
        btnDeleteFromDb = findViewById(R.id.button_btnDeleteFromDb);

        buttonLoad.setOnClickListener((v) -> load1());
        buttonLoadJson.setOnClickListener((v) -> loadJson(USER_NAME));

        btnSaveToDb.setOnClickListener(v -> insertToDb());
        btnSelectFromDb.setOnClickListener(v -> selectFromDb());
        btnDeleteFromDb.setOnClickListener(v -> deleteAllFromDb());
    }

    private void initViewModel() {
        retrofitActivityViewModel = ViewModelProviders.of(this)
                .get(MyActivityViewModel.class);
    }

    private void insertToDb() {

        textView.setText("");
        textViewOperationInfo.setText("");
        progressBar.setVisibility(View.VISIBLE);
        retrofitActivityViewModel.insertToDbRealm();
        progressBar.setVisibility(View.GONE);
    }

    private void selectFromDb() {

        textView.setText("");
        textViewOperationInfo.setText("");
        progressBar.setVisibility(View.VISIBLE);
        retrofitActivityViewModel.readFromDbRealm1();
        progressBar.setVisibility(View.GONE);
    }

    private void deleteAllFromDb() {
        textView.setText("");
        textViewOperationInfo.setText("");
        progressBar.setVisibility(View.VISIBLE);
        retrofitActivityViewModel.deleteAllFromDbRealm();
        progressBar.setVisibility(View.GONE);
    }

    private void load1() {
        textView.setText("");
        textViewOperationInfo.setText("");

        if (internetConnected()) {
            progressBar.setVisibility(View.VISIBLE);
            try {
                retrofitActivityViewModel.downloadOneUrl(USER_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                textView.setText(e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void loadJson(String userName) {

        if (internetConnected()) {
            textView.setText("");
            progressBar.setVisibility(View.VISIBLE);
            try {
                retrofitActivityViewModel.downloadUserJson(USER_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }

            progressBar.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }
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
        NetworkInfo networkInfo = activityComponent.getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SugarContext.terminate();
    }


//    //Сохранить из ArrayList в базу
//    private void saveAllSugar() {
//        Single<Object> singleSaveAll = Single.create((emitter) -> {
//
//            try {
//                String curName = "";
//                String curFullName = "";
//                String curPrivateType = "";
//                Date first = new Date();
//
//                //Сохранить из ArrayList в БД
//                for (RetrofitModel currentModel : modelList) {
//                    curName = currentModel.getName();
//                    curFullName = currentModel.getFullName();
//                    curPrivateType = currentModel.getPrivateType();
//                    SugarModel sugarModel = new SugarModel(curName, curFullName, curPrivateType);
//                    sugarModel.save();
//                }
//
//                //Отправить время операции
//                Date second = new Date();
//                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt(COUNT_KEY, tempList.size());
//                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());
//
//                emitter.onSuccess(bundle);
//            } catch (Exception e) {
//                emitter.onError(e);
//            }
//
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//
//        singleSaveAll.subscribeWith(createObserver());
//    }
//
//    //Прочитать все из БД
//    private void selectAllSugar() {
//        Single<Object> singleSelectAll = Single.create((emitter) -> {
//
//            textView.setText("");
//            try {
//                Date first = new Date();
//                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
//
//                //Отправить время операции
//                Date second = new Date();
//                Bundle bundle = new Bundle();
//                bundle.putInt(COUNT_KEY, tempList.size());
//                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());
//
//                SugarModel curModel;
//                for (int i = 0; i < tempList.size(); i++) {
//                    curModel = tempList.get(i);
//                    textView.append("Имя: " + curModel.getName() +
//                            "\nПолное имя: " + curModel.getFullName() +
//                            "\nПриватность: " + curModel.getPrivateType());
//                }
//
//                emitter.onSuccess(bundle);
//            } catch (Exception e) {
//                emitter.onError(e);
//            }
//
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//
//        singleSelectAll.subscribeWith(createObserver());
//    }
//
//    //Удалить все из БД
//    private void deleteAllSugar() {
//        Single<Object> singleDeleteAll = Single.create((emitter) -> {
//
//            textView.setText("");
//            try {
//                Date first = new Date();
//                List<SugarModel> tempList = SugarModel.listAll(SugarModel.class);
//                SugarModel.deleteAll(SugarModel.class);
//
//                //Отправить время операции
//                Date second = new Date();
//                Bundle bundle = new Bundle();
//                bundle.putInt(COUNT_KEY, tempList.size());
//                bundle.putLong(MSEK_KEY, second.getTime() - first.getTime());
//
//                emitter.onSuccess(bundle);
//            } catch (Exception e) {
//                emitter.onError(e);
//            }
//
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//
//        singleDeleteAll.subscribeWith(createObserver());
//    }
}

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

import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mihassu.libraryhw.R;


public class RetrofitActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText editText;
    private Button buttonLoad;
    private Button buttonLoadJson;

    private TextView textView;

    private Retrofit retrofit; //надо делать Singletone
    private RestApi restApi;
    private RestApiForUser restApiForUser;

    private OkHttpClient okHttpClient;


    private final String BASE_URL = "https://api.github.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson04_main);

        progressBar = findViewById(R.id.progress_bar_main);
        editText = findViewById(R.id.edit_text_main);
        buttonLoad = findViewById(R.id.button_load);
        buttonLoadJson = findViewById(R.id.button_load_json);

        textView = findViewById(R.id.text_view_main);

        initRetrofit();
        okHttpClient = new OkHttpClient();

        buttonLoad.setOnClickListener((v) -> load());
        buttonLoadJson.setOnClickListener((v) -> loadJson());
    }

    private void initRetrofit() {
        retrofit = null;
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
//            restApi = retrofit.create(RestApi.class);
        } catch (Exception e) {
            textView.setText("Не создался retrofit" + e.getMessage());
            return;
        }
    }

    private void load() {
        textView.setText("");
//        retrofit = null;
        try {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            restApi = retrofit.create(RestApi.class);
            restApiForUser = retrofit.create(RestApiForUser.class);
        } catch (Exception e) {
            textView.setText("Не создался retrofit" + e.getMessage());
            return;
        }

        //Вызов на сервер
//        Call<List<RetrofitModel>> call = restApi.loadUsers();
        Call<List<RetrofitModel>> call = restApiForUser.loadUser("mojombo");


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

                        for (int i = 0; i < response.body().size(); i++) {

                            retrofitModel = response.body().get(i);
//                            textView.append("\nLogin: " + retrofitModel.getLogin() +
//                                    "\nid: " + retrofitModel.getId() +
//                                    "\nURL: " + retrofitModel.getAvatarUrl() +
//                                    "\n------------------------");
                            textView.append("\nname: " + retrofitModel.getName() +
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
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/users/mojombo/repos").newBuilder();
        urlBuilder.addQueryParameter("login", "mojombo");

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

    private boolean internetConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}

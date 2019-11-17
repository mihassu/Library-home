package ru.mihassu.libraryhw.lesson04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mihassu.libraryhw.R;


public class Lesson04MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText editText;
    private Button buttonLoad;
    private TextView textView;

    private OkHttpClient okHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson04_main);

        progressBar = findViewById(R.id.progress_bar_main);
        editText = findViewById(R.id.edit_text_main);
        buttonLoad = findViewById(R.id.button_load);
        textView = findViewById(R.id.text_view_main);

        okHttpClient = new OkHttpClient();

        if (internetConnected()) {

            //Запустить в новом потоке через AsyncTask с помощью HttpURLConnection
//            new DownloadPageTask().execute("https://api.github.com/");

            //с помощью OkHttpClient через AsyncTask
//            new DownloadPageTask().execute(createRaquest());

            //с помощью OkHttpClient без  AsyncTask
            try {
                downloadOneUrl(createRaquest());
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show();
        }


    }


    private Request createRaquest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.github.com/users").newBuilder();
        urlBuilder.addQueryParameter("login", "mojombo");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();

        return request;
    }

    //с помощью OkHttpClient без AsyncTask
    private void downloadOneUrl(Request request) throws IOException {

        progressBar.setVisibility(View.VISIBLE);

        //т.к. без AsyncTask, то используем enqueue()
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Ошибка. Код: " + response);
                } else {
                    //Вывести заголовки
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " +
                                responseHeaders.value(i));
                    }

                    // Тело запроса
                    final String responseData = response.body().string();
                    Lesson04MainActivity.this.runOnUiThread(() -> {
                            textView.setText(responseData);
                            progressBar.setVisibility(View.GONE);
                    });

                }
            }
        });
    }


    //с помощью OkHttpClient через AsyncTask
//    private class DownloadPageTask extends AsyncTask<Request, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            textView.setText("");
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String doInBackground(Request... requests) {
//            try {
//                return downloadOneUrl(requests[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Ошибка";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            textView.setText(s);
//            progressBar.setVisibility(View.GONE);
//            super.onPostExecute(s);
//        }
//    }

    //с помощью OkHttpClient через AsyncTask
//    private String downloadOneUrl(Request request) throws IOException {
//        String data = "";
//            try {
//                //т.к. через AsyncTask, то используем execute()
//                Response response = okHttpClient.newCall(request).execute();
//                if (!response.isSuccessful()) {
//                    throw new IOException("Ошибка. Код: " + response);
//                } else {
//
//                    //Вывести заголовки
//                    Headers responseHeaders = response.headers();
//                    for (int i = 0; i < responseHeaders.size(); i++) {
//                        System.out.println(responseHeaders.name(i) + ": " +
//                                responseHeaders.value(i));
//                    }
//
//                    // Тело запроса
//                    data = response.body().string();
//                }
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        return data;
//    }

    //с помощью HttpURLConnection
//    private class DownloadPageTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            textView.setText("");
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                return downloadOneUrl(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Ошибка";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            textView.setText(s);
//            progressBar.setVisibility(View.GONE);
//            super.onPostExecute(s);
//        }
//    }

    //с помощью HttpURLConnection
//    private String downloadOneUrl(String address) throws IOException {
//        InputStream inputStream = null;
//        String data = "";
//        try {
//            URL url = new URL(address);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setReadTimeout(100000);
//            connection.setConnectTimeout(100000);
////            connection.setRequestMethod("GET"); //GET - по умолчанию
//            connection.setInstanceFollowRedirects(true);
//            connection.setUseCaches(false);
//            connection.setDoInput(true);
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                System.out.println("Метод запроса: " + connection.getRequestMethod());
//                // Вывести код ответа
//                System.out.println("Ответное сообщение: " + connection.getResponseMessage());
//
//                // Получить список полей и множество ключей из заголовка
//                Map<String, List<String>> myMap = connection.getHeaderFields();
//                Set<String> myField = myMap.keySet();
//
//                // Вывести все ключи и значения из заголовка
//                for (String k: myField) {
//                    System.out.println("Ключ: " + k + " значение: " + myMap.get(k));
//                }
//
//                inputStream = connection.getInputStream();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                int read = 0;
//                while ((read = inputStream.read()) != -1) {
//                    bos.write(read);
//                }
//
//                byte[] result = bos.toByteArray();
//                bos.close();
//
//                data = new String(result);
//            } else {
//                data = connection.getResponseMessage() + ". Код ошибки: " + responseCode;
//            }
//
//            connection.disconnect();
//
//        } catch (MalformedURLException e ) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        }
//
//        return data;
//    }


    private boolean internetConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}

package ru.mihassu.libraryhw.lesson03;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;
import ru.mihassu.libraryhw.R;

public class Lesson03MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView textView;
    private Button buttonWrite;
    private Button buttonRead;
//    private Button buttonDispose;

    private Disposable d;
    private DisposableObserver d1;
    private MyFileManager fileManager;

    private String FILE = "myfile.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson03_activity_main);

        editText = findViewById(R.id.edit_text);
        textView = findViewById(R.id.file_text);
        buttonWrite = findViewById(R.id.button_write);
        buttonRead = findViewById(R.id.button_read);
//        buttonDispose = findViewById(R.id.button_dispose);

        fileManager = new MyFileManager(this);

        //слушатель на запись
        Action action = () -> Toast.makeText(this, "Записано", Toast.LENGTH_SHORT).show();

        buttonWrite.setOnClickListener((v) ->
            d = fileManager.writeFile(editText.getText().toString()).subscribe(action)
        );

        Activity that = this;
        buttonRead.setOnClickListener((v) ->
            d1 = fileManager.readFile().subscribeWith(new DisposableObserver() {
                @Override
                public void onNext(Object o) {
                    textView.setText(o.toString());
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    Toast.makeText(that, "Чтение не удалось", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {

                }
            })
        );
    }

    @Override
    protected void onStop() {
        d.dispose();
        d1.dispose();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

//        switch (id) {
//            case R.id.button_write:
//                readFile();
//                break;
//        }
    }

    public void writeFile() {
//        String text = editText.getText().toString();
//        OutputStream fos = null;
//        try {
////            File myFile = new File("/storage/emulated/0/Download/m.txt");
//            File myFile = new File(getFilesDir(), "init.rc");
//
//            fos = new BufferedOutputStream(new FileOutputStream(myFile));
//            fos.write(text.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        //запись в файл во внутренней директории
        FileOutputStream fos = null;
        String text = editText.getText().toString();
        try {
            fos = openFileOutput("myfile.txt", MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void readFile() {
//        byte[] buffer = null;
//        InputStream is;
//        try {
//            is = getAssets().open(FILE);
//            int size = is.available();
//            buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String fileData = new String(buffer);
//        textView.setText(fileData);



//        FileInputStream fin = null;
//        try {
//            fin = openFileInput("myfile.txt");
//            byte[] bytes = new byte[fin.available()];
//            fin.read(bytes);
//            String textIN = new String(bytes);
//            textView.setText(textIN);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fin != null) {
//                    fin.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }


//        InputStream fin = null;
//        try {
////            File myFile = new File("/storage/emulated/0/Download/m.txt");
//        //getFilesDir() - возвращает объект File, представляющий внутренний каталог приложения
//            File myFile = new File(getFilesDir(), "init");
//
//            fin = new BufferedInputStream(new FileInputStream(myFile));
//            byte[] bytes = new byte[fin.available()];
//            fin.read(bytes);
//            String textIN = new String(bytes);
//            textView.setText(textIN);
//            textView.setText(getFilesDir().toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fin != null) {
//                    fin.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }


}

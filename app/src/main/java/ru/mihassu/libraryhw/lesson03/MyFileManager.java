package ru.mihassu.libraryhw.lesson03;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class MyFileManager {

    private Context context;

    public MyFileManager(Context context){
        this.context = context;
    }

    public Completable writeFile(String text) {
        return Completable.fromAction(() -> {

            //запись в файл во внутренней директории
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput("myfile.txt", MODE_PRIVATE);
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
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Object> readFile(){

        return Observable.create(emitter -> {

            FileInputStream fin = null;
            String textIN = null;
            try {
                fin = context.openFileInput("myfile.txt");
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                textIN = new String(bytes);

            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            } finally {
                try {
                    if (fin != null) {
                        fin.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }

            }

            emitter.onNext(textIN);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

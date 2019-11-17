package ru.mihassu.libraryhw.lesson05.databases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.mihassu.libraryhw.lesson05.entity.NoteRealmData;
import ru.mihassu.libraryhw.lesson05.model.MyNote;

public class RealmDbImpl implements DbProvider<NoteRealmData, List<MyNote>> {

    @Inject
    public RealmDbImpl() {
    }

    @Override
    public void insert(NoteRealmData data) {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.beginTransaction();
            realm.insertOrUpdate(data);
            realm.commitTransaction();
        }
    }

    @Override
    public void update(NoteRealmData data) {

    }

    @Override
    public void delete(NoteRealmData data) {

    }

    @Override
    public void deleteAll() {
        try (Realm realm = Realm.getDefaultInstance()){
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
        }
    }

    @Override
    public List<MyNote> select() {

        try (Realm realm = Realm.getDefaultInstance()){
            final RealmResults<NoteRealmData> results = realm.where(NoteRealmData.class).findAll();
            List<NoteRealmData> items = realm.copyFromRealm(results);

            List<MyNote> myNoteList = new ArrayList<>();

            for (NoteRealmData cur: items) {
                myNoteList.add(NoteRealmData.convertToEntity(cur));
            }

            return myNoteList;
        }
    }
}

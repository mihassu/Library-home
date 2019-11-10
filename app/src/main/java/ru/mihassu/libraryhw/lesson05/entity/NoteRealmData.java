package ru.mihassu.libraryhw.lesson05.entity;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import ru.mihassu.libraryhw.lesson05.model.MyNote;

@RealmClass
public class NoteRealmData implements RealmModel {

    private String fullName;
    private String privateType;

    @PrimaryKey
    private String name;


    public NoteRealmData() {}

    public NoteRealmData(String name, String fullName, String privateType) {
        this.name = name;
        this.fullName = fullName;
        this.privateType = privateType;

    }

    public String getFullName() {
        return fullName;
    }

    public String getPrivateType() {
        return privateType;
    }

    public String getName() {
        return name;
    }

    public static MyNote convertToEntity(NoteRealmData item) {
        return new MyNote(item.getName(), item.getFullName(), item.getPrivateType());
    }
}

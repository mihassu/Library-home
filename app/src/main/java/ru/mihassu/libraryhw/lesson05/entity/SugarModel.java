package ru.mihassu.libraryhw.lesson05.entity;

import com.orm.SugarRecord;

public class SugarModel extends SugarRecord {

    private String name;
    private String fullName;
    private String privateType;

    public SugarModel() {}

    public SugarModel(String name, String fullName, String privateType) {
        this.name = name;
        this.fullName = fullName;
        this.privateType = privateType;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPrivateType() {
        return privateType;
    }
}

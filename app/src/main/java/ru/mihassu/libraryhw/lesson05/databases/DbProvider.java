package ru.mihassu.libraryhw.lesson05.databases;

public interface DbProvider<T,R> {

    void insert(T data);
    void update(T data);
    void delete(T data);
    R select();
}

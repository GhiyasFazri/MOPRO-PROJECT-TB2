package com.example.uas_IyashFazriee.database;

public interface QueryResponse<T> {
    void onSuccess(T data);
    void onFailure(String message);
}
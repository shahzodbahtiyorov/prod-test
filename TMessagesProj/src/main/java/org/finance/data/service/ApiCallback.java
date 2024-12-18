package org.finance.data.service;

public interface ApiCallback<T> {
    void onSuccess(T response);
    void onFailure(String errorMessage);
}

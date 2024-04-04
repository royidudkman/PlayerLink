package com.example.playerlink.repositories;

import com.example.playerlink.Result;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}

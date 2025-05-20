package com.challenge.vote_challenge.interfaces;

public interface IDtoFrom<T> {
    void copyFrom(T model);
    T toModel();
}

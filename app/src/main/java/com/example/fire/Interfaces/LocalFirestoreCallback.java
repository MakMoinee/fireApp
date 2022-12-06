package com.example.fire.Interfaces;

import com.example.fire.Models.Dishes;

import java.util.List;

public interface LocalFirestoreCallback {
    default void onSuccess(List<Dishes> dishes) {
        /**
         * Default implementation
         */
    }


    void onError(Exception e);
}

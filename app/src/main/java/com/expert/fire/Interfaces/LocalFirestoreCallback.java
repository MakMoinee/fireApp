package com.expert.fire.Interfaces;

import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.PersonalInfo;

import java.util.List;

public interface LocalFirestoreCallback {
    default void onSuccess(List<Dishes> dishes) {
        /**
         * Default implementation
         */
    }

    default void onSuccess(){
        /**
         * default implementation
         */
    }

    default  void onSuccess(PersonalInfo info){
        /***
         * default implementation
         */
    }


    void onError(Exception e);
}

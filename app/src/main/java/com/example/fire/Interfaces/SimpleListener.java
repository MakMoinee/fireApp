package com.example.fire.Interfaces;

public interface SimpleListener {

    default void refreshList() {
        /**
         * Default implementation
         */
    }

    default void onSuccess(){
        /***
         * Default implementation
         */
    }

    default void onFail(){
        /***
         * Default implementation
         */
    }
}

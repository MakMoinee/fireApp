package com.expert.fire.Interfaces;

import com.android.volley.VolleyError;

public interface SimpleListener {

    default void refreshList() {
        /**
         * Default implementation
         */
    }

    default void onSuccess() {
        /***
         * Default implementation
         */
    }

    default void onSuccess(String response) {
        /***
         * Default implementation
         */
    }

    default void onFail() {
        /***
         * Default implementation
         */
    }

    default void onError(VolleyError error) {
        /***
         *  Default implementation
         */
    }

    default void onError(Exception error) {
        /***
         *  Default implementation
         */
    }
}

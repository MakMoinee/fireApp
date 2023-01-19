package com.expert.fire.Service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.expert.fire.Common.Constants;
import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.Models.Translation;
import com.google.gson.Gson;

public class LocalRequest {

    public static void getTranslation(Context ctx, Translation translation, SimpleListener listener) {
        String finalReq = new Gson().toJson(translation);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.translationURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return Constants.CONTENT_TYPE;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return finalReq.getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(ctx);
        queue.add(stringRequest);
    }
}

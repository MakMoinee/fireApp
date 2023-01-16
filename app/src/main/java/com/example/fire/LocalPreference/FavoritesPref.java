package com.example.fire.LocalPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fire.Models.Dishes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesPref {
    Context mContext;
    SharedPreferences pref;

    public FavoritesPref(Context c) {
        this.mContext = c;
        this.pref = this.mContext.getSharedPreferences("favorites", Context.MODE_PRIVATE);
    }

    public void storeDish(Dishes dish) {
        SharedPreferences.Editor editor = pref.edit();
        if (dish != null) {
            String dishRaw = new Gson().toJson(dish);
            editor.putString(dish.getDish() + "DishRaw", dishRaw);
            editor.commit();
            editor.apply();
        }

    }

    public void storeEmptyDish(Dishes dish) {
        SharedPreferences.Editor editor = pref.edit();
        if (dish != null) {
            editor.putString(dish.getDish() + "DishRaw", "");
            editor.commit();
            editor.apply();
        }
    }

    public List<Dishes> getDishes() {
        List<Dishes> dishList = new ArrayList<>();
        for (Map.Entry<String, ?> entry : pref.getAll().entrySet()) {
            try {
                if (entry.getValue() == "") {
                    continue;
                }
            } catch (Exception e) {

            }
            if (entry.getKey().contains("DishRaw")) {
                Dishes dishes = new Gson().fromJson(entry.getValue().toString(), new TypeToken<Dishes>() {
                }.getType());

                if (dishes != null) {
                    dishList.add(dishes);
                }
            }
        }

        return dishList;
    }


}

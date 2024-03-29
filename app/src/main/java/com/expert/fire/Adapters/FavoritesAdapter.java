package com.expert.fire.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.R;
import com.expert.fire.DishActivity;
import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.LocalPreference.FavoritesPref;
import com.expert.fire.Models.Dishes;
import com.google.gson.Gson;

import java.util.List;

public class FavoritesAdapter extends BaseAdapter {

    Context context;
    List<Dishes> dishesList;
    TextView txtDish;
    ImageButton btnFav;
    SimpleListener listener;
    Boolean isLangEng = false;

    public FavoritesAdapter(Context mContext, List<Dishes> d, SimpleListener l) {
        this.context = mContext;
        this.dishesList = d;
        this.listener = l;
        isLangEng = new LanguagePref(this.context).getIsEng();
    }

    @Override
    public int getCount() {
        return dishesList.size();
    }

    @Override
    public Object getItem(int position) {
        return dishesList.get(position).getDish();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(context).inflate(R.layout.list_item_fav_dish, parent, false);
        initViews(mView);
        Dishes dishes = dishesList.get(position);

        txtDish.setText(dishes.getDish());
        txtDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DishActivity.class);
                intent.putExtra("dishRaw", new Gson().toJson(dishes));
                intent.putExtra("title", dishes.getDish());
                intent.putExtra("videoURL", dishes.getVideoURL());
                intent.putExtra("instructions", String.join("\n", dishes.getInstructions()));
                intent.putExtra("ingredients", String.join("\n", dishes.getOrigIngredients()));
                if (dishes.getDescription().size() > 0) {
                    intent.putExtra("description", String.join("\n", dishes.getDescription()));
                } else {
                    intent.putExtra("description", "");
                }
                context.startActivity(intent);
            }
        });
        initListeners(dishes);
        return mView;
    }

    private void initListeners(Dishes dish) {
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                new FavoritesPref(context).storeEmptyDish(dish);
                                listener.refreshList();
                                dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                if (isLangEng) {
                    mBuilder.setMessage("Are you want to remove this dish to your favorites?")
                            .setNegativeButton("Yes", dListener)
                            .setPositiveButton("No", dListener)
                            .setCancelable(false)
                            .show();
                } else {
                    mBuilder.setMessage("Sigurado Ka bang Gusto Mong Tanggalin ang Ulam na Ito Sa Mga Paborito Mo?")
                            .setNegativeButton("Yes", dListener)
                            .setPositiveButton("No", dListener)
                            .setCancelable(false)
                            .show();
                }

            }
        });
    }

    private void initViews(View mView) {
        txtDish = mView.findViewById(R.id.txtDish);

        btnFav = mView.findViewById(R.id.btnFav);
    }
}

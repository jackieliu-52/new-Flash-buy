package com.jackie.flash_buy.ui.Card;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.ui.CircleImageTransformation;
import com.squareup.picasso.Picasso;


/**
 * Created by Jack on 2016/11/3.
 */
public class GoodsCardProvider extends CardProvider<GoodsCardProvider> {


    @Override
    public int getLayout() {
        return R.layout.material_goods_card_item;
    }


    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        ImageView circleImageView = (ImageView) findViewById(view,R.id.CircleImage,ImageView.class);
        Picasso.with(getContext())
                .load(getImageUrl())
                .transform(new CircleImageTransformation())
                .into(circleImageView);

    }
}

package com.jackie.flash_buy.ui.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.model.Aller_father;

/**
 * ViewHolder
 */
public class AllerHolder extends ParentViewHolder {
    private TextView mRecipeTextView;
    private ImageView mArrowExpandImageView;


    public AllerHolder(View itemView) {
        super(itemView);
        mRecipeTextView = (TextView)itemView.findViewById(R.id.aller_textview);

        mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrow_expand_imageview);

        mArrowExpandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
            }
        });
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }

    public void bind(Aller_father aller_father) {
        mRecipeTextView.setText(aller_father.getName());
    }
}

package com.jackie.flash_buy;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Jack on 2016/10/14.
 */
public class BaseFragment extends Fragment{
    protected final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

}

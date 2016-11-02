package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 设置账号信息，本来想用PreferFragment，但是不兼容v4下的Fragment
 * 只能手动绘制
 */
public class Fragment_account extends BaseFragment {
    final private String TAG = "Fragment_account";
    private Context mContext;
    /**
     * 单例对象实例
     */
    private static Fragment_account instance = null;

    EditText etName;
    EditText etMail;
    Spinner spSex;
    FloatingActionButton commitFab;
    CircleImageView avatar;

    private String name;
    private String sex;
    private String mail;

    /**
     * 对外接口
     *
     * @return Fragment2
     */
    public static Fragment_account GetInstance() {
        if (instance == null)
            instance = new Fragment_account();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        bindView(view);
        etName.setText(MainActivity.user.getName());
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = etName.getText().toString();
                MainActivity.user.setName(name);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mail = etMail.getText().toString();
                MainActivity.user.setMail(mail);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //设置默认下标,这里暂时不对性别进行保存
        spSex.setSelection(0,true);
        spSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] sexes = getResources().getStringArray(R.array.sex);
                sex = sexes[pos];
                MainActivity.user.setSex(sex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });


        return view;
    }

    /**
     * 用Butterknife出问题
     * @param view
     */
    private void bindView(View view){
        spSex = (Spinner)view.findViewById(R.id.sp_sex);
        etName=(EditText)view.findViewById(R.id.et_name);
        etMail=(EditText)view.findViewById(R.id.et_mail);
        commitFab = (FloatingActionButton)view.findViewById(R.id.settingFab);
        commitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入设置页面 ....
            }
        });
        avatar = (CircleImageView)view.findViewById(R.id.avatar);
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
        } else {
            //相当于Fragment的onPause，如果数据有改动，就传给服务器
            Log.i(TAG,"in");
        }
    }






    @Override
    public String toString() {
        return "fragment_account";
    }
}

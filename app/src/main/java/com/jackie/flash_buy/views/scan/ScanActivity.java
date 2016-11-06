package com.jackie.flash_buy.views.scan;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.ui.RevealBackgroundView;
import com.jackie.flash_buy.ui.ScanImageView;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.utils.zxing.ScanListener;
import com.jackie.flash_buy.utils.zxing.ScanManager;
import com.jackie.flash_buy.utils.zxing.decode.DecodeThread;
import com.jackie.flash_buy.utils.zxing.decode.Utils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.library.bean.InfoBean;
import immortalz.me.library.method.ColorShowMethod;

/**
 * Created by Jack on 2016/8/12.
 */
public class ScanActivity extends Activity implements ScanListener, View.OnClickListener, RevealBackgroundView.OnStateChangeListener{
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
    ScanManager scanManager;
    TextView iv_light;
    TextView qrcode_g_gallery;
    TextView qrcode_ic_back;
    final int PHOTOREQUESTCODE = 1111;

    private int scanMode;//扫描模型（条形，二维码，全部）
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    RevealBackgroundView vRevealBackground;

    @BindView(R.id.authorize_return)
    ImageView authorize_return;

    @BindView(R.id.common_title_TV_center)
    TextView title;
    @BindView(R.id.tv_scan_result)
    TextView tv_scan_result;

    @BindView(R.id.scan_hint)
    TextView scan_hint;
    @BindView(R.id.scan_image)
    ScanImageView scan_image;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ScanActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        scanMode = Constant.REQUEST_SCAN_MODE_ALL_MODE;
        initView();

        //动画
        setupRevealBackground(icicle);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    void initView() {
        switch (scanMode){
            case DecodeThread.ALL_MODE:
                title.setText(R.string.scan_allcode_title);
                scan_hint.setText(R.string.scan_allcode_hint);
                break;
        }
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcode_g_gallery = (TextView) findViewById(R.id.qrcode_g_gallery);
        qrcode_g_gallery.setOnClickListener(this);
        qrcode_ic_back = (TextView) findViewById(R.id.qrcode_ic_back);
        qrcode_ic_back.setOnClickListener(this);
        iv_light = (TextView) findViewById(R.id.iv_light);
        iv_light.setOnClickListener(this);

        authorize_return.setOnClickListener(this);
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview, scanContainer, scanCropView, scanLine, scanMode,this);

        vRevealBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
        scan_image.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }


    public void scanResult(Result rawResult, Bundle bundle) {

//        if (!scanManager.isScanning()) { //如果当前不是在扫描状态
//            scan_image.setVisibility(View.VISIBLE);
//            Bitmap barcode = null;
//            byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
//            if (compressedBitmap != null) {
//                barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
//                barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
//            }
//            scan_image.setImageBitmap(barcode);
//        }
//        scan_image.setVisibility(View.VISIBLE);
//        tv_scan_result.setVisibility(View.VISIBLE);
//        //rawResult.getText()就是扫描结果，条形码就是一串数字
//        tv_scan_result.setText("结果："+rawResult.getText());
        if(rawResult.getText().contains("cartNumber")){
            Intent intent = new Intent(ScanActivity.this, ConnectActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("1",rawResult.getText());
            intent.putExtras(bundle1);
            startActivity(intent);
        }
        else {
            EventBus.getDefault().post(new InternetEvent(rawResult.getText(), Constant.REQUEST_INTERNET_BAR));
        }
        finish(); //关闭
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if(e.getMessage()!=null&&e.getMessage().startsWith("相机")){
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 打开系统本有的相册，进行扫描识别
     * @param requestCode
     */
    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }


    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            scanContainer.setVisibility(View.VISIBLE);

        } else {
            scanContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("ScanActivity","onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_g_gallery:
                //打开相册
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.iv_light:
                scanManager.switchLight();
                break;
            case R.id.qrcode_ic_back:
                finish();
                break;
            case R.id.authorize_return:
                finish();
                break;
            default:
                break;
        }
    }
}

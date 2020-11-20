package com.arcsoft.arcfacedemo.activity.thermometry;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.util.utils.SPUtils;

public class SettingView extends RelativeLayout {
    private Context mContext;
    private EditText mPaletteEditText;
    private EditText mScaleEditText;
    private EditText mRotateEditText;
    private EditText mImageAlgoEditText;
    private EditText mAutoShutterSwitchEditText;
    private EditText mPeriodEditText;
    private EditText mDelayEditText;
    public SettingView(Context context) {
        super(context);
        mContext = context;
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initDefaultValue();
    }

    private void initView() {
        mPaletteEditText = findViewById(R.id.setting_palette_et);
        mScaleEditText = findViewById(R.id.setting_scale_et);
        mRotateEditText = findViewById(R.id.setting_rotate_et);
        mImageAlgoEditText = findViewById(R.id.setting_image_algo_et);
        mAutoShutterSwitchEditText = findViewById(R.id.setting_auto_shutter_switch_et);
        mPeriodEditText = findViewById(R.id.setting_period_et);
        mDelayEditText = findViewById(R.id.setting_delay_et);
    }

    private void initDefaultValue() {
        mPaletteEditText.setText(SPUtils.getPalette(mContext));
        mScaleEditText.setText(SPUtils.getScale(mContext));
        mRotateEditText.setText(SPUtils.getRotate(mContext));
        mImageAlgoEditText.setText(SPUtils.getImageAlgo(mContext));
        mAutoShutterSwitchEditText.setText(SPUtils.getSwitch(mContext));
        mPeriodEditText.setText(SPUtils.getPeriod(mContext));
        mDelayEditText.setText(SPUtils.getDelay(mContext));
    }

    public void save() {
        SPUtils.setPalette(mContext, mPaletteEditText.getText().toString());
        SPUtils.setScale(mContext, mScaleEditText.getText().toString());
        SPUtils.setRotate(mContext, mRotateEditText.getText().toString());
        SPUtils.setImageAlgo(mContext, mImageAlgoEditText.getText().toString());
        SPUtils.setSwitch(mContext, mAutoShutterSwitchEditText.getText().toString());
        SPUtils.setPeriod(mContext, mPeriodEditText.getText().toString());
        SPUtils.setDelay(mContext, mDelayEditText.getText().toString());
    }

    public EditText getPaletteView() {
        return mPaletteEditText;
    }

    public EditText getScaleView() {
        return mScaleEditText;
    }

    public EditText getRotateView() {
        return mRotateEditText;
    }

    public EditText getImageAlgoView() {
        return mImageAlgoEditText;
    }

    public EditText getAutoShutterSwitchView() {
        return mAutoShutterSwitchEditText;
    }

    public EditText getPeriodView() {
        return mPeriodEditText;
    }

    public EditText getDelayView() {
        return mDelayEditText;
    }
}

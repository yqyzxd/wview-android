package com.wind.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dyhdyh.support.countdowntimer.CountDownTimerSupport;
import com.dyhdyh.support.countdowntimer.OnCountDownTimerListener;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created By wind
 * on 2019-12-24
 */
public class CountdownTextView extends TextView {
    public CountdownTextView(Context context) {
        this(context, null);
    }

    public CountdownTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    CountDownTimerSupport mTimer;

    private void init() {

    }
    private String mPrefixText="";
    public void setPrefixText(String prefix){
        mPrefixText=prefix;
    }
    public void startCountdown(long ms) {
        if (mTimer != null) {
            mTimer.stop();
            mTimer = null;
        }
        mTimer = new CountDownTimerSupport(ms, 1000);
        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                //间隔回调
                //format  setText
                String text = formatTime(millisUntilFinished);
                setText(mPrefixText+text);

                if (mOnCountDownTimerListener!=null){
                    mOnCountDownTimerListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                //计时器停止
                mTimer = null;
                if (mOnCountDownTimerListener!=null){
                    mOnCountDownTimerListener.onFinish();
                }
            }
        });

        mTimer.start();
    }
    OnCountDownTimerListener mOnCountDownTimerListener;
    public void setOnCountDownTimerListener(OnCountDownTimerListener listener){
        this.mOnCountDownTimerListener=listener;
    }
    private String mPattern;
    public void setFormatPattern(String pattern){
        this.mPattern=pattern;
    }
    public String formatTime(long milliTimes) {
        if (TextUtils.isEmpty(mPattern)){
            mPattern="HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(mPattern);//这里想要只保留分秒可以写成"mm:ss"
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(milliTimes);
        //System.out.println(hms);
        return hms;
    }


    public void stop() {
        if (mTimer != null) {
            mTimer.stop();
            mTimer = null;
        }
    }


}

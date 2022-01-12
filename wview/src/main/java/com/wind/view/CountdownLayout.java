package com.wind.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Created by wind on 2017/3/7.
 */

public class CountdownLayout extends FrameLayout {

    private final int DEFAULT_COUNTDOWN_SECOND=60;
    public CountdownLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CountdownLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountdownLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    TextView tv_show;
    private int mTotalSeconds;
    private boolean mCanCountDown;
    private void init(){

        inflate(getContext(), R.layout.wd_layout_countdown,this);
        tv_show= findViewById(R.id.tv);
        mTotalSeconds=DEFAULT_COUNTDOWN_SECOND;
        mCanCountDown=true;
    }

    public void setInitText(String initText, int textColor){
        tv_show.setText(initText);
        tv_show.setTextColor(textColor);
    }
    public void setInitText(String initText, ColorStateList stateList){
        tv_show.setText(initText);
        tv_show.setTextColor(stateList);
    }
    @Override
    public void setEnabled(boolean enabled) {
        //super.setEnabled(enabled);
        tv_show.setEnabled(enabled);
    }

    /**
     * 设置倒计时总时间
     * @param seconds 单位为秒
     */
    public void setTotalSeconds(int seconds){
        this.mTotalSeconds=seconds;
        if (mTotalSeconds>0){
            showCountdown(mTotalSeconds);
        }else {
            tv_show.setText("重新发送");
        }
        startCountdown();
    }

    CountdownHandler mHandler;
    public void startCountdown(){
        if (mHandler!=null){
            mHandler.removeMessages(CountdownHandler.WHAT_COUNTDOWN);
            mHandler=null;
        }
        mHandler=new CountdownHandler(getContext(),mTotalSeconds);
        mHandler.startCountdown();
        mCanCountDown=false;
    }
    public void stop(){
        if (mHandler!=null){
            mHandler.removeMessages(CountdownHandler.WHAT_COUNTDOWN);
            mHandler=null;
        }
    }
    public boolean canCountdown() {
        return mCanCountDown && tv_show.isEnabled();
    }

    private class CountdownHandler extends Handler {

        private WeakReference<Context> ref;
        private int num;
        public static final int WHAT_COUNTDOWN=1;
        public CountdownHandler(Context context, int num){
            ref=new WeakReference<Context>(context);
            this.num=num;
        }
        @Override
        public void handleMessage(Message msg) {
            if (ref==null&&ref.get()==null){
                return;
            }

            switch (msg.what){
                case WHAT_COUNTDOWN:
                    num--;
                    if (num>0){


                        showCountdown(num);
                        //Log.e("Countdown","mCallback is null:"+(mCallback==null));
                        if (mCallback!=null){
                            mCallback.onCountdownUpdate(num);
                        }
                        startCountdown();
                    }else {
                        mCanCountDown=true;
                        tv_show.setText("重新发送");

                        if (mCallback!=null){
                            mCallback.onCountdownUpdate(0);
                            mCallback.onCountdownEnd();
                        }
                    }
                    break;
            }

        }

        public void startCountdown() {
            mHandler.sendEmptyMessageDelayed(WHAT_COUNTDOWN,1000);
        }
    }

    private void showCountdown(int seconds) {
        //获取分钟，
        //int minute=seconds/60;
        int second=seconds%60;
        if (second<10){
            tv_show.setText(getContext().getString(R.string.wd_countdown,"0"+second));
        }else
            tv_show.setText(getContext().getString(R.string.wd_countdown,""+second));
    }

    private CountdownCallback mCallback;
    public void setCountdownCallback(CountdownCallback callback){
        this.mCallback=callback;
    }
    public interface CountdownCallback{
        void onCountdownUpdate(int leftSeconds);
        void onCountdownEnd();
    }
}

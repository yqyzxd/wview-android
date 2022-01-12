package com.wind.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.widget.TextView;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wind on 2018/3/19.
 */

public class TimeCountdownView extends TextView {
    public TimeCountdownView(Context context) {
        super(context);
        init();
    }

    public TimeCountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeCountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    private static final int WHAT_TIMECOUNTDOWN = 1;
    private Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_TIMECOUNTDOWN:
                    long leftSecondTime = (long) msg.obj;
                    setLeftTime(leftSecondTime * 1000);
                    break;

            }
        }
    };
    private long mLeftSecondTime;
    private ScheduledExecutorService mScheduledExecutorService;

    public void setExecutor(ScheduledExecutorService scheduledExecutorService) {
        mScheduledExecutorService = scheduledExecutorService;
    }

    public void setTotalTime(long totalTime){
        mLeftSecondTime=totalTime;
    }
    public void startCountdown() {
        startCountdown(null);
    }
    public void startCountdown(ScheduledExecutorService scheduledExecutorService) {
        if (mLeftSecondTime<=0){
            return;
        }
        if (scheduledExecutorService!=null){
            mScheduledExecutorService=scheduledExecutorService;
        }
        if (mScheduledExecutorService == null) {
            mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }


        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //System.out.println("TimeCountdownView run");
                mLeftSecondTime = mLeftSecondTime - 1;
                Message msg = Message.obtain(mTimeHandler, WHAT_TIMECOUNTDOWN, mLeftSecondTime);
                mTimeHandler.sendMessage(msg);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void setLeftTime(long milliTime) {
        if (milliTime < 0) {
            return;
        }

       /* String leftPayTime =format2ddHHmmss(milliTime);
        //String desc = mOrderDetail.getMsg();
        String desc =mDectorText;
        int placeholderIndex = desc.indexOf("%s");
        if (placeholderIndex >= 0) {
            String retStr = String.format(desc, leftPayTime);
            SpannableString ss = new SpannableString(retStr);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#E62E34"));
            ss.setSpan(foregroundColorSpan,
                    placeholderIndex, placeholderIndex + leftPayTime.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            setText(ss);
        } else {
            setText(leftPayTime);
        }*/

        //System.out.println("countdown milliTime:"+milliTime);
        if (milliTime==0){
            stopCountdown();
            if (mListener!=null){
                mListener.onCountdownEnd();
            }
        }else {
            setText(mDectorText.replace("%1$s",(milliTime/1000)+""));
        }
    }
    private String mDectorText;
    public void setDectorText(String dectorText) {
        this.mDectorText=dectorText;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //System.out.println("SplashFragment TimeCountdownView stopCountdown()");
        stopCountdown();
    }

    /**
     * 结束倒计时
     */
    public void stopCountdown(){
        if (mScheduledExecutorService!=null){
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService=null;
        }
    }
    private TimeCountdownListener mListener;
    public void setTimeCountdownListener(TimeCountdownListener listener){
        this.mListener=listener;
    }

    /**
     * 倒计时结束监听
     */
    public interface TimeCountdownListener{
        void onCountdownEnd();
    }

    public static String format2ddHHmmss(long milliTime) {
        if (milliTime<0){
            return "00:00";
        }
        long second=milliTime/1000;//总共的秒数
        long minute=second/60;//总共分钟数
        long hour=minute/60;//总共小时数
        long modSecond= second%60;
        StringBuilder sb=new StringBuilder();
        String str_hour=hour+"";
        String str_min=minute+"";
        String str_sec=modSecond+"";
        if (hour==0){
            if (minute<10){
                str_min="0"+minute;
            }
            if (modSecond<10){
                str_sec="0"+modSecond;
            }
            sb.append("00").append(":").append(str_min).append(":").append(str_sec);
        }else {
            if (hour<10){
                str_hour="0"+hour;
            }
         /*   if (minute<10){
                str_min="0"+minute;
            }*/
            if (modSecond<10){
                str_sec="0"+modSecond;
            }
            long modMin=minute%60;
            if (modMin<10){
                str_min="0"+modMin;
            }else {
                str_min=modMin+"";
            }
            sb.append(str_hour).append(":").append(str_min).append(":").append(str_sec);
        }
        return sb.toString();
    }
}

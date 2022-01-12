package com.wind.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * created by wind on 2020/11/11:5:54 PM
 */
public class PopupWindowEx {
    private PopupWindow mPopupWindow;
    private View mContentView;
    public PopupWindowEx(Builder builder){
        mContentView= LayoutInflater.from(builder.context).inflate(builder.layoutResId,null);
        mPopupWindow=new PopupWindow(mContentView,builder.width,builder.height);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(builder.animStyle);

    }

    public void dismiss(){
        if (mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }
    /**
     * 相对于窗体的显示位置
     *
     * @param view 可以为Activity中的任意一个View（最终的效果一样），
     *                   会通过这个View找到其父Window，也就是Activity的Window。
     * @param gravity 在窗体中的位置，默认为Gravity.NO_GRAVITY
     * @param x 表示距离Window边缘的距离，方向由Gravity决定。
     *          例如：设置了Gravity.TOP，则y表示与Window上边缘的距离；
     *          而如果设置了Gravity.BOTTOM，则y表示与下边缘的距离。
     * @param y
     * @return
     */
    public PopupWindowEx showAtLocation(View view,int gravity,int x,int y){

        if (mPopupWindow!=null){
            mPopupWindow.showAtLocation(view,gravity,x,y);
        }
        return this;
    }

    /**
     * 显示在anchor控件的正下方，或者相对这个控件的位置
     *
     * @param anchor
     * @param xoff
     * @param yoff
     * @param gravity
     * @return
     */
    public PopupWindowEx showAsDropDown(View anchor, int xoff, int yoff,int gravity) {
        if (mPopupWindow != null) {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
                mPopupWindow.showAsDropDown(anchor, xoff, yoff, gravity);
            }
        }
        return this;
    }

    /**
     * 根据id获取view
     *
     * @param viewId
     * @return
     */
    public View getItemView(int viewId) {
        if (mPopupWindow != null) {
            return mContentView.findViewById(viewId);
        }
        return null;
    }

    /**
     * 根据id设置pop内部的控件的点击事件的监听
     *
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getItemView(viewId);
        view.setOnClickListener(listener);
    }

    public static class Builder{
        private Context context;
        private int layoutResId;
        private int width;
        private int height;
        private int animStyle;


        public Builder(Context context){
            this.context=context;
        }

        public Builder setContentView(int layoutResId){
            this.layoutResId=layoutResId;
            return this;
        }
        public Builder setWidth(int width){
            this.width=width;
            return this;
        }
        public Builder setHeight(int height){
            this.height=height;
            return this;
        }

        public Builder setAnimationStyle(int animStyle){
            this.animStyle=animStyle;
            return this;
        }

        public PopupWindowEx build(){

            return new PopupWindowEx(this);
        }


    }
}

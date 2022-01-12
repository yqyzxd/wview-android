package com.wind.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BadgeView extends TextView {
    public BadgeView(@NonNull Context context) {
        this(context,null);
    }

    public BadgeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BadgeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private int defaltBg=R.drawable.wd_shape_circlee15252;
    private void init(AttributeSet attrs){
        Drawable bg=getBackground();
        /*int bg=attrs.getAttributeIntValue(android.R.id.background,-1);
        if (bg==-1) {
            setBackgroundResource(defaltBg);
        }*/
        if (bg==null){
            setBackgroundResource(defaltBg);
        }

        setGravity(Gravity.CENTER);
    }

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height=getMeasuredHeight();
        setMeasuredDimension(height,height);
    }*/

    public void setBagde(int bagde){
        this.setText(bagde+"");
    }
    public void setCustomText(String text){
        this.setText(text);
    }


}

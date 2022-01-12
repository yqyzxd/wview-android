package com.wind.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by wind on 2018/1/26.
 */

public class TitleBar extends FrameLayout {

    public TitleBar(@NonNull Context context) {
        super(context);
        init();
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private TextView mCenterView;
    private TextView mRightView;
    private ImageView mLeftView;
    private View mLine;
    private View mTitleBarContainer;
    private void init() {
        inflate(getContext(), R.layout.title_bar, this);
        mCenterView = findViewById(R.id.tv_title);
        mLeftView = findViewById(R.id.iv_left);
        mRightView = findViewById(R.id.tv_right);
        mLine = findViewById(R.id.line_titlebar);

        mTitleBarContainer = findViewById(R.id.title_bar_container);

        mLeftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.onBackPressed();
                }
            }
        });
    }

    public TitleBar setLeftIcon(int resId) {
        mLeftView.setImageResource(resId);
        return this;
    }

    public TitleBar setTitleVisible(boolean visible) {
        // 标题是否可见
        mCenterView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    public TitleBar setTitle(String title) {
        mCenterView.setText(title);
        return this;
    }

    public TitleBar setTextColor(int color) {
        mCenterView.setTextColor(color);
        mRightView.setTextColor(color);
        return this;
    }

    public ImageView getLeftView() {
        return mLeftView;
    }

    public TextView getRightView() {
        return mRightView;
    }

    public TitleBar setRightText(String text) {
        mRightView.setText(text);
        return this;
    }

    public TitleBar setRightIcon(int resId) {
        mRightView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        return this;
    }

    /**
     * 设置背景透明度
     * @param alpha 0~255
     * @return
     */
    public TitleBar setBgAlpha(int alpha) {
        if (null != mTitleBarContainer.getBackground()) {
            mTitleBarContainer.getBackground().setAlpha(alpha);
        }
        return this;
    }

    public TitleBar setBgColor(int color) {
        mTitleBarContainer.setBackgroundColor(color);
        return this;
    }

    public TitleBar setBgResource(int id) {
        mTitleBarContainer.setBackgroundResource(id);
        return this;
    }

    public TitleBar inflateStatusBar() {
        int statusBarHeight=getStatusBarHeight(getContext());
        View view_placeholder_status_bar=findViewById(R.id.view_placeholder_status_bar);
        ViewGroup.LayoutParams lp=view_placeholder_status_bar.getLayoutParams();
        lp.height=statusBarHeight;
        view_placeholder_status_bar.setLayoutParams(lp);
        return this;
    }

    public void setLeftVisibility(int visibility) {
        mLeftView.setVisibility(visibility);
    }

    public void setLineVisibility(int visibility) {
        mLine.setVisibility(visibility);
    }

    public void setLineColor(int color) {
        mLine.setBackgroundColor(color);
    }

    public void setRightVisibility(int visibility) {
        mRightView.setVisibility(visibility);
    }


    public void onScrollChanged(int y) {
        int start = DisplayUtil.dip2px(getContext(), 50);
        int end = DisplayUtil.dip2px(getContext(), 150);
        //  Log.e("Scroll", "y" + y + "-start:" + start + "-end" + end);
        String hexAlpha = "FF";
        if (y >= start && y <= end) {
            float percent = (y - start) / (float) (end - start);
            int i = (int) (percent * 255);
            // Log.e("Scroll", "y" + y + "-start:" + start + "-end" + end + "-percent:" + percent + "-i:" + i);
            String hex = Integer.toHexString(i);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            String color = "#" + hex + "1f1f1f";
            // Log.e("color", "i" + i + ":" + hex);
            setBgColor(Color.parseColor(color));

            hexAlpha = hex;
        }
        if (y > end) {
            hexAlpha = "FF";
            setBgColor(Color.parseColor("#000000"));

        }


        if (y < start) {
            hexAlpha = "00";
            setBgColor(Color.TRANSPARENT);
        }
        String color = "#" + hexAlpha + "ffffff";
        setTextColor(Color.parseColor(color));
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

package com.wind.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class HeightRatioFrameLayout extends FrameLayout {
	private float mRatio = 1f;

	public HeightRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public HeightRatioFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public void setRatio(float ratio) {
		this.mRatio = ratio;
		requestLayout();
	}

	public HeightRatioFrameLayout(Context context) {
		super(context);
	}

	private void init(AttributeSet attrs) {
		TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.RatioLayout);
		mRatio = typeArray.getFloat(R.styleable.RatioLayout_ratio, 1f);
		typeArray.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childHeightSize = getMeasuredHeight();
		// 高度和宽度一样
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize,
				MeasureSpec.EXACTLY);
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				Math.round(childHeightSize * mRatio), MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}

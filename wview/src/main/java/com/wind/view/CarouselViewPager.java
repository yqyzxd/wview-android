package com.wind.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wind on 2017/3/6.
 */

public class CarouselViewPager extends LinearLayout implements ViewPager.OnPageChangeListener {
    public CarouselViewPager(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CarouselViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CarouselViewPager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CarouselViewPager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
    }

    private WidthRatioFrameLayout mRfl;
    private ViewPager mViewPager;
    private ImagePagerAdapter mImagePagerAdapter;
    private RecyclerView mRvDots;
    private int mCurrentPosition;
    private DotsAdapter mDotsAdapter;
    private CarouselHandler mHander;

    private int mDotMarginBottom, mDotInterDotMargin, mDotSize, mDotSizeWidth, mDotSizeHeight;
    private Drawable mDotDrawable;
    private int mDotGravity;
    private int mDotMarginTop;
    public static final int DOT_GRAVITY_OVERLAYBOTTOM = 0;
    public static final int DOT_GRAVITY_BELOW = 1;
    private float mRatio = 0f;
    private Drawable mSelectedDrawable;
    private Drawable mNormalDrawable;

    private void init(AttributeSet attrs) {
        TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.CarouselViewPager);
        mDotMarginBottom = typeArray
                .getDimensionPixelSize(R.styleable.CarouselViewPager_dots_margin_bottom,
                        getContext().getResources().getDimensionPixelSize(R.dimen.wd_carousel_dots_bottom_margin));
        mDotInterDotMargin = typeArray.
                getDimensionPixelSize(R.styleable.CarouselViewPager_dot_inter_dot_margin,
                        getContext().getResources().getDimensionPixelSize(R.dimen.wd_carousel_dot_inter_dot_margin));
        mDotSize = typeArray.
                getDimensionPixelSize(R.styleable.CarouselViewPager_dot_size,
                        getContext().getResources().getDimensionPixelSize(R.dimen.wd_carousel_dot_size));
        mDotSizeWidth = typeArray.
                getDimensionPixelSize(R.styleable.CarouselViewPager_dot_size_width, 0);
        mDotSizeHeight = typeArray.
                getDimensionPixelSize(R.styleable.CarouselViewPager_dot_size_height, 0);
        mDotDrawable = typeArray.getDrawable(R.styleable.CarouselViewPager_dot_color_selector);

        mSelectedDrawable = typeArray.getDrawable(R.styleable.CarouselViewPager_dot_selected_drawable);
        mNormalDrawable = typeArray.getDrawable(R.styleable.CarouselViewPager_dot_normal_drawable);

        mDotGravity = typeArray.getInteger(R.styleable.CarouselViewPager_dot_gravity, mDotGravity);

        mDotMarginTop = typeArray.
                getDimensionPixelSize(R.styleable.CarouselViewPager_dots_margin_top, 0);

        //???????????????viewpager????????????
        mRatio = typeArray.
                getFloat(R.styleable.CarouselViewPager_ratio_image, 0f);


        typeArray.recycle();
        int layoutId = R.layout.wd_layout_carousel;
        if (mDotGravity == DOT_GRAVITY_BELOW) {
            if (mRatio > 0) {
                layoutId = R.layout.wd_layout_carousel__gravity_below_with_ratio;
            } else {
                layoutId = R.layout.wd_layout_carousel__gravity_below;
            }
        } else if (mDotGravity == DOT_GRAVITY_OVERLAYBOTTOM) {
            if (mRatio > 0) {
                layoutId = R.layout.wd_layout_carousel_with_ratio;
            }

        }
        inflate(getContext(), layoutId, this);
        FrameLayout fl = findViewById(R.id.rfl);
        if (fl!=null && fl instanceof WidthRatioFrameLayout) {
            mRfl = (WidthRatioFrameLayout) fl;
        }
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        //??????ViewPager??????mScroller
        setViewPagerScrollSpeed(mViewPager);

        mRvDots = (RecyclerView) findViewById(R.id.rv_dots);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvDots.setLayoutManager(layoutManager);
        mViewPager.addOnPageChangeListener(this);
        mDotsAdapter = new DotsAdapter();
        mRvDots.setAdapter(mDotsAdapter);


        MarginLayoutParams params = (MarginLayoutParams) mRvDots.getLayoutParams();
        params.bottomMargin = mDotMarginBottom;
        params.topMargin = mDotMarginTop;

        if (mRatio > 0 && mRfl != null) {
            mRfl.setmRatio(mRatio);
        }
    }


    private void setViewPagerScrollSpeed(ViewPager viewPager) {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ViewPager.OnPageChangeListener mOutOnPageChangeListener;

    public void setImageResIds(List<Integer> resIds) {
        setupPager(resIds);
        mSize = resIds.size();
//        startCarousel();
    }


    private int mSize;

    public void setImageResPaths(List<String> items) {
        setupPager(items);
        mSize = items.size();
//        startCarousel();

    }

    private void setupPager(List resIds) {

        mImagePagerAdapter = new ImagePagerAdapter(getContext(), resIds);
        mViewPager.setAdapter(mImagePagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        if (resIds.size() > 1)
            mDotsAdapter.replaceAll(resIds);

    }


    public void setCurrentItem(int position) {
        if (position >= 0 && position < mImagePagerAdapter.getCount()) {
            mViewPager.setCurrentItem(position);
        }
    }

    private int mInterval;

    public void startCarousel(int interval, boolean showDots) {
        cancel();
        mInterval = interval;
        mHander = new CarouselHandler(getContext(), mSize);
        //????????????
        mHander.sendEmptyMessageDelayed(CarouselHandler.MSG_UPDATE_IMAGE, mInterval);
        if (!showDots) {
            mRvDots.setVisibility(GONE);
        }
    }

    public void startCarousel(int interval) {
        startCarousel(interval, true);
    }

    public void startCarousel() {
        startCarousel(1500, true);
    }

    public void startCarousel(boolean showDots) {
        startCarousel(1500, showDots);
    }

    public boolean isCarouseling() {
        return mHander != null;
    }

    public void cancel() {
        if (mHander != null) {
            mHander.removeMessages(CarouselHandler.MSG_UPDATE_IMAGE);
            mHander = null;
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOutOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOutOnPageChangeListener != null) {
            mOutOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        // LogUtil.e("CarouselViewPager","onPageSelected");
        mCurrentPosition = position;
        mDotsAdapter.notifyDataSetChanged();

        if (mOutOnPageChangeListener != null) {
            mOutOnPageChangeListener.onPageSelected(position);
        }

        if (mHander == null) {
            return;
        }
        mHander.sendMessage(Message.obtain(mHander, CarouselHandler.MSG_PAGE_CHANGED, position, 0));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOutOnPageChangeListener != null) {
            mOutOnPageChangeListener.onPageScrollStateChanged(state);
        }
        if (mHander == null) {
            return;
        }
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                mHander.sendEmptyMessage(CarouselHandler.MSG_KEEP_SILENT);
                break;
            case ViewPager.SCROLL_STATE_IDLE:
                mHander.sendEmptyMessageDelayed(CarouselHandler.MSG_UPDATE_IMAGE, mInterval);
                break;
            default:
                break;
        }
    }

    private class DotsAdapter extends RecyclerView.Adapter<DotsAdapter.ViewHolder> {

        private List items;

        private DotsAdapter() {
            items = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.wd_carousel_item_dot, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == mCurrentPosition) {
                holder.itemView.setActivated(true);
                if (mSelectedDrawable != null)
                    holder.itemView.setBackground(mSelectedDrawable);
            } else {
                holder.itemView.setActivated(false);
                if (mNormalDrawable != null)
                    holder.itemView.setBackground(mNormalDrawable);
            }

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void replaceAll(List resIds) {
            items.clear();
            items.addAll(resIds);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);

                MarginLayoutParams params = (MarginLayoutParams) itemView.getLayoutParams();
                params.leftMargin = mDotInterDotMargin;
                params.rightMargin = mDotInterDotMargin;
                if (mDotSizeWidth != 0 || mDotSizeHeight != 0) {
                    params.width = mDotSizeWidth;
                    params.height = mDotSizeHeight;
                } else {
                    params.width = mDotSize;
                    params.height = mDotSize;
                }


            }
        }
    }

    private class ImagePagerAdapter<T> extends PagerAdapter {
        List<T> items;
        private Context mContext;

        public ImagePagerAdapter(Context context, List<T> items) {
            this.items = items;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {  //?????????????????????????????????
            ImageView iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            T path = items.get(position);

            if (path instanceof String) {
                String url = (String) (items.get(position));
                ViewCompat.setTransitionName(iv,url);
                iv.setTag(url);

                if (url.endsWith(".gif")) {
                    Glide.with(mContext)
                            .asGif()
                            .load(url)
//                    .dontAnimate() //??????????????????
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE) //DiskCacheStrategy.NONE
                            .into(iv);
                } else {
                    Glide.with(mContext)
                            .load(url)
                            .placeholder(R.drawable.placeholder_bg)
                            .into(iv);
                }
                //WdImageLoader.display(mContext, iv, (String) (items.get(position)), R.drawable.placeholder_bg);
            } else {
                //WdImageLoader.display(mContext, iv, (Integer) (items.get(position)), R.drawable.placeholder_bg);

                int resId = (Integer) (items.get(position));
                ViewCompat.setTransitionName(iv,String.valueOf(resId));
                iv.setTag(String.valueOf(resId));
                Glide.with(mContext)
                        .load(resId)
                        .placeholder(R.drawable.placeholder_bg)
                        .into(iv);
            }
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCarouseItemClick(position);
                    }
                    if (mListener2!=null){
                        mListener2.onCarouseItemClick(v,position);
                    }
                }
            });
            container.addView(iv, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return iv;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    private OnCarouselItemClickListener mListener;
    private OnCarouselItemClickListener2 mListener2;

    public void setOnCarouselItemClickListener(OnCarouselItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnCarouselItemClickListener2(OnCarouselItemClickListener2 listener) {
        this.mListener2 = listener;
    }


    public interface OnCarouselItemClickListener {
        void onCarouseItemClick(int position);
    }

    public interface OnCarouselItemClickListener2{
        void onCarouseItemClick(View view,int position);
    }

    private class CarouselHandler extends Handler {

        /**
         * ?????????????????????View???
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * ?????????????????????
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * ?????????????????????
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????????????????????????????????????????????????????
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        //??????????????????
        protected static final long MSG_DELAY = 1500;

        //?????????????????????Handler??????.?????????????????????????????????Activity???????????????Fragment???
        private WeakReference<Context> weakReference;
        private int length;

        protected CarouselHandler(Context context, int length) {
            weakReference = new WeakReference<Context>(context);
            this.length = length;
        }

      /*  public void startCarouse() {
            sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
        }*/

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context activity = weakReference.get();
            if (activity == null) {
                //Activity??????????????????????????????UI???
                return;
            }
            if (length == 0) {
                return;
            }
            if (msg.what == MSG_UPDATE_IMAGE) {
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (hasMessages(MSG_UPDATE_IMAGE)) {
                    removeMessages(MSG_UPDATE_IMAGE);
                }
            }

            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    mCurrentPosition++;
                    mCurrentPosition = mCurrentPosition % length;
                    mViewPager.setCurrentItem(mCurrentPosition, true);
                    //??????????????????
                    sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, mInterval);
                    break;
                case MSG_KEEP_SILENT:
                    //?????????????????????????????????
                    break;
                case MSG_BREAK_SILENT:
                    sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, mInterval);
                    break;
                case MSG_PAGE_CHANGED:
                    //?????????????????????????????????????????????????????????????????????
                    mCurrentPosition = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }


}

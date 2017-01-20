package com.chenyu.library.bannerViewPager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * A BannerViewPager owns its indicators and it can roll automatically.
 * BannerViewPager支持指示器以及自动轮播。
 *
 * @author Chen Yu
 * @version 1.1.0
 * @date 2017-01-20
 */
public class BannerViewPager extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;
    private ViewPagerAdapter mAdapter;
    private Context mContext;
    private int mCurrentPosition;

    //viewpager's rolling state
    private int mViewPagerScrollState;
    //by default,auto-rolling is on.
    private boolean isAutoRolling = true;
    //the interval between rollings
    private int mAutoRollingTime = 4000;
    private int mReleasingTime = 0;
    private static final int MESSAGE_AUTO_ROLLING = 0X1001;
    private static final int MESSAGE_AUTO_ROLLING_CANCEL = 0X1002;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_AUTO_ROLLING:
                    if(mCurrentPosition == mAdapter.getCount() - 1){
                        mViewPager.setCurrentItem(0,true);
                    }else {
                        mViewPager.setCurrentItem(mCurrentPosition + 1,true);
                    }
                    postDelayed(mAutoRollingTask,mAutoRollingTime);
                    break;
                case MESSAGE_AUTO_ROLLING_CANCEL:
                    postDelayed(mAutoRollingTask,mAutoRollingTime);
                    break;
            }
        }
    };

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        //initialize the viewpager
        mViewPager = new ViewPager(mContext);
        ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
        lp.width = ViewPager.LayoutParams.MATCH_PARENT;
        lp.height = ViewPager.LayoutParams.MATCH_PARENT;
        mViewPager.setLayoutParams(lp);

        //initialize the indicator
        mIndicator = new ViewPagerIndicator(mContext);
        FrameLayout.LayoutParams indicatorlp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        indicatorlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        indicatorlp.bottomMargin = 20;
        mIndicator.setLayoutParams(indicatorlp);
    }

    public void setAutoRolling(boolean isAutoRolling){
        this.isAutoRolling = isAutoRolling;
    }

    public void setAutoRollingTime(int time){
        this.mAutoRollingTime = time;
    }

    public void setAdapter(ViewPagerAdapter adapter){
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);

        mAdapter = adapter;
        mAdapter.registerSubscriber(new DataSetSubscriber() {
            @Override
            public void update(int count) {
                mIndicator.setItemCount(count);
            }
        });

        //add the viewpager and the indicator to the container.
        addView(mViewPager);
        addView(mIndicator);

        //start the auto-rolling task if needed
        if(isAutoRolling){
            postDelayed(mAutoRollingTask,mAutoRollingTime);
        }

    }

    /**
     * If the current page is being dragged by the user,this method will be invoke.
     * And then it will call {@link ViewPagerIndicator#setPositionAndOffset}.
     * @param position Position index of the first page currently being displayed.
     * @param offset Value from [0, 1) indicating the offset from the page at position.
     */
    private void setIndicator(int position,float offset){
        mIndicator.setPositionAndOffset(position,offset);
    }

    /**
     * This runnable decides the viewpager should roll to next page or wait.
     */
    private Runnable mAutoRollingTask = new Runnable() {
        @Override
        public void run() {
            int now = (int) System.currentTimeMillis();
            int timediff = mAutoRollingTime;
            if(mReleasingTime != 0){
                timediff = now - mReleasingTime;
            }

            if(mViewPagerScrollState == ViewPager.SCROLL_STATE_IDLE){
                //if user's finger just left the screen,we should wait for a while.
                if(timediff >= mAutoRollingTime * 0.8){
                    mHandler.sendEmptyMessage(MESSAGE_AUTO_ROLLING);
                }else {
                    mHandler.sendEmptyMessage(MESSAGE_AUTO_ROLLING_CANCEL);
                }
            }else if(mViewPagerScrollState == ViewPager.SCROLL_STATE_DRAGGING){
                mHandler.sendEmptyMessage(MESSAGE_AUTO_ROLLING_CANCEL);
            }

        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        setIndicator(position,positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == ViewPager.SCROLL_STATE_DRAGGING){
            mViewPagerScrollState = ViewPager.SCROLL_STATE_DRAGGING;
        }else if(state == ViewPager.SCROLL_STATE_IDLE){
            mReleasingTime = (int) System.currentTimeMillis();
            mViewPagerScrollState = ViewPager.SCROLL_STATE_IDLE;
        }

    }

    /**
     * Save the state of this BannerViewPager.The current position will be saved.
     * @return Parcelable
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.currentPosition = mCurrentPosition;
        return ss;
    }

    /**
     * Restore the BannerViewPager from the previous state.
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mViewPager.setCurrentItem(ss.currentPosition);
    }

    static class SavedState extends BaseSavedState{

        int currentPosition;

        public SavedState(Parcel source) {
            super(source);
            currentPosition = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>(){

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

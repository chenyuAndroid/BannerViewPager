package com.chenyu.library.bannerViewPager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager's adapter.It use {@link DataSetSubscriber} and {@link DataSetSubject} to tell
 * {@link ViewPagerIndicator} that the dataset has been changed,so ViewPagerIndicator will
 * requestlayout to adjust the new state.
 *
 * @author Chen Yu
 * @version 1.0.0
 * @date 2016-10-03
 */
public class ViewPagerAdapter extends PagerAdapter implements DataSetSubject {

    private List<DataSetSubscriber> mSubscribers = new ArrayList<>();
    private List<View> mDataViews;
    private OnPageClickListener mOnPageClickListener;

    /**
     * 构造函数
     * @param mDataViews view列表
     */
    public ViewPagerAdapter(List<View> mDataViews,OnPageClickListener listener) {
        this.mDataViews = mDataViews;
        this.mOnPageClickListener = listener;
    }

    public View getView(int location){
        return this.mDataViews.get(location);
    }

    @Override
    public int getCount() {
        return mDataViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = mDataViews.get(position);
        final int i = position;
        if(mOnPageClickListener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPageClickListener.onPageClick(v,i);
                }
            });
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        notifySubscriber();
    }

    @Override
    public void registerSubscriber(DataSetSubscriber subscriber) {
        mSubscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(DataSetSubscriber subscriber) {
        mSubscribers.remove(subscriber);
    }

    @Override
    public void notifySubscriber() {
        for(DataSetSubscriber subscriber : mSubscribers){
            subscriber.update(getCount());
        }
    }
}

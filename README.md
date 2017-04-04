# BannerViewPager
ViewPager with indicator and auto-rolling function.
带指示器的、支持自动轮播的ViewPager

![pic01](https://github.com/chenyuAndroid/BannerViewPager/blob/master/BannerViewPager/pic/banner01%20.gif)
![pic02](https://github.com/chenyuAndroid/BannerViewPager/blob/master/BannerViewPager/pic/banner02.gif)


# How to use it?
1、add the BannerViewPager to your xml files：
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.chenyu.library.bannerViewPager.BannerViewPager
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="200dp">

    </com.chenyu.library.bannerViewPager.BannerViewPager>

    <!-- others -->
</LinearLayout>
```

2、get the instance of BannerViewPager,and the set adapter for it.
```java
//获取BannerViewPager实例
bannerViewPager = (BannerViewPager) findViewById(R.id.banner);
//实例化ViewPagerAdapter，第一个参数是View集合，第二个参数是页面点击监听器
mAdapter = new ViewPagerAdapter(mViews, new OnPageClickListener() {
    @Override
    public void onPageClick(View view, int position) {
        Log.d("cylog","position:"+position);
    }
});
//设置适配器
bannerViewPager.setAdapter(mAdapter);
```
# Update 
2017-04-04
1、Fix some bugs which may lead to the a memory leak.For example,it will remove the runnable from view each time the BannerViewPager becomes INVISIBLE or GONE.It means that BannerViewPager will stop auto-rolling while it is INVISIBLE or GONE.

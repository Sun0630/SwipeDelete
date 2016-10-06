package com.sunxin.swipedelete;

/**
 * Created by sunxin on 2016/10/6.
 */
public class SwipeLayoutManager {
    //使用单例模式，构造出对象来管理滑动菜单

    private SwipeLayoutManager() {
    }

    private static SwipeLayoutManager mInstance = new SwipeLayoutManager();

    public static SwipeLayoutManager getInstance() {

        return mInstance;
    }


    //用来记录当前的SwipeLayout的状态
    private SwipeLayout currentLayout;

    public void setSwipeLayout(SwipeLayout Layout) {
        this.currentLayout = Layout;
    }

    /**
     * 清空当前记录的已经打开的layout
     */
    public void clearCurrentLayout() {
        currentLayout = null;
    }


    /**
     * 关闭当前打开的layout
     */
    public void closeCurrentLayout() {
        if (currentLayout != null) {

            currentLayout.close();
        }
    }


    /**
     * 判断档期的layout是否应该能被滑动
     *
     * @param swipeLayout 要被判断的swipeLayout，如果该layout和当前的layout相同，那么也是可以滑动的。
     * @return true 可以滑动
     */
    public boolean isShouldSwipe(SwipeLayout swipeLayout) {
        if (currentLayout == null) {
            //说明没有打开的SwipeLayout，可以滑动
            return true;
        } else {
            //说明有打开的layout，但要判断滑动的是否是当前打开的layout
            //如果是当前打开的layout，那么还是可以滑动的
            return currentLayout == swipeLayout;
        }
    }

}

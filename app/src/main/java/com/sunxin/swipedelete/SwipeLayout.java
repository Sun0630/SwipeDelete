package com.sunxin.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by sunxin on 2016/10/6.
 */
public class SwipeLayout extends FrameLayout {

    private View mContentView;
    private View mDeleteView;
    private int mContentViewWidth;
    private int mDeleteWidth;
    private int mDeleteHeight;

    private ViewDragHelper mDragHelper;

    private SwipeLayoutManager mManager = SwipeLayoutManager.getInstance();

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    //定义开关状态
    enum SwipeState {
        Open, Close;
    }

    //定义当前的状态默认为关
    private SwipeState currentState = SwipeState.Close;


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //拿到两个子View
        //内容区域
        mContentView = getChildAt(0);
        //删除区域
        mDeleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获得宽高
        mContentViewWidth = mContentView.getMeasuredWidth();
        mDeleteWidth = mDeleteView.getMeasuredWidth();
        mDeleteHeight = mDeleteView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //布局
        mContentView.layout(0, 0, mContentViewWidth, mDeleteHeight);
        mDeleteView.layout(mContentView.getRight(), 0, mContentViewWidth + mDeleteWidth, mDeleteHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //根据ViewDragHelper来决定是否拦截事件
        boolean res = mDragHelper.shouldInterceptTouchEvent(ev);
        //如果当前有打开的，直接拦截。交给OnTouchEvent处理
        if (!mManager.isShouldSwipe(this)) {
            //说明有打开的，那么先要关闭
            mManager.closeCurrentLayout();
            res = true;
        }

        return res;
    }


    private float mDownX, mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //如果当前有打开的，那么下面的逻辑不能执行，
        if (!mManager.isShouldSwipe(this)) {

            //并且不能上下滑动
            requestDisallowInterceptTouchEvent(true);
            //让下面的逻辑不能执行
            return true;
        }


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录按下的点的坐标
                mDownX = event.getX();
                mDownY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                //移动的点
                float moveX = event.getX();
                float moveY = event.getY();

                //计算差值

                float deltaX = moveX - mDownX;
                float deltaY = moveY - mDownY;

                //判断是偏向水平移动还是偏向竖直移动
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    //偏向水平移动更多，那么就请求不让父View拦截事件
                    requestDisallowInterceptTouchEvent(true);
                }
                //更新坐标
                mDownX = moveX;
                mDownY = moveY;

                break;
            case MotionEvent.ACTION_UP:
                break;

        }


        //把触摸事件都传递给ViewDragHelper进行处理
        mDragHelper.processTouchEvent(event);
        return true;
    }

    //设置ViewDragHelper的回调
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

        /**
         * 判断子View是否捕获
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView || child == mDeleteView;
        }

        /**
         * 当View的位置改变时调用
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == mContentView) {
                //如果拖动的是mContentView
                mDeleteView.layout(mDeleteView.getLeft() + dx, mDeleteView.getTop() + dy,
                        mDeleteView.getRight() + dx, mDeleteView.getBottom() + dy);
            } else if (changedView == mDeleteView) {
                //如果拖动的是mDeleteView
                mContentView.layout(mContentView.getLeft() + dx, mContentView.getTop() + dy,
                        mContentView.getRight() + dx, mContentView.getBottom() + dy);
            }

            //判断SwipeLayout的状态，是打开的还是关闭的
            //根据left值判断
            if (mContentView.getLeft() == 0 && currentState != SwipeState.Close) {
                //说明是关闭的
                currentState = SwipeState.Close;

                if (mListener != null){
                    mListener.onClose(getTag());
                }

                //清空一下
                SwipeLayoutManager.getInstance().clearCurrentLayout();

            } else if (mContentView.getLeft() == -mDeleteWidth && currentState != SwipeState.Open) {
                //说明是打开的
                currentState = SwipeState.Open;

                if (mListener != null){
                    mListener.onOpen(getTag());

                }

                //使用SwipeLayoutManager记录一下状态
                SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);

            }


        }

        /**
         * 获取子View在水平方向上的拖拽范围
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            //水平方向上的拖拽范围就是删除View的宽度
            return mDeleteWidth;
        }

        /**
         * 控制子View在水平方向上移动
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制左右边界
            if (child == mContentView) {
                if (left > 0) {
                    left = 0;
                } else if (left < -mDeleteWidth) {
                    left = -mDeleteWidth;
                }
            } else if (child == mDeleteView) {
                if (left < mContentViewWidth - mDeleteWidth) {
                    left = mContentViewWidth - mDeleteWidth;
                } else if (left > mContentViewWidth) {
                    left = mContentViewWidth;
                }
            }

            return left;
        }

        /**
         * 当松开的时候调用
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //缓慢滑动
            //判断mContentView的left值
            if (mContentView.getLeft() < -mDeleteWidth / 2) {
                //打开

                open();

            } else {

                //关闭
                close();
            }
        }
    };

    /**
     * 打开
     */
    public void open() {
        mDragHelper.smoothSlideViewTo(mContentView, -mDeleteWidth, mContentView.getTop());
        //重绘
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    /**
     * 关闭
     */
    public void close() {
        mDragHelper.smoothSlideViewTo(mContentView,0 , mContentView.getTop());
        //重绘
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }


    //对外提供打开或关闭的位置
    public void setTag(){

    }

    //对外提供接口回调
    private OnSwipeStateChangedListener mListener;

    public void setOnSwipeStateChangedListener(OnSwipeStateChangedListener mListener){
        this.mListener = mListener;
    }

    interface OnSwipeStateChangedListener{
        void onOpen(Object object);
        void onClose(Object object);
    }
}

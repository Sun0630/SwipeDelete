package com.sunxin.swipedelete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    private ArrayList<String> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //准备数据
        for (int i = 0; i < 30; i++) {
            datas.add("friend - " + i);
        }

        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(new MyAdapter());

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //当ListView当前状态为触摸滑动的时候，关闭
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    private class MyAdapter extends BaseAdapter implements SwipeLayout.OnSwipeStateChangedListener {

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = View.inflate(MainActivity.this,R.layout.item_view,null);
            }else{

            }

            ViewHolder holder = ViewHolder.getHolder(convertView);

            holder.mSwipeLayout.setOnSwipeStateChangedListener(this);

            holder.mSwipeLayout.setTag(position);
            //设置数据
            holder.name.setText(datas.get(position));

            return convertView;
        }

        @Override
        public void onOpen(Object object) {
            Toast.makeText(MainActivity.this,"第"+(Integer)object+"个打开了",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onClose(Object object) {
            Toast.makeText(MainActivity.this,"第"+(Integer)object+"个关闭了",Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder{
        TextView name,delete;
        SwipeLayout mSwipeLayout;
        public ViewHolder(View convertView){
            name = (TextView) convertView.findViewById(R.id.tv_name);
            delete = (TextView) convertView.findViewById(R.id.tv_delete);
            mSwipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipelayout);
        }

        public static ViewHolder getHolder(View convertView){
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null){
                holder = new ViewHolder(convertView);
            }else{
                convertView.setTag(holder);
            }

            return  holder;
        }
    }
}

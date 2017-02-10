package com.wj.fragment.bbsfrag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.administrator.myforever.R;
import com.wj.activity.MainActivity;


/**
 * Created by 汪京 on 2016/7/7.
 */
public class BbsDetailFragment extends Fragment {
    private ListView mlistView;
    private MyBBSAdapter myBBSAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bbs_details, null);
        initViews();
        initData();
        initEvents();
        return view;
    }

    private void initViews() {
        mlistView = (ListView) view.findViewById(R.id.listView);
        myBBSAdapter = new MyBBSAdapter(getContext());
    }

    private void initData() {
        mlistView.setAdapter(myBBSAdapter);
    }

    private void initEvents() {
        //处理滑动冲突
        mlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean enable = false;
                if(mlistView != null&&mlistView.getChildCount()>0){
                    boolean firstItemVisible = mlistView.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVisible = mlistView.getChildAt(0).getTop() == 0;
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                ((MainActivity)getActivity()).bbsFragment.swipeRefreshLayout.setEnabled(enable);
            }
        });
    }
}

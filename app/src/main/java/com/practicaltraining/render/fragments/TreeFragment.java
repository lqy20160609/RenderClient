package com.practicaltraining.render.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.TreeStructureAdapter;
import com.practicaltraining.render.callbacks.CheckListener;
import com.practicaltraining.render.callbacks.OnItemLongClickListener;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.core.Node;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.utils.TreeNodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TreeFragment extends FatherFragment {
    private List<Node> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private Dialog progressDialog;
    private TreeStructureAdapter mAdapter;

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden&&(ModelsFragment.meshCount!=0)&&StaticVar.node!=null) {
            SocketIOManager.getInstance().setCreatedModelFinished(()->{
                if (progressDialog!=null) {
                    progressDialog.dismiss();
                }
            });
            Node tempRoot = StaticVar.node;
            String meshName = ModelsFragment.meshName;
            StaticVar.meshNum++;
            Node obj = new Node(StaticVar.meshNum,tempRoot.getId(),tempRoot.getLevel()+1,meshName,tempRoot.isSelected());
            mData.add(TreeNodeUtil.getLastAddPostion(mData,tempRoot),obj);
            tempRoot.getChildren().add(obj);
            mAdapter.notifyDataSetChanged();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation_type", 0);
            jsonObject.put("parent", tempRoot.getId());
            jsonObject.put("son", obj.getId());
            jsonObject.put("meshId", 0);
            SocketIOManager.getInstance().getNewScence(jsonObject);
            progressDialog = new Dialog(getContext(),R.style.progress_dialog);
            progressDialog.setContentView(R.layout.waitting_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("模型加载中");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (progressDialog!=null&&progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    timer.cancel();
                }
            },0,3000);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_tree_structure, container, false);
        recyclerView = rootView.findViewById(R.id.container_tree_structure);

        {
            //Item初始化 mData&mData_rgb
            init();
            //添加manager&adapter
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new TreeStructureAdapter(mData);
            //添加操作，切换到module，长按切换到setting
            mAdapter.setTreeFragToModelFrag(() -> FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                    TreeFragment.class.getName(),ModelsFragment.class.getName()));

            mAdapter.setOnItemLongClickListener(() -> {
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                        TreeFragment.class.getName(), SettingFragment.class.getName());
                changeCurrentFragment.changeCurrentFragment(SettingFragment.class.getName());
            });
            //check操作监听
            mAdapter.setCheckListener(new CheckListener() {
                @Override
                public void onCheck(int groupId) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("operation_type",11);
                    jsonObject.put("groupId",groupId);
                    SocketIOManager.getInstance().getNewScence(jsonObject);
                }

                @Override
                public void onUncheck(int groupId) {
                    //TODO 反选操作待实现
                }

            });
            recyclerView.setAdapter(mAdapter);

            DefaultItemAnimator animator = new DefaultItemAnimator();
            //设置动画时间
            animator.setAddDuration(100);
            animator.setRemoveDuration(100);
            recyclerView.setItemAnimator(animator);

            setItemSpace(recyclerView,15,15,0,0);



        }
        return rootView;

    }

    //数据初始化
    public void init() {
        Node root = new Node(StaticVar.meshNum,-1,0,"Root",false);
        mData.add(root);
        Log.d("Node", "rootId:"+root.getId()+"rootPid:"+root.getPid()+"isExpanded:"+root.isExpanded());

    }

    //设置Item间距
    public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

        private HashMap<String, Integer> mSpaceValueMap;

        private static final String TOP_DECORATION = "top_decoration";
        private static final String BOTTOM_DECORATION = "bottom_decoration";
        private static final String LEFT_DECORATION = "left_decoration";
        private static final String RIGHT_DECORATION = "right_decoration";

        protected RecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
            this.mSpaceValueMap = mSpaceValueMap;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (mSpaceValueMap.get(TOP_DECORATION) != null)
                outRect.top = mSpaceValueMap.get(TOP_DECORATION);
            if (mSpaceValueMap.get(LEFT_DECORATION) != null)

                outRect.left = mSpaceValueMap.get(LEFT_DECORATION);
            if (mSpaceValueMap.get(RIGHT_DECORATION) != null)
                outRect.right = mSpaceValueMap.get(RIGHT_DECORATION);
            if (mSpaceValueMap.get(BOTTOM_DECORATION) != null)

                outRect.bottom = mSpaceValueMap.get(BOTTOM_DECORATION);

        }

    }

    //设置两个recyclerview的Item间距
    public void setItemSpace(RecyclerView recyclerView, int top, int bottom, int left, int right) {

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, top);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, bottom);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, left);
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, right);
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));

    }



}

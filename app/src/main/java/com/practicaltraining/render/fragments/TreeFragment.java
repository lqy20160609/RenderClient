package com.practicaltraining.render.fragments;

import android.app.Dialog;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.TreeStructureAdapter;
import com.practicaltraining.render.callbacks.CheckListener;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.objects.Node;
import com.practicaltraining.render.utils.LoadDataUtil;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.utils.TreeNodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TreeFragment extends FatherFragment {
    private List<Node> mData = new ArrayList<>();
    private Dialog progressDialog;
    private TreeStructureAdapter mAdapter;
    private String TAG = "TreeFragment";

    public void reset(ProgressDialog progressDialog) {
        mData.clear();
        init();
        mAdapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }
    //隐藏再次显现时调用，model页面返回数据后，实现添加操作，并完成数据传输
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && (ModelsFragment.meshCount != 0) && StaticVar.node != null) {
            SocketIOManager.getInstance().setCreatedModelFinished(() -> {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            });
            //获取所记录的node
            Node tempRoot = StaticVar.node;
            //获取从model中获取的model name
            String meshName = ModelsFragment.meshName;
            //id++
            StaticVar.meshNum++;
            //生成新的Node
            Node obj = new Node(StaticVar.meshNum, tempRoot.getId(), tempRoot.getLevel() + 1, meshName, tempRoot.isSelected());
            //obj 父节点链表初始化
            obj.getParentList().addAll(tempRoot.getParentList());
            obj.getParentList().add(0,tempRoot);
            //获取当前需要添加节点的位置
            int addposition = TreeNodeUtil.getLastAddPosition(mData, tempRoot);
            Log.d(TAG, "onHiddenChanged: "+obj.getParent().getId());

            //mData、tempRoot 数据更新
            mData.add(addposition, obj);
            TreeNodeUtil.getNode(mData,tempRoot).getChildren().add(obj);

            //如果当前节点处于未展开状态，完成添加操作，自动展开
            if (tempRoot.isParent_expanded()) {
                //设置当前节点不再是展开节点的父节点
                tempRoot.setParent_expanded(false);
                //设置所有子节点展开
                TreeNodeUtil.changeExpanded(mData, tempRoot, true);
            }
            //数据更新
            mAdapter.notifyDataSetChanged();

            //传输数据
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation_type", 0);
            jsonObject.put("parent", tempRoot.getId());
            jsonObject.put("son", obj.getId());
            jsonObject.put("meshId", Integer.parseInt(meshName));
            SocketIOManager.getInstance().getNewModelScence(jsonObject);
            progressDialog = new Dialog(getContext(), R.style.progress_dialog);

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
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    timer.cancel();
                    StaticVar.node = null;
                }
            }, 0, 3000);
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
        RecyclerView recyclerView = rootView.findViewById(R.id.container_tree_structure);
        Button clear = rootView.findViewById(R.id.select_clear);
        {

            init(); // 初始化不含Load数据
//            test1(); //关于Load的测试
            //添加manager&adapter
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new TreeStructureAdapter(mData);

            //添加操作，切换到module，长按切换到setting界面
            mAdapter.setTreeFragToModelFrag(() -> {
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                        TreeFragment.class.getName(), ModelsFragment.class.getName());
                changeCurrentFragment.changeCurrentFragment(ModelsFragment.class.getName());
            });

            mAdapter.setOnItemLongClickListener(() -> {
                FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                        TreeFragment.class.getName(), SettingFragment.class.getName());
                changeCurrentFragment.changeCurrentFragment(SettingFragment.class.getName());
            });



            //check操作监听、传输数据
            mAdapter.setCheckListener(new CheckListener() {
                @Override
                public void onCheck(int groupId) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("operation_type", 11);
                    jsonObject.put("groupId", groupId);
                    SocketIOManager.getInstance().getNewModelScence(jsonObject);

                }

                @Override
                public void onUncheck(int groupId) {

                }

            });
            recyclerView.setAdapter(mAdapter);
            //设置初始动画
            DefaultItemAnimator animator = new DefaultItemAnimator();
            //设置动画时间
            animator.setAddDuration(300);
            animator.setRemoveDuration(100);
            recyclerView.setItemAnimator(animator);


        }
        //清空所有选中状态
        clear.setOnClickListener(v -> {
            TreeNodeUtil.changAllSelectedState(mData, false);
            mAdapter.notifyDataSetChanged();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation_type", 12);
            jsonObject.put("groupId", 0);
            SocketIOManager.getInstance().getNewModelScence(jsonObject);
        });

        return rootView;

    }


    //设置Item间距
    public class RecyclerViewSpacesItemDecoration extends RecyclerView.ItemDecoration {

        private HashMap<String, Integer> mSpaceValueMap;

        private static final String TOP_DECORATION = "top_decoration";
        private static final String BOTTOM_DECORATION = "bottom_decoration";
        private static final String LEFT_DECORATION = "left_decoration";
        private static final String RIGHT_DECORATION = "right_decoration";

        RecyclerViewSpacesItemDecoration(HashMap<String, Integer> mSpaceValueMap) {
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

    //数据初始化
    public void init() {
        Node root = new Node(StaticVar.meshNum, -1, 0, "Root", false);
        mData.add(root);


    }

    // Load JSONArray数据测试
    public void test() {
        JSONArray jsonArrays = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("parent", 0);
        jsonObject.put("son", 1);
        jsonArrays.add(jsonObject);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("parent", 1);
        jsonObject1.put("son", 7);
        jsonArrays.add(jsonObject1);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("parent", 0);
        jsonObject2.put("son", 2);
        jsonArrays.add(jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("parent", 1);
        jsonObject3.put("son", 5);
        jsonArrays.add(jsonObject3);
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("parent", 2);
        jsonObject4.put("son", 4);
        jsonArrays.add(jsonObject4);
        Log.d("jsonarray:", "" + jsonArrays.toString());
        LoadDataUtil.loadData(mData,jsonArrays);
    }
    //Load 解析后的Node数据测试
    public void test1() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node(1, 0));
        nodes.add(new Node(2, 0));
        nodes.add(new Node(3, 2));
        nodes.add(new Node(5, 1));
        nodes.add(new Node(4, 1));
        nodes.add(new Node(8, 2));
        for (Node node : nodes) {
            Log.d("test1: ", node.getId() + "");
        }
        LoadDataUtil.loadNodeData(mData,nodes);
    }

}

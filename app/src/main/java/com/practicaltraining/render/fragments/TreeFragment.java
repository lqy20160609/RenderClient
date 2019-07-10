package com.practicaltraining.render.fragments;

import android.app.Dialog;

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
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.adapters.TreeStructureAdapter;
import com.practicaltraining.render.callbacks.CheckListener;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.objects.Node;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.utils.TreeNodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;


public class TreeFragment extends FatherFragment {
    private List<Node> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private Dialog progressDialog;
    private TreeStructureAdapter mAdapter;
    public String TAG = "jsonarray";

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && (ModelsFragment.meshCount != 0) && StaticVar.node != null) {
            SocketIOManager.getInstance().setCreatedModelFinished(() -> {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            });
            Node tempRoot = StaticVar.node;
            String meshName = ModelsFragment.meshName;
            StaticVar.meshNum++;
            Node obj = new Node(StaticVar.meshNum, tempRoot.getId(), tempRoot.getLevel() + 1, meshName, tempRoot.isSelected());
            mData.add(TreeNodeUtil.getLastAddPostion(mData, tempRoot), obj);
            tempRoot.getChildren().add(obj);

            if (tempRoot.isParent_expanded()) {
                tempRoot.setParent_expanded(false);
                TreeNodeUtil.changeExpanded(mData, tempRoot, true);
            }
//            mAdapter.notifyItemRangeChanged(holder.getAdapterPosition(), size);


            mAdapter.notifyDataSetChanged();
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
        recyclerView = rootView.findViewById(R.id.container_tree_structure);
        Button clear= rootView.findViewById(R.id.select_clear);

        {
            //Item初始化 mData&mData_rgb
            init();
//            test();
            //添加manager&adapter
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new TreeStructureAdapter(mData);
            //添加操作，切换到module，长按切换到setting
            mAdapter.setTreeFragToModelFrag(() -> FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                    TreeFragment.class.getName(), ModelsFragment.class.getName()));

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
                    jsonObject.put("operation_type", 11);
                    jsonObject.put("groupId", groupId);
                    SocketIOManager.getInstance().getNewModelScence(jsonObject);

                }

                @Override
                public void onUncheck(int groupId) {

                }

            });
            recyclerView.setAdapter(mAdapter);

            DefaultItemAnimator animator = new DefaultItemAnimator();
            //设置动画时间
            animator.setAddDuration(100);
            animator.setRemoveDuration(100);
            recyclerView.setItemAnimator(animator);


//            setItemSpace(recyclerView, 15, 15, 0, 0);


        }
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNodeUtil.changAllSelectedState(mData,false);
                mAdapter.notifyDataSetChanged();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("operation_type", 12);
                jsonObject.put("groupId", 0);
                SocketIOManager.getInstance().getNewModelScence(jsonObject);
            }
        });

        return rootView;

    }

    //数据初始化
    public void init() {
        Node root = new Node(StaticVar.meshNum, -1, 0, "Root", false);
        mData.add(root);

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

    //初始化
    public void init(JSONArray jsonArray) {
        //除了Root之外的所有节点
        List<Node> nodes = new ArrayList<>();
        List<Node> root = new ArrayList<>();
        //解析JSONArray 加载node数据
        try {

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    nodes.add(new Node(Integer.parseInt(jsonObject.getString("son")), Integer.parseInt(jsonObject.getString("parent"))));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }

        //排序
        nodes = BaseSort(nodes);

        for (Node n : nodes) {

            Log.d("jsonarray", "nodes after sort: " + n.getId());

        }
        //数据初始化
        TreeNodeUtil.initData(nodes);
        for (Node n : nodes) {

            Log.d("jsonarray", "nodes after initData: " + n.getId());

        }
        //添加到mData
        //添加ROOT
        root.add(new Node(StaticVar.meshNum, -1, 0, "Root", false));
        root.addAll(nodes);

        mData.addAll(root);


    }

    //基类排序
    public List<Node> BaseSort(List<Node> nodes) {
        List<Node> templist = new ArrayList<>();
        List<List<Node>> temp;
        Integer[] tempBaseBucket = getBaseBucket(nodes).toArray(new Integer[]{});
        //生成相应个数桶
        int[] baseBucket = new int[tempBaseBucket.length];
        for (int i = 0; i < tempBaseBucket.length; i++) {
            baseBucket[i] = tempBaseBucket[i].intValue();
        }
        temp = new ArrayList();
        for (int i = 0; i < baseBucket.length; i++) {
            Log.d("jsonarray", "BaseSort: " + baseBucket[i]);
            temp.add(new ArrayList<>());
        }


        //分到不同桶
        for (Node n : nodes) {

            Log.d("jsonarray", "temp Pid: " + n.getPid() + "id" + n.getId());
            temp.get(indexOfBaseBucket(baseBucket, n.getPid())).add(n);
//            Log.d("jsonarray", "temp:" +temp.get(indexOfBaseBucket(baseBucket, n.getPid())).toString());
        }
        //桶内排序
        for (List<Node> nodes1 : temp) {

            for (int i = 0; i < nodes1.size(); i++) {

            }
        }

        //合桶
        for (List<Node> n : temp) {
            templist.addAll(n);
        }


        StaticVar.meshNum = TreeNodeUtil.findMaxId(TreeNodeUtil.findLeafs(nodes)) + 1;

        return templist;

    }

    //基类篮子
    public TreeSet<Integer> getBaseBucket(List<Node> nodes) {

        TreeSet<Integer> treeSet = new TreeSet<>();

        for (Node n : nodes) {
            treeSet.add(n.getPid());
        }

        return treeSet;
    }

    //position
    public int indexOfBaseBucket(int[] baseBucket, int x) {
        for (int i = 0; i < baseBucket.length; i++) {
            if (x == baseBucket[i]) {
                Log.d("jsonarray", "indexOfBaseBucket: " + i);
                return i;
            }
        }
        return -1;

    }

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
        init(jsonArrays);
    }

    public List<Node> InsertSort(List<Node> nodes) {
        if (nodes.size() == 0) {
            return nodes;
        }
        Node current;
        for (int i = 0; i < nodes.size(); i++){
            current= nodes.get(i);
            int j =i;
//            while(current.getId()<){
//
//
//            }
        }
        return  nodes;
    }
}

package com.practicaltraining.render.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.R;
import com.practicaltraining.render.TreeView.holder.IconTreeItemHolder;
import com.practicaltraining.render.TreeView.model.TreeNode;
import com.practicaltraining.render.TreeView.view.AndroidTreeView;
import com.practicaltraining.render.core.FragmentSwitchManager;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.utils.StaticVar;

import java.util.List;

public class TreeFragment extends FatherFragment {
    private static String TAG="TreeFragment";
    private AndroidTreeView tView;
    private TreeNode root = TreeNode.root();
    private Dialog progressDialog;
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden&&(ModelsFragment.meshCount!=0)&&StaticVar.node!=null) {
            SocketIOManager.getInstance().setCreatedModelFinished(()->{
                progressDialog.dismiss();
            });
            TreeNode tempRoot = StaticVar.node;
            String meshName = ModelsFragment.meshName;
            TreeNode obj = new TreeNode(new IconTreeItemHolder.IconTreeItem(meshName));
            obj.setGroupId(StaticVar.meshNum);
            StaticVar.meshNum++;
            tView.addNode(tempRoot,obj);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operation_type", 0);
            jsonObject.put("parent", tempRoot.getGroupId());
            jsonObject.put("son", obj.getGroupId());
            jsonObject.put("meshId", meshName);
            //SocketIOManager.getInstance().sendParamWithBack(jsonObject);
//            progressDialog = new Dialog(getContext(),R.style.progress_dialog);
//            progressDialog.setContentView(R.layout.waitting_dialog);
//            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            TextView msg = progressDialog.findViewById(R.id.id_tv_loadingmsg);
//            msg.setText("模型加载中");
//            progressDialog.show();
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setCancelable(false);
        }
        tView.expandAll();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View treeView = inflater.inflate(R.layout.fragment_default, null, false);
        ViewGroup containerView = treeView.findViewById(R.id.container);

        IconTreeItemHolder.setTreeFragToModelFrag(() -> {
            FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                    TreeFragment.class.getName(), ModelsFragment.class.getName());
            changeCurrentFragment.changeCurrentFragment(ModelsFragment.class.getName());
        });

        root = TreeNode.root();


        TreeNode obj = new TreeNode(new IconTreeItemHolder.IconTreeItem("Root"));
        obj.setGroupId(StaticVar.meshNum);
        StaticVar.meshNum++;
        root.addChild(obj);
        tView = new AndroidTreeView(getActivity(), root);

        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);
        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);

            }
        }

        return treeView;
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = (node, value) -> {

    };
    //长按显示所点击的内容
    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = (node, value) -> {
        IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                TreeFragment.class.getName(), SettingFragment.class.getName());
        changeCurrentFragment.changeCurrentFragment(SettingFragment.class.getName());
        return true;
    };

    //储存信息
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

}

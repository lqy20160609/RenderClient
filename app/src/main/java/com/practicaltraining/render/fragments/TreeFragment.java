package com.practicaltraining.render.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.practicaltraining.render.R;
import com.practicaltraining.render.TreeView.holder.IconTreeItemHolder;
import com.practicaltraining.render.TreeView.model.TreeNode;
import com.practicaltraining.render.TreeView.view.AndroidTreeView;
import com.practicaltraining.render.core.FragmentSwitchManager;

public class TreeFragment extends FatherFragment {
    private AndroidTreeView tView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        View treeView = inflater.inflate(R.layout.fragment_default, null, false);
        ViewGroup containerView = treeView.findViewById(R.id.container);
        IconTreeItemHolder.setTreeFragToModelFrag(()->{
            FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                    TreeFragment.class.getName(),ModelsFragment.class.getName());
            changeCurrentFragment.changeCurrentFragment(ModelsFragment.class.getName());
        });
        TreeNode root = TreeNode.root();
        IconTreeItemHolder treeItemHolder = new IconTreeItemHolder(getContext());
        IconTreeItemHolder.IconTreeItem iconTreeItem = new IconTreeItemHolder.IconTreeItem("Root");
        TreeNode ObjRoot = new TreeNode(iconTreeItem);
        root.addChild(ObjRoot);
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
        //IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        //statusBar.setText("Last clicked: " + item.text);
    };
    //长按显示所点击的内容
    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = (node, value) -> {
        IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
        FragmentSwitchManager.getInstance().switchToNextFragmentByTag(getActivity().getSupportFragmentManager(),
                TreeFragment.class.getName(),SettingFragment.class.getName());
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

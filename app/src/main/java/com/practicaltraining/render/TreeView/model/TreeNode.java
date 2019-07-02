package com.practicaltraining.render.TreeView.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.practicaltraining.render.R;
import com.practicaltraining.render.TreeView.view.AndroidTreeView;
import com.practicaltraining.render.TreeView.view.TreeNodeWrapperView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeNode {
    public static final String NODES_ID_SEPARATOR = ":";

    private int mId;
    private int mLastId;
    private TreeNode mParent;
    private boolean mSelected;
    private boolean mSelectable = true;
    private final List<TreeNode> children;
    private BaseNodeViewHolder mViewHolder;
    private TreeNodeClickListener mClickListener;
    private TreeNodeLongClickListener mLongClickListener;
    private Object mValue;
    private boolean mExpanded;
    private int groupId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getmId() {
        return mId;
    }

    public static TreeNode root() {
        TreeNode root = new TreeNode(null);
        root.setSelectable(false);
        return root;
    }

    private int generateId() {
        return ++mLastId;
    }

    public TreeNode(Object value) {
        children = new ArrayList<>();
        mValue = value;
    }

    //添加单个节点
    public TreeNode addChild(TreeNode childNode) {
        childNode.mParent = this;
        childNode.mId = generateId();
        children.add(childNode);
        return this;
    }

    //添加多个子节点
    public TreeNode addChildren(TreeNode... nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public TreeNode addChildren(Collection<TreeNode> nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    //删除子节点
    public int deleteChild(TreeNode child) {
        for (int i = 0; i < children.size(); i++) {
            //遍历子节点并删除
            if (child.mId == children.get(i).mId) {
                children.remove(i);
                return i;
            }
        }
        return -1;
    }

    public List<TreeNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public int size() {
        return children.size();
    }

    //父节点
    public TreeNode getParent() {
        return mParent;
    }

    //当前id
    public int getId() {
        return mId;
    }

    //是否是叶子（最后一个节点）
    public boolean isLeaf() {
        return size() == 0;
    }

    public Object getValue() {
        return mValue;
    }

    //是否已展开
    public boolean isExpanded() {
        return mExpanded;
    }

    //设置展开
    public TreeNode setExpanded(boolean expanded) {
        mExpanded = expanded;
        return this;
    }

    //设置被选中
    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    //是否被选中
    public boolean isSelected() {
        return mSelectable && mSelected;
    }

    //设置可否被选中
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public boolean isSelectable() {
        return mSelectable;
    }

    //获取路径
    public String getPath() {
        final StringBuilder path = new StringBuilder();
        TreeNode node = this;
        while (node.mParent != null) {
            path.append(node.getId());
            node = node.mParent;
            if (node.mParent != null) {
                path.append(NODES_ID_SEPARATOR);
            }
        }
        return path.toString();
    }

    //获取当前层级
    public int getLevel() {
        int level = 0;
        TreeNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
            level++;
        }
        return level;
    }

    //是否为当前层级最后一个节点
    public boolean isLastChild() {
        if (!isRoot()) {
            int parentSize = mParent.children.size();
            if (parentSize > 0) {
                final List<TreeNode> parentChildren = mParent.children;
                return parentChildren.get(parentSize - 1).mId == mId;
            }
        }
        return false;
    }

    public TreeNode setViewHolder(BaseNodeViewHolder viewHolder) {
        mViewHolder = viewHolder;
        if (viewHolder != null) {
            viewHolder.mNode = this;
        }
        return this;
    }

    public TreeNode setClickListener(TreeNodeClickListener listener) {
        mClickListener = listener;
        return this;
    }

    public TreeNodeClickListener getClickListener() {
        return this.mClickListener;
    }

    public TreeNode setLongClickListener(TreeNodeLongClickListener listener) {
        mLongClickListener = listener;
        return this;
    }

    public TreeNodeLongClickListener getLongClickListener() {
        return mLongClickListener;
    }

    public BaseNodeViewHolder getViewHolder() {
        return mViewHolder;
    }

    public boolean isFirstChild() {
        if (!isRoot()) {
            List<TreeNode> parentChildren = mParent.children;
            return parentChildren.get(0).mId == mId;
        }
        return false;
    }

    //判断是否为根节点
    public boolean isRoot() {
        return mParent == null;
    }

    //获取根节点
    public TreeNode getRoot() {
        TreeNode root = this;
        while (root.mParent != null) {
            root = root.mParent;
        }
        return root;
    }

    //监听器接口
    public interface TreeNodeClickListener {
        void onClick(TreeNode node, Object value);
    }

    public interface TreeNodeLongClickListener {
        boolean onLongClick(TreeNode node, Object value);
    }

    //BaseNodeViewHolder类
    public static abstract class BaseNodeViewHolder<E> {
        protected AndroidTreeView tView;
        public TreeNode mNode;
        private View mView;
        protected int containerStyle;
        protected Context context;

        public BaseNodeViewHolder(Context context) {
            this.context = context;
        }

        public View getView() {
            if (mView != null) {
                return mView;
            }
            final View nodeView = getNodeView();
            final TreeNodeWrapperView nodeWrapperView = new TreeNodeWrapperView(nodeView.getContext(), getContainerStyle());
            nodeWrapperView.insertNodeView(nodeView);
            mView = nodeWrapperView;

            return mView;
        }

        public void setTreeViev(AndroidTreeView treeViev) {
            this.tView = treeViev;
        }

        public AndroidTreeView getTreeView() {
            return tView;
        }

        public void setContainerStyle(int style) {
            containerStyle = style;
        }

        public View getNodeView() {
            return createNodeView(mNode, (E) mNode.getValue());
        }

        public ViewGroup getNodeItemsView() {
            return (ViewGroup) getView().findViewById(R.id.node_items);
        }

        public boolean isInitialized() {
            return mView != null;
        }

        public int getContainerStyle() {
            return containerStyle;
        }


        public abstract View createNodeView(TreeNode node, E value);

        public void toggle(boolean active) {
            // empty
        }

        public void toggleSelectionMode(boolean editModeEnabled) {
            // empty
        }
    }


}

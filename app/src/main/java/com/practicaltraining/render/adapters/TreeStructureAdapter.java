package com.practicaltraining.render.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.alibaba.fastjson.JSONObject;
import com.github.johnkil.print.PrintView;
import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.CheckListener;
import com.practicaltraining.render.callbacks.OnItemLongClickListener;
import com.practicaltraining.render.callbacks.TreeFragToModelFrag;
import com.practicaltraining.render.core.SocketIOManager;
import com.practicaltraining.render.objects.Node;
import com.practicaltraining.render.utils.StaticVar;
import com.practicaltraining.render.utils.TreeNodeUtil;

import java.util.List;

public class TreeStructureAdapter extends RecyclerView.Adapter<TreeStructureAdapter.VH> {
    //数据
    private List<Node> mData;
    //接口
    private TreeFragToModelFrag treeFragToModelFrag;
    private CheckListener checkListener;
    private OnItemLongClickListener onItemLongClickListener;

    //初始化
    public TreeStructureAdapter(List<Node> mData) {
        this.mData = mData;
    }
    //接口
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    public void setTreeFragToModelFrag(TreeFragToModelFrag treeFragToModelFrag) {
        this.treeFragToModelFrag = treeFragToModelFrag;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tree_structure, parent, false);
        return new TreeStructureAdapter.VH(v);

    }


    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        final Node node = mData.get(holder.getAdapterPosition());
        //设置缩进
        holder.itemView.setPadding(80 * node.getLevel(), 0, 0, 0);
        //设置文本内容
        holder.tvValue.setText(node.getText());

        //设置checked状态
        holder.checkBox.setIconText(node.isSelected() ? R.string.ic_check_circle : R.string.ic_check_circle_outline_blank);
        holder.tvValue.setTextColor(node.isSelected() ? Color.RED : Color.GRAY);
        if (node.isParent_expanded()) {
            holder.arrowView.setIconText(R.string.ic_keyboard_arrow_right);
        } else {
            holder.arrowView.setIconText(R.string.ic_arrow_drop_down);
        }

        //设置展开
        if (!node.isExpanded()) {
            setVisibility(holder.itemView, false);
        } else {
            setVisibility(holder.itemView, true);

        }

        //点击展开
        holder.itemView.setOnClickListener(view -> {

            int size = TreeNodeUtil.getLastPosition(mData, node) - position + 1;
            if (node.isParent_expanded()) {
                node.setParent_expanded(false);
                TreeNodeUtil.changeExpanded(mData, node, true);
            } else {
                node.setParent_expanded(true);
                TreeNodeUtil.changeExpanded(mData, node, false);
            }

            notifyItemRangeChanged(holder.getAdapterPosition(), size);
        });

        //长按切换fragment
        holder.itemView.setOnLongClickListener(view -> {

            StaticVar.currentItemId = node.getId();
            onItemLongClickListener.setItemLongClickLinstener();
            return true;
        });


        //设置选中状态
        holder.checkBox.setOnClickListener(view -> {

            if (!node.isSelected()) {
                TreeNodeUtil.changeSelected(mData, node, true);
                checkListener.onCheck(node.getId());
            } else {
                TreeNodeUtil.changeSelected(mData, node, false);
                checkListener.onUncheck(node.getId());
            }
            notifyDataSetChanged();
        });

        //添加，切换页面，在fragment中完成添加
        holder.add.setOnClickListener(view -> {
            treeFragToModelFrag.switchToModel();
            StaticVar.node = node;
        });

        //删除
        holder.delete.setOnClickListener(view -> {
            int lastPosition = TreeNodeUtil.getLastPosition(mData, node);
            Log.d("lastPosition", ""+lastPosition);
            TreeNodeUtil.removeNode(mData, node);
            int sonId = node.getId();
            int parentId = node.getPid();
            JSONObject json = new JSONObject();
            json.put("operation_type",1);
            json.put("groupId",sonId);
            json.put("parent",parentId);
            SocketIOManager.getInstance().getNewModelScence(json);
            if (holder.getAdapterPosition() == 0) {
                notifyItemRangeRemoved(1, lastPosition);
            } else {
                int itemCount = lastPosition - holder.getAdapterPosition() + 1;
                Log.d("itemCount", ""+itemCount);
                notifyItemRangeRemoved(holder.getAdapterPosition(), itemCount);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //设置可见性
    public void setVisibility(View v, boolean isVisible) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) v.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        v.setLayoutParams(param);
    }


    public class VH extends RecyclerView.ViewHolder {
        TextView tvValue;
        PrintView arrowView;
        PrintView checkBox;
        PrintView add;
        PrintView delete;

        public VH(View v) {
            super(v);
            tvValue = itemView.findViewById(R.id.node_text);
            arrowView = itemView.findViewById(R.id.icon_arrow);
            checkBox = itemView.findViewById(R.id.node_checked);
            add = itemView.findViewById(R.id.node_add);
            delete = itemView.findViewById(R.id.node_delete);
        }

    }

}

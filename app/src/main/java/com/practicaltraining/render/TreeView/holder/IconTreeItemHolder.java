package com.practicaltraining.render.TreeView.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.practicaltraining.render.R;
import com.practicaltraining.render.TreeView.model.TreeNode;
import com.practicaltraining.render.callbacks.TreeFragToModelFrag;
import com.practicaltraining.render.utils.StaticVar;


public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    private static TreeFragToModelFrag treeFragToModelFrag;

    public static void setTreeFragToModelFrag(TreeFragToModelFrag s) {
        treeFragToModelFrag = s;
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        arrowView = view.findViewById(R.id.arrow_icon);


        view.findViewById(R.id.btn_addFolder).setOnClickListener(v -> {
            StaticVar.node = node;
            treeFragToModelFrag.switchToModel();
        });

        view.findViewById(R.id.btn_delete).setOnClickListener(v -> getTreeView().removeNode(node));

        if (node.getLevel() == 1) {
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public String text;


        public IconTreeItem(String text) {
            this.text = text;
        }

    }

}

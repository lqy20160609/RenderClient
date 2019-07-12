package com.practicaltraining.render.utils;

import android.graphics.Color;
import android.util.Log;

import com.practicaltraining.render.objects.Node;

import java.util.ArrayList;
import java.util.List;


public class TreeNodeUtil {
    private static String TAG = "TreeNodeUtil";


    //删除节点与其子节点
    public static void removeNode(List<Node> nodes, Node node) {
        //删除所有子节点
        removeAllChildNode(nodes, node);

        //删除当前节点
        Log.d(TAG, "removeNode: " + node.getId());
        if (node.getId() == 0) {
            node.getChildren().clear();
        } else {
            if (node.getParent() != null) {
                Node parent = node.getParent();
                node.getParentList().clear();
                parent.getChildren().remove(getNodePosition(parent.getChildren(), node));
                nodes.remove(node);

            } else {
                Log.d(TAG, "remove current node， parent of current node not found");
            }
        }
        for (Node n : nodes) {
            Log.d(TAG, "CurrentNodeList: " + n.getId());
        }

    }

    //通过nodeId 来获取nodes中的node
    public static Node getNode(List<Node> nodes, Node node) {

        for (Node n : nodes) {
            if (n.getId() == node.getId()) {
                return n;
            }
        }
        return node;
    }

    ////通过nodeId 来获取nodes中的node的位置
    public static int getNodePosition(List<Node> nodes, Node node) {

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getId() == node.getId()) {
                return i;
            }

        }
        return -1;
    }

    //删除指定节点的子节点
    private static void removeAllChildNode(List<Node> nodes, Node node) {
        if (node.getChildren().isEmpty()) {
            return;
        }
        for (Node n : node.getChildren()) {
            removeAllChildNode(nodes, n);
            nodes.remove(n);
        }
    }

    //设置选中状态
    public static void changeSelected(List<Node> nodes, Node node, boolean selected) {
        if (node.isSelected()) {
            return;
        }
        //清空所有选中状态
        changAllSelectedState(nodes, false);
        //设置所有子节点选中状态
        changeChildSelectedState(node, true);
        //设置当前节点选中状态
        node.setSelected(selected);
        node.setColor(selected ? Color.RED : Color.GRAY);

    }

    //清空选中状态
    public static void changAllSelectedState(List<Node> nodes, boolean selected) {
        for (Node n : nodes) {
            if (n.isSelected()) {
                n.setSelected(selected);
                n.setColor(Color.GRAY);
            }
        }
    }

    //单选时 同层 其他设置为未选中
    public static void changeSameLevelSelectedState(Node node) {
        if (node.getId() == 0) {
            return;
        }

        Node parent = node.getParent();

        if (parent == null) throw new AssertionError();
        for (Node child : parent.getChildren()) {
            if (child.getId() != node.getId()) {
                changeChildSelectedState(child, false);
                child.setSelected(false);
                child.setColor(Color.GRAY);
            }
        }

    }

    //设置子节点选中状态
    private static void changeChildSelectedState(Node node, boolean selected) {

        //子节点
        for (Node child : node.getChildren()) {
            child.setSelected(selected);
            child.setColor(selected ? Color.RED : Color.GRAY);
            if (child.getChildren().isEmpty()) {
                continue;
            }
            changeChildSelectedState(child, selected);
        }

    }

    //设置父节点选中状态
    public static void changeParentSelectedState(Node node, boolean selected) {

        changeParentSelected(node, selected);

    }

    //父节点选中状态
    private static void changeParentSelected(Node node, boolean selected) {
        Node parent = node;
        while ((parent = parent.getParent()) != null) {
            parent.setSelected(false);
            parent.setColor(Color.GRAY);
        }
    }


    //是否子节点全部选中
    public static boolean isChildrenSelected(Node node) {

        for (Node child : node.getChildren()) {
            if (!child.isSelected()) {
                return false;
            }
        }
        return true;
    }

    //设置展开状态 递归
    public static void changeExpanded(List<Node> nodes, Node node, boolean expanded) {
        for (Node n : nodes) {
            if (n.getPid() == node.getId()) {
                n.setExpanded(expanded);
                changeExpanded(nodes, n, expanded);

            }
        }
    }

    //获得所有叶子节点
    public static List<Node> findLeafs(List<Node> nodes) {
        List<Node> leafs = new ArrayList<>();
        if (nodes == null || nodes.size() == 0) {
            return leafs;
        }
        for (Node node : nodes) {
            if (node.getChildren().isEmpty()) {
                leafs.add(node);
            }
        }
        return leafs;
    }

    //添加节点的位置
    public static int getLastAddPosition(List<Node> nodes, Node node) {
        int last_position = getLastPosition(nodes, node) + 1;

        Log.d(TAG, "getLastAddPosition: " + last_position);

        return last_position;
    }

    //获得选中节点的最后一个叶子节点位置
    public static int getLastPosition(List<Node> nodes, Node node) {

//        return nodes.indexOf(getLastNode(node));


        Node lastNode = getLastNode(node);

        for (int i = 0; i < nodes.size(); i++) {

            if (lastNode.getId() == nodes.get(i).getId()) {
                return i;
            }
        }
        Log.d(TAG, "getLastPosition: lastNode not found" );
        return -1;
    }

    //获得选中节点的最后一个叶子节点
    private static Node getLastNode(Node node) {
        Node lastNode = null;
        lastNode = node;

        while (!lastNode.getChildren().isEmpty()) {
            lastNode = lastNode.getChildren().get(lastNode.getChildren().size() - 1);
        }

        Log.d(TAG, "getLastNode: " + lastNode.getId());
        return lastNode;

    }

    public static int findMaxId(List<Node> nodes) {
        int maxId = nodes.get(0).getId();

        for (int i = 1; i < nodes.size(); i++) {
            if (maxId < nodes.get(i).getId()) {
                maxId = nodes.get(i).getId();
            }
        }
        Log.d(TAG, "MaxId: " + maxId);
        return maxId;
    }


}

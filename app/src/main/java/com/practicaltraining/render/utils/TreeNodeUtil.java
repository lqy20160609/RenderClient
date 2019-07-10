package com.practicaltraining.render.utils;

import android.graphics.Color;
import android.util.Log;


import com.practicaltraining.render.objects.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;


public class TreeNodeUtil {
    public static String TAG = "TreeNodeUtil";

    public static Node getParent(List<Node> nodes, Node node) {
//        if (node.getId() == 0)
//            return nodes.get(0);
        for (Node n : nodes) {
            if (n.getId() == node.getPid()) {
                return n;
            }
        }
        Log.d("Node", "getParent: Not Found ");
        return null;
    }
//    public static List removeNodePosition(List<Node> nodes,Node node){
//
//        String TAG = "Debug";
//        List removeList = new ArrayList();
//
//        removeChildPosition(nodes,node,removeList);
//        if(node.getId()==0){
//            node.getChildren().clear();
//
//        }else{
//            getParent(nodes, node).getChildren().remove(node);
//            removeList.add(nodes.indexOf(node));
//            nodes.remove(node);
//        }
//        Collections.sort(removeList,Collections.reverseOrder());
//        Log.d(TAG, "removeNodePosition: "+removeList.toString());
//        return removeList;
//    }
//
//    public static void removeChildPosition(List<Node> nodes,Node node,List removeList){
//
//        if(node.getChildren()==null){
//            return;
//        }else{
//            for(Node n :node.getChildren()){
//                removeChildPosition(nodes,n,removeList);
//                removeList.add(nodes.indexOf(n));
//                nodes.remove(n);
//            }
//        }
//    }

    public static void removeNode(List<Node> nodes, Node node) {
        removeAllChildNode(nodes, node);

        if (node.getId() == 0) {
            node.getChildren().clear();
            return;
        } else {
            Node node_parent = getParent(nodes, node);
            if (node_parent != null) {
                getParent(nodes, node).getChildren().remove(node);
                nodes.remove(node);
            }
        }

    }

    public static void removeAllChildNode(List<Node> nodes, Node node) {
        if (node.getChildren() == null) {
            return;
        }
        for (Node n : node.getChildren()) {
            removeAllChildNode(nodes, n);
            nodes.remove(n);
        }
    }

    public static void changeSelected(List<Node> nodes, Node node, boolean selected) {
        if (node.isSelected()) {
            return;
        }
        changAllSelectedState(nodes, false);

//        changeSameLevelSelectedState(nodes,node);

        changeChildSelectedState(node, true);

//        changeParentSelectedState(nodes, node, selected);
        node.setSelected(selected);
        node.setColor(selected ? Color.RED : Color.GRAY);

    }

    public static void changAllSelectedState(List<Node> nodes, boolean selected) {
        for (Node n : nodes) {
            n.setSelected(false);
            n.setColor(Color.GRAY);
        }

    }

    public static void changeSameLevelSelectedState(List<Node> nodes, Node node) {
        if (node.getId() == 0) {
            return;
        }

        Node parent = getParent(nodes, node);

        for (Node child : parent.getChildren()) {
            if (child.getId() != node.getId()) {
                changeChildSelectedState(child, false);
                child.setSelected(false);
                child.setColor(Color.GRAY);
            }
        }

    }

    public static void changeChildSelectedState(Node node, boolean selected) {

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

    public static void changeParentSelectedState(List<Node> nodes, Node node, boolean selected) {

        changeParentSelected(nodes, node, selected);

    }

    public static void changeParentSelected(List<Node> nodes, Node node, boolean selected) {
        Node parent = node;
        while ((parent = getParent(nodes, parent)) != null) {
            parent.setSelected(false);
            parent.setColor(Color.GRAY);
        }
    }

    public static void changeExpanded(List<Node> nodes, Node node, boolean expanded) {
        for (Node n : nodes) {
            if (n.getPid() == node.getId()) {
                n.setExpanded(expanded);
                changeExpanded(nodes, n, expanded);

            }
        }
    }


    public static boolean isChildrenSelected(Node node) {

        for (Node child : node.getChildren()) {
            if (!child.isSelected()) {
                return false;
            }
        }
        return true;
    }

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

    public static void showNodes() {

    }

    public static Node getNode(List<Node> nodes, int id) {
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

//    public static Node getLastNodeId(Node node) {
//        Node lastNode = node;
//        while (!lastNode.getChildren().isEmpty()) {
//            lastNode = lastNode.getChildren().get(lastNode.getChildren().size() - 1);
//        }
//        return lastNode;
//    }

    public static int getLastAddPostion(List<Node> nodes, Node node) {
        int lastposition = nodes.indexOf(getLastNode(node)) + 1;
        Log.d("Debug", "getLastAddPostion: " + lastposition);
        return lastposition;


    }

    public static int getLastPosition(List<Node> nodes, Node node) {

        return nodes.indexOf(getLastNode(node));
    }

    public static Node getLastNode(Node node) {
        Node lastNode = node;
        while (!lastNode.getChildren().isEmpty()) {
            lastNode = lastNode.getChildren().get(lastNode.getChildren().size() - 1);
        }
        return lastNode;

    }

    //初始化数据
    public static void initData(List<Node> nodes) {

        for (Node n : nodes) {
            n.setLevel(getLevel(nodes, n.getPid()));
            n.setChildren(initChildNode(nodes, n.getId()));
            if (n.getText()==null) {
                n.setText("New Node" + n.getId());
            }
        }
        for(Node n:nodes){
            Log.d(TAG, "initData: setLevel "+ n.getLevel());
        }

    }

    //初始化当前节点Level
    public static int getLevel(List<Node> nodes, int pid) {
        if(pid==-1){
            return 0;
        }
        int level = 0;
        while (pid >= 0) {
            level++;
            pid = getParentId(nodes,pid);
        }

        return level;


    }

    //获取父节点ID
    public static int getParentId(List<Node> nodes, int id) {

        for (Node n : nodes) {
            if (n.getId() == id) {
                return n.getPid();
            }
        }

        return 0;
    }

    //对children初始化
    public static List<Node> initChildNode(List<Node> nodes, int id) {
        List<Node> childNode = new ArrayList<>();
        for (Node n : nodes) {
            if (n.getPid() == id) {
                childNode.add(n);
            }
        }

        return childNode;

    }

    //对Root初始化
    public static List<Node> initRootNode(List<Node> nodes) {
        List<Node> childNode = new ArrayList<>();
        for (Node n : nodes) {
            if (n.getPid() == 0) {
                childNode.add(n);
            }
        }

        return childNode;

    }

    //读取JSONArray最大ID
    public static int findMaxId(List<Node> nodes) {
        int maxId = nodes.get(0).getId();

        for (int i = 1; i < nodes.size(); i++) {
            if (maxId < nodes.get(i).getId()) {
                maxId = nodes.get(i).getId();
            }
        }

        return maxId;
    }

    public static void getMergeList(List<List<Node>> list, List<Node> nodes, int[] parent_list) {

        if (list.isEmpty() || list.get(0).isEmpty() || list.get(0).get(0) == null || list.get(0).get(0).getPid() != 0) {
            Log.d(TAG, "getMergeList: data of getMergeList is empty");
            return;
        }
        getBaseMergeList(list, nodes, parent_list, list.get(0));
    }

    public static void getBaseMergeList(List<List<Node>> list, List<Node> nodes, int[] parent_list, List<Node> temp) {

        for (Node node : temp) {
            nodes.add(node);
            if (hasChildren(parent_list, node.getId())) {
                int indexOfList = findChildrenList(node.getId(), parent_list);
                Log.d(TAG, "indexOfList :" + indexOfList);
                getBaseMergeList(list, nodes, parent_list, list.get(indexOfList));
            }
        }

    }

    public static boolean hasChildren(int[] parent_list, int id) {
        for (int n : parent_list) {
            if (id == n) {
                return true;
            }
        }
        return false;
    }

    public static int findChildrenList(int id, int[] parent_list) {
        if (parent_list.length == 0 || id < 0) {
            return -1;
        }
        for (int i = 0; i < parent_list.length; i++) {
            if (parent_list[i] == id) {
                return i;
            }
        }
        Log.d("TreeNodeUtil", "findChildrenList: Not Found");
        return -1;
    }
    public static List getParentIdList(List<Node> nodes){
        TreeSet<Integer> treeSet = new TreeSet<>();

        for (Node n : nodes) {
            treeSet.add(n.getPid());
        }

        Integer[] tempBaseBucket = treeSet.toArray(new Integer[]{});

        List parent_id = new ArrayList();
        parent_id.addAll(Arrays.asList(tempBaseBucket));
        Log.d(TAG, "getParentIdList: "+parent_id.toString());
        return parent_id;
    }
}

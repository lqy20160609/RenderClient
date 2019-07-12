package com.practicaltraining.render.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.practicaltraining.render.objects.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class LoadDataUtil {
    private static String TAG = "LoadDataUtil";

    //初始化
    public static void loadData(List<Node> mData , JSONArray jsonArray) {
      loadNodeData(mData,analysisJsonArray(jsonArray));
    }

    public static void loadNodeData(List<Node> mData ,List <Node> nodes) {

        //除了Root之外的所有节点
        //桶排序
        nodes = LoadDataUtil.BucketSort(nodes);
        for (Node node : nodes) {
            Log.d("BucketSort: ", node.getId() + "");
        }
        //添加Root
        Node root = new Node(0, -1, 0, "Root", false);
        nodes.add(0, root);
        //数据初始化
        initData(nodes);

        //数据存储到客户端
        mData.addAll(nodes);
        StaticVar.meshNum = LoadDataUtil.findMaxId(TreeNodeUtil.findLeafs(mData)) + 1;
        Log.d("meshNum: ", String.valueOf(StaticVar.meshNum));
    }

    public  static List<Node> analysisJsonArray(JSONArray jsonArray) {
        //除了Root之外的所有节点
        List<Node> nodes = new ArrayList<>();
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
        return nodes;
    }


    //桶排序
    public static List<Node> BucketSort(List<Node> nodes) {
        /*
        list: 待返回数据列表
        temp: 临时数据列表
         */
        List<Node> list = new ArrayList<>();
        List<List<Node>> temp = new ArrayList<>();


        //根据pid生成parent id数组
        Integer[] tempBaseBucket = getBucket(nodes).toArray(new Integer[]{});
        int[] baseBucket = new int[tempBaseBucket.length];
        for (int i = 0; i < tempBaseBucket.length; i++) {
            baseBucket[i] = tempBaseBucket[i];
        }

        for (int n : baseBucket) {
            Log.d(TAG, String.valueOf(n));
        }

        //桶初始化
        for (int i = 0; i < baseBucket.length; i++) {
            temp.add(new ArrayList<>());
        }

        //分到不同桶
        for (Node n : nodes) {
            temp.get(indexOfBaseBucket(baseBucket, n.getPid())).add(n);
        }

        //桶内排序
        for (int i = 0; i < temp.size(); i++) {
            InsertSort(temp.get(i));
        }
        //合桶
        LoadDataUtil.getMergeList(temp, list, baseBucket);

        return list;

    }

    //桶
    private static TreeSet<Integer> getBucket(List<Node> nodes) {

        TreeSet<Integer> treeSet = new TreeSet<>();
        for (Node n : nodes) {
            treeSet.add(n.getPid());
            Log.d("getBucket: ", "getBucket: " + n.getPid());
        }


        return treeSet;
    }

    //position
    private static int indexOfBaseBucket(int[] baseBucket, int x) {
        for (int i = 0; i < baseBucket.length; i++) {
            if (x == baseBucket[i]) {
                return i;
            }
        }
        return -1;

    }

    //插入排序
    private static void InsertSort(List<Node> nodes) {
        if (nodes.size() == 0) {
            return;
        }
        Node current;
        for (int i = 0; i < nodes.size() - 1; i++) {
            current = nodes.get(i + 1);
            int j = i;
            while (j >= 0 && current.getId() < nodes.get(j).getId()) {
                nodes.set(j + 1, nodes.get(j));
                j--;
            }
            nodes.set(j + 1, current);
        }
    }


    //合并数据list
    private static void getMergeList(List<List<Node>> list, List<Node> nodes, int[] parent_list) {
        //判断当前数据是否合法
        if (list.isEmpty() || list.get(0).isEmpty() || list.get(0).get(0) == null || list.get(0).get(0).getPid() != 0) {
            Log.d(TAG, "getMergeList: data of getMergeList is empty");
            return;
        }
        //合并数据list
        getBaseMergeList(list, nodes, parent_list, list.get(0));
    }
    //合并数据list 递归
    /*
    list: 初始数据 List<List<Node>>
    nodes：归并后的数据
    parent_list: 桶的数据，有序存放的pid数组
    temp：临时数据 代替 list每个节点 进入递归
     */

    private static void getBaseMergeList(List<List<Node>> list, List<Node> nodes, int[] parent_list, List<Node> temp) {

        for (Node node : temp) {
            nodes.add(node);
            if (hasChildren(parent_list, node.getId())) {
                int indexOfList = findChildrenList(node.getId(), parent_list);

                Log.d(TAG, "indexOfList :" + indexOfList);

                getBaseMergeList(list, nodes, parent_list, list.get(indexOfList));
            }
        }

    }
    // 是否有子节点
    private static boolean hasChildren(int[] parent_list, int id) {
        for (int n : parent_list) {
            if (id == n) {
                return true;
            }
        }
        return false;
    }
    //通过当前节点ID，找到子节点对应的list中的节点链表
    private static int findChildrenList(int id, int[] parent_list) {
        if (parent_list.length == 0 || id < 0) {
            return -1;
        }
        for (int i = 0; i < parent_list.length; i++) {
            if (parent_list[i] == id) {
                return i;
            }
        }
        Log.d(TAG, "findChildrenList: Not Found");
        return -1;
    }

    public static List<Integer> getParentIdList(List<Node> nodes) {
        TreeSet<Integer> treeSet = new TreeSet<>();

        for (Node n : nodes) {
            treeSet.add(n.getPid());
        }

        Integer[] tempBaseBucket = treeSet.toArray(new Integer[]{});

        List<Integer> parent_id = new ArrayList<>(Arrays.asList(tempBaseBucket));
        Log.d(TAG, "getParentIdList: " + parent_id.toString());
        return parent_id;
    }

    /*
    初始化Load 生成的Nodes数据
    setParentList: 初始化当前节点的父节点链表
    setLevel: 初始化当前节点的Level
    setChildren: 初始化当前节点的子节点链表
    setText: 当前内容初始化(需要与后台沟通 更新方法)
     */
    public static void initData(List<Node> nodes) {

        for (Node n : nodes) {
            n.setParentList(initParentList(nodes, n));
            n.setLevel(getLevel(n));
            n.setChildren(initChildNode(nodes, n.getId()));
            if (n.getText() == null) {
                n.setText("New Node" + n.getId());
            }
        }


    }
    //对parent_list初始化
    //因为父节点Id 小于当前节点Id 直接反向遍历找父节点
    private static List<Node> initParentList(List<Node> nodes, Node node) {
        List<Node> parent_list = new ArrayList<>();
        int id = node.getPid();
        for (int i = nodes.indexOf(node); i >= 0; i--) {
            if (id == nodes.get(i).getId()) {
                parent_list.add(nodes.get(i));
                id = nodes.get(i).getPid();
            }
        }
        return parent_list;
    }

    //初始化当前节点Level，根据父节点个数获取，初始Root Level为0
    private static int getLevel(Node node) {
        int level =node.getParentList().size();
        Log.d(TAG, "initData: setLevel " +level);
        return level;

    }

    //对children初始化
    private static List<Node> initChildNode(List<Node> nodes, int id) {
        List<Node> childNode = new ArrayList<>();
        for (Node n : nodes) {
            if (n.getPid() == id) {
                childNode.add(n);
            }
        }

        return childNode;

    }


    //读取load数据中最大Id
    //也可以通过先初始化数据后，获取所有叶子饥节点，在叶子节点内得到最大值Id，为所求最大值Id

    public static int findMaxId(List<Node> nodes) {
        int maxId = nodes.get(0).getId();

        for (int i = 1; i < nodes.size(); i++) {
            if (maxId < nodes.get(i).getId()) {
                maxId = nodes.get(i).getId();
            }
        }
        return maxId;
    }


}

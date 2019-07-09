package com.practicaltraining.render.core;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Node {
    /*
    id: 唯一标识
    pid: 父节点
    children:子节点
    level: 当前层数
    text: 内容
    selected： 是否是选中状态
    expanded： 是否是展开状态

    */
    private int id;
    private int pid;
    private List<Node> children;
    private int level;
    private String text;
    private boolean selected;
    private boolean expanded;
    private int color;
    private boolean parent_expanded;
    public int getColor() {
        return color;
    }

    public boolean isParent_expanded() {
        return parent_expanded;
    }

    public void setParent_expanded(boolean parent_expanded) {
        this.parent_expanded = parent_expanded;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Node(int id, int pid, int level, String text, boolean selected) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.text = text;
        this.children = new ArrayList<>();
        this.selected =selected;
        this.expanded = true;
        this.color = Color.GRAY;
        this.parent_expanded = false;


    }
    public Node(int id,int pid){
        this.id = id;
        this.pid = pid;
//        this.level = level;
//        this.text = text;
        this.children = new ArrayList<>();
        this.selected =false;
        this.expanded = true;
        this.color = Color.GRAY;
        this.parent_expanded = false;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    public void addNode(Node node) {
        this.children.add(node);
    }


}

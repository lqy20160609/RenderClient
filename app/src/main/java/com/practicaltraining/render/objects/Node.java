package com.practicaltraining.render.objects;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Node {
    /*
    id: 唯一标识
    pid: 父节点Id
    children:子节点链表
    parent_list：父节点链表
    level: 当前层数
    text: 内容
    selected： 是否是选中状态
    expanded： 是否是展开状态

    */
    private int id;
    private int pid;
    private List<Node> children;
    private List<Node> parent_list;
    private int level;
    private String text;
    private boolean selected;
    private boolean expanded;
    private int color;
    private boolean parent_expanded;


    public Node(int id, int pid) {
        this.id = id;
        this.pid = pid;
        this.parent_list = new ArrayList<>();
        this.children = new ArrayList<>();
        this.selected = false;
        this.expanded = true;
        this.color = Color.GRAY;
        this.parent_expanded = false;
    }

    public Node(int id, int pid, int level, String text, boolean selected) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.text = text;
        this.parent_list = new ArrayList<>();
        this.children = new ArrayList<>();
        this.selected = selected;
        this.expanded = true;
        this.color = Color.GRAY;
        this.parent_expanded = false;
    }

    //废弃，parent_list没有初始化，容易出Bug
    public Node(int id, int pid, int level, List<Node> parent_list, String text, boolean selected) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.text = text;
        this.parent_list = parent_list;
        this.children = new ArrayList<>();
        this.selected = selected;
        this.expanded = true;
        this.color = Color.GRAY;
        this.parent_expanded = false;


    }

    public boolean isParent_expanded() {
        return parent_expanded;
    }

    public void setParent_expanded(boolean parent_expanded) {
        this.parent_expanded = parent_expanded;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Node> getParentList() {
        return parent_list;
    }

    public void setParentList(List<Node> parent) {
        this.parent_list = parent;
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

    public Node getParent() {

        return this.getParentList() != null ? this.getParentList().get(0) : null;
    }



}

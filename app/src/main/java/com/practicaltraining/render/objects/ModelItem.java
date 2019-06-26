package com.practicaltraining.render.objects;

public class ModelItem {
    //规定Resource类用于Arraylist
    private String name;
    private int id;
    public ModelItem(String name, int id){
        this.name=name;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

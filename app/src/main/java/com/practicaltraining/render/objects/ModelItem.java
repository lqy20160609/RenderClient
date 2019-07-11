package com.practicaltraining.render.objects;

public class ModelItem {
    //规定Resource类用于Arraylist
    private String name;
    private int id;
    private String meshName;
    public ModelItem(String name, int id,String meshName){
        this.name=name;
        this.id=id;
        this.meshName = meshName;
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
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

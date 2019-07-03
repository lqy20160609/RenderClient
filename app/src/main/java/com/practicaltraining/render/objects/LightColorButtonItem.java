package com.practicaltraining.render.objects;


public class LightColorButtonItem {
    private int color;
    private boolean checked;

    public LightColorButtonItem(int color, boolean checked){
        this.color = color;
        this.checked = checked;
    }
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

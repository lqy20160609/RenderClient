package com.practicaltraining.render.objects;


public class LightColorRGBItem {
    private String RGB;
    private int progress;
    public LightColorRGBItem(String RGB, int progress){
        this.RGB = RGB;
        this.progress = progress;
    }
    public String getRGB() {
        return RGB;
    }

    public void setRGB(String RGB) {
        this.RGB = RGB;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }



}

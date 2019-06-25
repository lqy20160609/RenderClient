package com.practicaltraining.render.objects;

public class SettingItem {
    private int iconId;
    private String description;

    public SettingItem(int iconId, String description) {
        this.iconId = iconId;
        this.description = description;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

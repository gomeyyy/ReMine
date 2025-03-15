package com.razinrahimi.remine.data;

public class ChecklistItem {
    private String itemName;
    private boolean isDone;

    public ChecklistItem(String itemName) {
        this.itemName = itemName;
        this.isDone = false;
    }

    public String getItemName() { return itemName; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { this.isDone = done; }
}


package com.razinrahimi.remine.data;

import java.util.ArrayList;
import java.util.List;

public class Notes {
    private String noteId;
    private String title;
    private String content;
    private List<ChecklistItem> checklist;
    private String taskId; // Nullable: If null, note is independent

    public Notes(String noteId, String title, String content) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.checklist = new ArrayList<>();
        this.taskId = null; // Default: No task linked
    }

    public String getNoteId() { return noteId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTaskId() { return taskId; } // Nullable getter

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void addChecklistItem(String item) {
        checklist.add(new ChecklistItem(item));
    }

    public void markItemAsDone(int index) {
        if (index >= 0 && index < checklist.size()) {
            checklist.get(index).setDone(true);
        }
    }

    public void displayNote() {
        System.out.println("Title: " + title);
        System.out.println("Content: " + content);
        System.out.println("Linked to Task ID: " + (taskId != null ? taskId : "None"));
        System.out.println("Checklist:");
        for (ChecklistItem item : checklist) {
            System.out.println("- " + item.getItemName() + " [" + (item.isDone() ? "✔" : "✘") + "]");
        }
    }
}



package com.razinrahimi.remine.data;

import java.util.UUID;

public abstract class Task {
    protected String taskId;
    protected String title;
    protected String notes;
    protected String dueDate;
    protected TaskPriority priority;
    protected TaskStatus status;

    //Firestore needs a no-arg constructor
    public Task() {}

    public Task(String title, String notes, String dueDate, TaskPriority priority) {
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public String getDueDate() { return dueDate; }
    public TaskPriority getPriority() { return priority; }

    public void setTaskId(String taskId) { this.taskId = taskId; }
    public void setTitle(String title) { this.title = title; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public abstract String getTaskType();
}


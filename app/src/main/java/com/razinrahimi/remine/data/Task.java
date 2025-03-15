package com.razinrahimi.remine.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Task {
    protected int taskId;
    protected String title;
    protected String description;
    protected LocalDateTime dueDate;
    protected TaskPriority priority;
    protected TaskStatus status;
    protected List<Notes> notesList;

    public Task(int taskId, String title, String description, LocalDateTime dueDate, TaskPriority priority) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = TaskStatus.PENDING;
        this.notesList = new ArrayList<>();
    }

    // Abstract method to be implemented in subclasses
    public abstract void displayTaskDetails();

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        System.out.println(title + " marked as completed!");
    }

    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void markCompleted() { this.status = TaskStatus.COMPLETED; }
    public boolean isOverdue() {
        if (LocalDateTime.now().isAfter(dueDate)) {
            this.status = TaskStatus.OVERDUE; // Update status if overdue
            return true;
        }
        return false;
    }

    public void attachNote(Notes note) {
        note.setTaskId(this.taskId);
        notesList.add(note);
    }

    // Detach a note from this task
    public void detachNote(Notes note) {
        notesList.remove(note);
        note.setTaskId(null); // Convert to an independent note
    }

    public void displayNotes() {
        System.out.println("Notes for task: " + title);
        for (Notes note : notesList) {
            note.displayNote();
        }
    }

    public void updateTask(String newTitle, String newDescription, TaskPriority newPriority) {
        this.title = newTitle;
        this.description = newDescription;
        this.priority = newPriority;
    }
}

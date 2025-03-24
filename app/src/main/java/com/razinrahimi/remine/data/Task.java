package com.razinrahimi.remine.data;

import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

//Base class for tasks (Personal, Work, Health)
public abstract class Task {
    protected String taskId; //Unique for each tasks
    protected String userId; //Unique for each user
    protected String title;
    protected String notes;
    protected String dueDate;
    protected TaskPriority priority;
    protected TaskStatus status;

    // Firestore needs a no-arg constructor
    public Task() {}

    //Constructor
    public Task(String title, String notes, String dueDate, TaskPriority priority) {
        this.taskId = UUID.randomUUID().toString(); //Create Unique Task ID using UUID class from java.util package
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Retrieve existing User ID from firebase authentication
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = TaskStatus.PENDING; //Initialise every task as pending on create
    }

    //Firestore needs getter and setter for every attributes
    public String getUserId() {
        return userId;
    }

    public String getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public String getDueDate() { return dueDate; }
    public TaskPriority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }

    public void setTaskId(String taskId) { this.taskId = taskId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setStatus(TaskStatus status) { this.status = status;}

    //abstract method to be overridden by children classes
    public abstract String getTaskType();
    public abstract String getLocation();

}

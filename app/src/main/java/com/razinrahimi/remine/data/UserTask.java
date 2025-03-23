package com.razinrahimi.remine.data;

public class UserTask extends Task {

    private String location;
    // Firestore requires an empty constructor
    public UserTask() {}

    public UserTask(String title, String notes, String dueDate, TaskPriority priority) {
        super(title, notes, dueDate, priority);
        this.location = "";
    }

    @Override
    public String getTaskType() {
        return "UserTask";
    }
    @Override
    public String getLocation() {return location; }
}


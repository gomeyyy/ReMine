package com.razinrahimi.remine.data;

public class UserTask extends Task {

    // Firestore requires an empty constructor
    public UserTask() {}

    public UserTask(String title, String notes, String dueDate, TaskPriority priority) {
        super(title, notes, dueDate, priority);
    }

    @Override
    public String getTaskType() {
        return "UserTask";
    }
}


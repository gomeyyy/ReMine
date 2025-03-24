package com.razinrahimi.remine.data;

public class PersonalTask extends Task {
    private String personalLocation;

    //Empty constructor for firestore
    public PersonalTask () {}

    public PersonalTask(String title, String notes, String dueDate, TaskPriority priority) {
        super(title, notes, dueDate, priority);
        this.personalLocation = "-";
    }
    @Override
    public String getTaskType() {
        return "PersonalTask";
    }
    @Override
    public String getLocation() {
        return personalLocation;
    }
}


package com.razinrahimi.remine.data;

public class PersonalTask extends Task {

    public PersonalTask () {}

    public PersonalTask(String title, String notes, String dueDate, TaskPriority priority) {
        super(title, notes, dueDate, priority);
    }

    @Override
    public String getTaskType() {
        return "PersonalTask";
    }
}


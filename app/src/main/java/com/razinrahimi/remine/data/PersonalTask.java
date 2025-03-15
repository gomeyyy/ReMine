package com.razinrahimi.remine.data;
import java.time.LocalDateTime;

public class PersonalTask extends Task {
    public PersonalTask(int taskId, String title, String description, LocalDateTime dueDate, TaskPriority priority) {
        super(taskId, title, description, dueDate, priority);
    }

    @Override
    public void displayTaskDetails() {
        System.out.println("Personal Task: " + title);
    }
}


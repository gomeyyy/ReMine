package com.razinrahimi.remine.data;
import java.time.LocalDateTime;
public class WorkTask extends Task{
    private String meetingLocation;
    public WorkTask(int taskId, String title, String description, LocalDateTime dueDate, TaskPriority priority, String meetingLocation) {
        super(taskId, title, description, dueDate, priority);
        this.meetingLocation = meetingLocation;
    }

    @Override
    public void displayTaskDetails() {
        System.out.println("Work Task: " + title + " | Due: " + dueDate + " | Priority: " + priority);
    }
}

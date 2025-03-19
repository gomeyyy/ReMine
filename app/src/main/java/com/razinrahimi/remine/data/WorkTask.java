package com.razinrahimi.remine.data;

public class WorkTask extends Task{
    private String workLocation;

    public WorkTask() {}
    public WorkTask(String title, String notes, String dueDate, TaskPriority priority, String workLocation) {
        super(title, notes, dueDate, priority);
        this.workLocation = workLocation;
    }
    @Override
    public String getTaskType() {
        return "WorkTask";
    }
}

package com.razinrahimi.remine.data;

public class HealthTask extends Task {
    private boolean isExerciseRoutine;

    public HealthTask() {}

    public HealthTask(String title, String notes, String dueDate, TaskPriority priority, boolean isExerciseRoutine) {
        super(title, notes, dueDate, priority);
        this.isExerciseRoutine = isExerciseRoutine;
    }
    @Override
    public String getTaskType() {
        return "HealthTask";
    }

}


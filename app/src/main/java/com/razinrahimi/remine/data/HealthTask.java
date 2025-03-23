package com.razinrahimi.remine.data;

public class HealthTask extends Task {
    private boolean isExerciseRoutine;
    private String healthLocation;

    public HealthTask() {}

    public HealthTask(String title, String notes, String dueDate, TaskPriority priority, boolean isExerciseRoutine) {
        super(title, notes, dueDate, priority);
        this.isExerciseRoutine = isExerciseRoutine;
        this.healthLocation = "-";
    }
    @Override
    public String getTaskType() {
        return "HealthTask";
    }
    @Override
    public String getLocation(){
        return healthLocation;
    }

}


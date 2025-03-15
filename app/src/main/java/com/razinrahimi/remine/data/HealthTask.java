package com.razinrahimi.remine.data;
import java.time.LocalDateTime;
public class HealthTask extends Task {
    private boolean isExerciseRoutine;

    public HealthTask(int taskId, String title, String description, LocalDateTime dueDate, TaskPriority priority, boolean isExerciseRoutine) {
        super(taskId, title, description, dueDate, priority);
        this.isExerciseRoutine = isExerciseRoutine;
    }

    @Override
    public void displayTaskDetails() {
        System.out.println("Health Task: " + title + " | Exercise Routine: " + isExerciseRoutine);
    }
}


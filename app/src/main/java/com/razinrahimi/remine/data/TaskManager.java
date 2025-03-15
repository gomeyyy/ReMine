package com.razinrahimi.remine.data;

import java.util.ArrayList;
import java.util.List;
public class TaskManager {
    private List<Task> tasks;

    public TaskManager() { this.tasks = new ArrayList<>(); }
    public void addTask(Task task) {
        tasks.add(task);
        System.out.println("Task added: " + task.getTitle());
    }

    public void removeTask(int taskId) {
        tasks.removeIf(task -> task.getTaskId() == taskId);
        System.out.println("Task removed with ID: " + taskId);
    }

    public void displayAllTasks() {
        System.out.println("Task List:");
        for (Task task : tasks) {
            task.displayTaskDetails();
        }
    }
}

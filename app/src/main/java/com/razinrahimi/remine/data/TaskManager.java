package com.razinrahimi.remine.data;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Class that handles task management
public class TaskManager {
    private FirebaseFirestore db; //variable for firestore
    private Context context;

    public TaskManager(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    //Method to add task to firestore
    public void addTask(Task task) {

        //To format and standardise date for dueDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.parse(task.getDueDate(), formatter).format(formatter);
        //Since firestore only store string, localdate must be converted to String with correct format

        task.setDueDate(formattedDate);

        //Find path to add task in firestore and add task
        db.collection("tasks")
                .document(task.getTaskId())
                .set(task) //Set every attributes for task in the database using task object
                //Built-in Exception Handler for databse operations
                //Return messages using Toast
                .addOnSuccessListener(aVoid -> {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Uploaded...", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Upload Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                });
    }

    //method to update task
    public void updateTask(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.parse(task.getDueDate(), formatter).format(formatter);
        task.setDueDate(formattedDate);

        db.collection("tasks")
                .document(task.getTaskId()) //Only update existing task
                .update(
                        "title", task.getTitle(),
                        "notes", task.getNotes(),
                        "dueDate", task.getDueDate(),
                        "location", task.getLocation(),
                        "priority", task.getPriority().toString(), //convert enum to string
                        "taskType", task.getTaskType()
                )
                .addOnSuccessListener(aVoid -> {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Task updated successfully!", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                });
    }


}


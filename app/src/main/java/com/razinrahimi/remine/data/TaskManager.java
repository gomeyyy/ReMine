package com.razinrahimi.remine.data;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class TaskManager {
    private FirebaseFirestore db;
    private Context context;

    public TaskManager(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    // 🔹 Add a Task (Handles Subclasses)
    public void addTask(Task task) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.parse(task.getDueDate(), formatter).format(formatter);

        task.setDueDate(formattedDate);

        db.collection("tasks")
                .document(task.getTaskId())
                .set(task)
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
}


package com.razinrahimi.remine.data;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.adapters.TaskAdapter;

public class SwipeActionsCallback extends ItemTouchHelper.SimpleCallback { //Extend ItemTouchHelper Class (For Swipe action in UI)
    private TaskAdapter adapter;
    private Context context;
    private FirebaseFirestore db; //Variable for Firestore Database

    //Constructor
    public SwipeActionsCallback(TaskAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    //Dragging function
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    //Method to swipe task for delete or edit
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Task task = adapter.getTask(position);

        if (direction == ItemTouchHelper.RIGHT) { // ✅ Swipe Right → Delete from Firestore
            showDeleteConfirmation(viewHolder, task, position);
        } else if (direction == ItemTouchHelper.LEFT) { // ✅ Swipe Left → Edit Task in Firestore
            adapter.notifyItemChanged(position); // Reset swipe
            showEditDialog(task, position);
        }
    }

    //Confirmation and toast messages for task deletion
    private void showDeleteConfirmation(RecyclerView.ViewHolder viewHolder, Task task, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this task?")
                //Delete
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.collection("tasks").document(task.getTaskId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                adapter.removeTask(position);
                                showUndoSnackbar(viewHolder.itemView, task, position);
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete task!", Toast.LENGTH_SHORT).show());
                })
                //Cancel Delete
                .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                .show();
    }

    //UI message to undo deletion
    private void showUndoSnackbar(View view, Task task, int position) {
        com.google.android.material.snackbar.Snackbar.make(view, "Task deleted", com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {
                    db.collection("tasks").document(task.getTaskId()).set(task);
                    adapter.restoreTask(position, task);
                })
                .show();
    }

    // Supposed to show confirmation for editing task but never show
    // If it works, don't touch it
    private void showEditDialog(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.add_task_activity, null);
        EditText editTitle = dialogView.findViewById(R.id.titleEt);
        EditText editDescription = dialogView.findViewById(R.id.notesEt);

        editTitle.setText(task.getTitle());
        editDescription.setText(task.getNotes());

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            task.setTitle(editTitle.getText().toString());
            task.setNotes(editDescription.getText().toString());

            db.collection("tasks").document(task.getTaskId()).set(task)
                    .addOnSuccessListener(aVoid -> {
                        adapter.notifyItemChanged(position);
                        Toast.makeText(context, "Task Updated!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update task!", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}


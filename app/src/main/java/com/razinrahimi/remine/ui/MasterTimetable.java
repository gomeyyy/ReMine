package com.razinrahimi.remine.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.adapters.TaskAdapter;
import com.razinrahimi.remine.data.Task;
import com.razinrahimi.remine.data.TaskStatus;
import com.razinrahimi.remine.data.UserTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MasterTimetable extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private FirebaseFirestore db;

    private Spinner displayCategory;
    Button goToAddTaskButton, refreshButton;
    Button buttonToDashboard, buttonToMaster, buttonToAccount;
    private Context context;
    private String taskType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_master_timetable);

        displayCategory = findViewById(R.id.display_category);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this,
                R.array.categories, // Reference to the array
                android.R.layout.simple_spinner_item
        );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        displayCategory.setAdapter(adapter1);

        String defaultCategory = "WorkTask";

        refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(view -> {
            String selectedCategorySpinner = displayCategory.getSelectedItem().toString(); // Get selected category
            String selectedCategory = defaultCategory;
            if (selectedCategorySpinner.equals("Work And Education")) {
                selectedCategory = "WorkTask";
            } else if (selectedCategorySpinner.equals("Personal")) {
                selectedCategory = "PersonalTask";
            } else if (selectedCategorySpinner.equals("Health")) {
                selectedCategory = "HealthTask";
            }
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            fetchTasksFromFirestore(selectedCategory); // Refresh RecyclerView with selected category
        });


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore & Task List
        db = FirebaseFirestore.getInstance();
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        // Fetch Tasks from Firestore
        fetchTasksFromFirestore(defaultCategory);

        goToAddTaskButton = findViewById(R.id.goToAddTaskButton);
        // Handle Add Task Button Click
        goToAddTaskButton.setOnClickListener(view -> {
            startActivity(new Intent(this, AddTask.class));
            finish(); // ✅ Moved inside the lambda
        });


        goToAddTaskButton = findViewById(R.id.goToAddTaskButton);
        goToAddTaskButton.setOnClickListener(view -> startActivity(new Intent(this, AddTask.class)));

        buttonToDashboard = findViewById(R.id.button_to_dashboard);
        buttonToAccount = findViewById(R.id.button_to_account);
        buttonToMaster = findViewById(R.id.button_to_master);

        buttonToDashboard.setOnClickListener(view -> startActivity(new Intent(this, DashboardActivity.class)));
        buttonToAccount.setOnClickListener(view -> startActivity(new Intent(this, AccountSetting.class)));
        buttonToMaster.setOnClickListener(view -> startActivity(new Intent(this, MasterTimetable.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        String taskType = "WorkTask"; //

    }

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Task task = taskAdapter.getTask(position);

            if (direction == ItemTouchHelper.RIGHT) { // Swipe Right → Delete
                showDeleteConfirmation(viewHolder, task, position);
            } else if (direction == ItemTouchHelper.LEFT) { // Swipe Left → Edit
                editTask(task);
            }
        }
    });

    private void fetchTasksFromFirestore(String taskType) {
        db.collection("tasks")
                .whereEqualTo("taskType", taskType) // ✅ Filter by task type
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Firestore Error", e.getMessage());
                            return;
                        }
                        taskList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            UserTask task = doc.toObject(UserTask.class);

                            checkAndUpdateOverdueTask(task);

                            taskList.add(task);
                        }
                        taskAdapter.setTasks(taskList);
                    }

                });
    }

    private void showDeleteConfirmation(RecyclerView.ViewHolder viewHolder, Task task, int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    db.collection("tasks").document(task.getTaskId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                taskAdapter.removeTask(position);
                                showUndoSnackbar(viewHolder.itemView, task, position);
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete task!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", (dialog, which) -> taskAdapter.notifyItemChanged(position))
                .show();
    }

    // Show Undo Snackbar
    private void showUndoSnackbar(android.view.View view, Task task, int position) {
        com.google.android.material.snackbar.Snackbar.make(view, "Task deleted", com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> restoreDeletedTask(task, position))
                .show();
    }

    // Restore deleted task
    private void restoreDeletedTask(Task task, int position) {
        db.collection("tasks").document(task.getTaskId()).set(task)
                .addOnSuccessListener(aVoid -> {
                    taskList.add(position, task);
                    taskAdapter.notifyItemInserted(position);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to restore task!", Toast.LENGTH_SHORT).show());
    }

    // Open Edit Task Page
    public void editTask(Task task) {
        // Pass task details to AddTask activity
        Intent intent = new Intent(this, AddTask.class);
        String taskId = task.getTaskId();
        intent.putExtra("taskId", task.getTaskId());

        startActivity(intent);
    }

    private void checkAndUpdateOverdueTask(Task task) {
        try {
            // ✅ Convert dueDate from String to LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Ensure Firestore date format matches
            LocalDate dueDate = LocalDate.parse(task.getDueDate(), formatter);
            LocalDate today = LocalDate.now();

            // ✅ If the task is PENDING and the due date has passed, mark it as OVERDUE
            if (dueDate.isBefore(today) && task.getStatus() == TaskStatus.PENDING) {
                task.setStatus(TaskStatus.OVERDUE);

                // ✅ Update Firestore to mark task as OVERDUE
                db.collection("tasks").document(task.getTaskId())
                        .update("status", TaskStatus.OVERDUE.name())
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Task marked as overdue: " + task.getTaskId()))
                        .addOnFailureListener(e -> Log.e("Firestore Error", "Failed to update task status", e));
            }
        } catch (DateTimeParseException e) {
            Log.e("Date Error", "Invalid date format for task: " + task.getTaskId(), e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchTasksFromFirestore(taskType); // Refresh RecyclerView after returning
    }

}

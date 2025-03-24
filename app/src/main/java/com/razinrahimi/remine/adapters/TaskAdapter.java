package com.razinrahimi.remine.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.razinrahimi.remine.R;
import com.razinrahimi.remine.data.Task;
import com.razinrahimi.remine.data.TaskStatus;
import com.razinrahimi.remine.ui.AddTask;
import com.razinrahimi.remine.ui.MasterTimetable;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList; //List to store task attributes
    private Context context;
    private FirebaseFirestore db;

    //Constructor
    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    //Part of recycler view adapter
    //Create view holder for new tasks
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    //Display data in view holder
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //Display tasks
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getNotes());
        holder.taskDueDate.setText("Due: " + task.getDueDate());
        holder.taskPriority.setText("Priority: " + task.getPriority());

        //Status Button Functions
        if (task.getStatus() == TaskStatus.PENDING) {
            holder.btnCompleteTask.setVisibility(View.VISIBLE);
            holder.txtOverdue.setVisibility(View.GONE);
            holder.btnCompleteTask.setText("Pending");
            holder.btnCompleteTask.setEnabled(true);
            holder.btnCompleteTask.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.gray)); // ✅ Change to Grey
        } else if (task.getStatus() == TaskStatus.OVERDUE) {
            holder.btnCompleteTask.setVisibility(View.GONE);
            holder.txtOverdue.setVisibility(View.VISIBLE);
            holder.txtOverdue.setText("Overdue");
        } else { // COMPLETED
            holder.btnCompleteTask.setVisibility(View.VISIBLE);
            holder.btnCompleteTask.setText("Completed");
            holder.btnCompleteTask.setEnabled(false);
            holder.btnCompleteTask.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green)); // ✅ Change to Green
        }

        // Handle Button Click → Update Firestore & RecyclerView
        // Lambda expression used
        holder.btnCompleteTask.setOnClickListener(v -> markTaskAsCompleted(task, holder.getAdapterPosition()));
    }

    //Mark task as complete method for status button
    private void markTaskAsCompleted(Task task, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //Start Database
        db.collection("tasks").document(task.getTaskId())
                .update("status", TaskStatus.COMPLETED.name()) // Update Firestore
                //Built-in exception handling (Firestore)
                //Check if database updated or no
                .addOnSuccessListener(aVoid -> {
                    task.setStatus(TaskStatus.COMPLETED); // Update locally
                    notifyItemChanged(position); // Refresh RecyclerView
                })
                //Display error message using Toast
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show());
    }

    //Return total number of items in this adapter
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    //Get task to be edited
    public Task getTask(int position) {
        return taskList.get(position);
    }

    //Add new tasks
    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    //Delete Task
    public void removeTask(int position) {
        Task task = taskList.get(position);
        db.collection("tasks").document(task.getTaskId()).delete()
                //Built-in Exception Handler To Check if task is deleted or no
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show());
    }

    //Undo deletion
    public void restoreTask(int position, Task task) {
        taskList.add(position, task);
        notifyItemInserted(position); //Insert task in list
    }

    //Hold task attributes in view holder
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDueDate, taskPriority, txtOverdue;
        Button btnCompleteTask;

        //Find UI componenents in view holder
        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            txtOverdue = itemView.findViewById(R.id.txtOverdue);
            btnCompleteTask = itemView.findViewById(R.id.btnCompleteTask);
        }
    }
}


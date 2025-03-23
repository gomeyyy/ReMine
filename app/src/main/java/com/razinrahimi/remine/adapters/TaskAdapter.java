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

    private List<Task> taskList;
    private Context context;
    private FirebaseFirestore db;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getNotes());
        holder.taskDueDate.setText("Due: " + task.getDueDate());
        holder.taskPriority.setText("Priority: " + task.getPriority());

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
        holder.btnCompleteTask.setOnClickListener(v -> markTaskAsCompleted(task, holder.getAdapterPosition()));
    }

    private void markTaskAsCompleted(Task task, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("tasks").document(task.getTaskId())
                .update("status", TaskStatus.COMPLETED.name()) // ✅ Update Firestore
                .addOnSuccessListener(aVoid -> {
                    task.setStatus(TaskStatus.COMPLETED); // ✅ Update locally
                    notifyItemChanged(position); // ✅ Refresh RecyclerView
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public Task getTask(int position) {
        return taskList.get(position);
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    public void removeTask(int position) {
        Task task = taskList.get(position);
        db.collection("tasks").document(task.getTaskId()).delete()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show());
    }

    public void restoreTask(int position, Task task) {
        taskList.add(position, task);
        notifyItemInserted(position);
    }

    public void editTask(int position) {
        Task task = taskList.get(position);

        // Pass task details to AddTask activity

        Intent intent = new Intent(context, AddTask.class);
        intent.putExtra("taskId", task.getTaskId());
        intent.putExtra("title", task.getTitle());
        intent.putExtra("description", task.getNotes());
        intent.putExtra("dueDate", task.getDueDate());
        intent.putExtra("priority", task.getPriority());

        context.startActivity(intent);
        db.collection("tasks").document(task.getTaskId()).delete();
                    taskList.remove(position);

    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, taskDueDate, taskPriority, txtOverdue;
        Button btnCompleteTask;

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


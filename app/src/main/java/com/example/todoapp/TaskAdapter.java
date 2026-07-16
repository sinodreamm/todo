package com.example.todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private DatabaseHelper dbHelper;

    public TaskAdapter(List<Task> taskList, DatabaseHelper dbHelper) {
        this.taskList = taskList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskTime.setText(task.getTime());
        // Temporarily remove listener to avoid triggering it on bind
        holder.checkboxTask.setOnCheckedChangeListener(null);
        holder.checkboxTask.setChecked(task.isCompleted());

        updateTextStyle(holder.tvTaskTitle, task.isCompleted());

        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTaskStatus(task.getId(), isChecked);
            updateTextStyle(holder.tvTaskTitle, isChecked);
        });
    }

    private void updateTextStyle(TextView tv, boolean isCompleted) {
        if (isCompleted) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskTime;
        CheckBox checkboxTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskTime = itemView.findViewById(R.id.tvTaskTime);
            checkboxTask = itemView.findViewById(R.id.checkboxTask);
        }
    }
}

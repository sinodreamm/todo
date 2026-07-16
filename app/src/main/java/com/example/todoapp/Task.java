package com.example.todoapp;

public class Task {
    private int id;
    private String title;
    private String time;
    private boolean isCompleted;

    public Task(int id, String title, String time, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getTime() { return time; }
    public boolean isCompleted() { return isCompleted; }

    public void setCompleted(boolean completed) { isCompleted = completed; }
}

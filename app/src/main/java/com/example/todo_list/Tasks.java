package com.example.todo_list;

public class Tasks {
    private String title;
    private String description;
    private String deadline;
    private boolean status;

    public Tasks() {
        // Default constructor required for Firebase
    }

    public Tasks(String title, String description, String deadline, boolean status) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    // Getter and Setter for Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter for Deadline
    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    // Getter and Setter for Status
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

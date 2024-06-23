package cen3024LMS;

/**
 * Name: Justin Winker
 * Course: CEN3024C Software Development
 * Date: 06/14/2024
 * Represents a constructor class for single book in a library system
 * This class encapsulated the basic properties of a book like the ID, Title, and Author
 */

import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean isCheckedOut;
    private LocalDate dueDate;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isCheckedOut = false;  // All books are NOT checked out, initially
        this.dueDate = null;  // Initially, there is no due date
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    // Getter and Setter for isCheckedOut
    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        isCheckedOut = checkedOut;
    }

    @Override
    public String toString() {
        return id + ", " + title + ", " + author + ", " + (isCheckedOut ? "Checked out, Due: " + dueDate : "Available");
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}





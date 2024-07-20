package cen3024LMS;

/**
 * Represents a single book in a library management system. This class encapsulates the basic properties
 * of a book such as ID, title, and author.
 *
 * @author Justin Winker
 * @version 1.0
 * @since 2024-06-14
 */

import java.time.LocalDate;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean isCheckedOut;
    private LocalDate dueDate;

    /**
     * Constructs a new Book instance with the specified ID, title, and author.
     * Initially, books are not checked out and have no due date.
     *
     * @param id the unique identifier for the book
     * @param title the title of the book
     * @param author the author of the book
     */

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isCheckedOut = false;  // All books are NOT checked out, initially
        this.dueDate = null;  // Initially, there is no due date
    }

    /**
     * Returns the ID (barcode) of the book
     * @return id
     */

    public int getId() {
        return id;
    }

    /**
     * Returns the title of the book
     * @return title
     */

    public String getTitle() {
        return title;
    }

    /**
     * Returns the author of the book
     * @return author
     */

    public String getAuthor() {
        return author;
    }

    /**
     * Returns whether the book is checked out.
     *
     * @return true if the book is checked out, false otherwise
     */

    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    /**
     * Sets the checked out status of the book.
     *
     * @param checkedOut the new checked out status of the book
     */

    public void setCheckedOut(boolean checkedOut) {
        isCheckedOut = checkedOut;
    }

    /**
     * Returns a string representation of the book, displaying its ID, title, author, and check-out status.
     *
     * @return a string representation of the book
     */

    @Override
    public String toString() {
        return id + ", " + title + ", " + author + ", " + (isCheckedOut ? "Checked out, Due: " + dueDate : "Available");
    }

    /**
     * Returns the due date of the book.
     *
     * @return the book's due date, or null if not checked out
     */

    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date of the book.
     *
     * @param dueDate the new due date for the book
     */

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}









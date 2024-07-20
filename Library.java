package cen3024LMS;

/**
 * Represents a library management system for handling books. This class manages a collection of books and interfaces
 * with a file to persist the collection's state.
 *
 * @author Justin Winker
 * @version 1.0
 * @since 2024-06-14
 */

import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Library {
    private Map<Integer, Book> books = new HashMap<>();
    private String filePath;
    private Scanner scanner;

    /**
     * Initializes the library and loads the books from the specified file.
     *
     * @param filePath The path to the file where the library data is stored.
     * @throws IOException If an error occurs creating or accessing the file.
     */

    public Library(String filePath) throws IOException {
        this.filePath = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();  // Create a new file if it doesn't exist
        }
        loadBooksFromFile();  // Load books after ensuring the file exists
    }

    /**
     * Method: loadBooksFromFile
     * Loads the books from the file the user provided
     * This method reads a text file (.txt) line by line and creates the Book object based on the data provided
     * Reads the due date from the file and sets it appropriately when creating Book objects
     */

    private void loadBooksFromFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    int id = Integer.parseInt(parts[0].trim());
                    String title = parts[1].trim();
                    String author = parts[2].trim();
                    boolean isCheckedOut = "Checked out".equals(parts[3].trim());
                    LocalDate dueDate = parts[4].trim().equals("null") ? null : LocalDate.parse(parts[4].trim());
                    Book book = new Book(id, title, author);
                    book.setCheckedOut(isCheckedOut);
                    book.setDueDate(dueDate);
                    books.put(id, book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing the file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Adds a new book to the library and saves the current list to the file.
     *
     * @param id The book's ID.
     * @param title The title of the book.
     * @param author The author of the book.
     */

    public void addBook(int id, String title, String author) {
        if (!books.containsKey(id)) {
            books.put(id, new Book(id, title, author));
            saveToFile();
        } else {
            System.out.println("A book with this ID already exists.");
        }
    }

    /**
     *
     * @param id
     * @return id (barcode)
     */
    public Book getBookById(int id) {
        return books.get(id);
    }

    /**
     *
     * @param title
     * @return title of the book
     * Excludes being case sensitive to input
     */
    public Book getBookByTitle(String title) {
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }


    /**
     * Removes a book from the library by its ID and updates the file.
     *
     * @param id The ID of the book to remove.
     * @return A message indicating if the book was not found.
     */

    public String removeBookByID(int id) {
        if (books.containsKey(id)) {
            books.remove(id);
            saveToFile();
        } else {
            System.out.println("No book found with ID: " + id);
        }
        return null;
    }

    /**
     * Lists all the books in the library, showing their current status (checked in or checked out).
     *
     * @return A string listing all books.
     */

    public String listBooks() {
        StringBuilder listing = new StringBuilder();
        for (Book book : books.values()) {
            listing.append(book.toString()).append("\n");
        }
        return listing.toString();
    }

    /**
     *  Saves the current list of books to the file.
     *  Method overwrites the existing file the with updated data
     */

    private void saveToFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filePath, false)); // False to overwrite the file
            for (Book book : books.values()) {
                String dueDateStr = book.getDueDate() != null ? book.getDueDate().toString() : "null";
                writer.println(book.getId() + ", " + book.getTitle() + ", " + book.getAuthor() + ", " + (book.isCheckedOut() ? "Checked out" : "Available") + ", " + dueDateStr);
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();  // Ensure the writer is closed to flush data
            }
        }
    }

    /**
     * Removes a book from the library by its title and updates the file.
     *
     * @param title The title of the book to remove.
     */

    public void removeBookByTitle(String title) {
        Iterator<Map.Entry<Integer, Book>> iterator = books.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Book> entry = iterator.next();
            if (entry.getValue().getTitle().equalsIgnoreCase(title)) {
                iterator.remove();
                saveToFile();
                break;
            }
        }
    }

    /**
     * Checks out a book by setting its status to "checked out" and calculating its due date based on the number of weeks specified.
     *
     * @param title The title of the book to check out.
     * @param weeks The number of weeks until the book is due.
     */

    public void checkOutBook(String title, int weeks) {
        Book book = getBookByTitle(title);
        if (book != null && !book.isCheckedOut()) {
            if (weeks < 1 || weeks > 3) {
                System.out.println("Invalid number of weeks. Please enter a value between 1 and 3.");
                return;
            }
            book.setCheckedOut(true);
            book.setDueDate(LocalDate.now().plusWeeks(weeks));
            saveToFile();
            System.out.println("Book was checked out successfully. Due back on: " + book.getDueDate());
        } else {
            System.out.println("Book is either not available or already checked out.");
        }
    }


    /**
     * Checks in a book by setting its status to "available" and removing the due date.
     *
     * @param title The title of the book to check in.
     */

    public void checkInBook(String title) {
        for (Book book : books.values()) {
            if (book.getTitle().equalsIgnoreCase(title) && book.isCheckedOut()) {
                book.setCheckedOut(false);
                book.setDueDate(null);  // Reset the due date
                saveToFile();
                System.out.println("Book was checked in successfully.");
                return;
            }
        }
        System.out.println("Book is either not available or already checked in.");
    }
}
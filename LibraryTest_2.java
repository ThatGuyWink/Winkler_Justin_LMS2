package cen3024LMS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest_2 {

    private Library library;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize the library with some test data
        library = new Library("testLibrary.txt"); // Assume testLibrary.txt is handled appropriately
        // Add two books for testing removal
        library.addBook(1029, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling");
        library.addBook(1030, "The Hobbit", "J.R.R. Tolkien");
    }

    @Test
    @DisplayName("Remove Book By ID Test")
    void removeBookByIdTest() {
        assertNotNull(library.getBookById(1029), "Book should exist before removal");

        // Removes the book
        library.removeBookByID(1029);

        // Assert that the book is no longer in the library
        assertNull(library.getBookById(1029), "Book should be null after being removed by ID");
    }

    @Test
    @DisplayName("Remove Book By Title Test")
    void removeBookByTitleTest() {
        assertNotNull(library.getBookByTitle("The Hobbit"), "Book should exist before removal");

        // Perform the removal
        library.removeBookByTitle("The Hobbit");

        // Assert that the book is no longer in the library
        assertNull(library.getBookByTitle("The Hobbit"), "Book should be null after being removed by Title");
    }
}

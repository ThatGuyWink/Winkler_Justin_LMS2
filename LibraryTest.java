package cen3024LMS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Book bookTest;

    @BeforeEach
    void setUp() throws IOException {
        // Initialize a test file library and the test book data
        library = new Library("testLibrary.txt");
        bookTest = new Book(1029, "Harry Potter and the Sorcerer's Stone", "J.K. Rowling");
        library.addBook(bookTest.getId(), bookTest.getTitle(), bookTest.getAuthor()); // Assuming addBook handles these directly
    }

    @Test
    @DisplayName("Add Book Test")
    void addBookTest() {
        // Finds the book via ID and confirms it was added correctly
        Book addedBook = library.getBookById(1029);
        assertNotNull(addedBook, "Test failed: The book was not added to the library.");
        assertEquals("Harry Potter and the Sorcerer's Stone", addedBook.getTitle(), "Test failed: Title does not match.");
        assertEquals("J.K. Rowling", addedBook.getAuthor(), "Test failed: Author does not match.");
    }
}
package cen3024LMS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest_3 {

    private Library library;
    private Book book;

    @BeforeEach
    void setUp() throws IOException {
        library = new Library("testLibrary.txt");
        // Adding a book for testing checkout and check in functionalities
        library.addBook(2001, "1984", "George Orwell");
        book = library.getBookByTitle("1984");
    }

    @Test
    @DisplayName("Check Out Book Test")
    void checkOutBookTest() {
        assertNull(book.getDueDate(), "Due date should be null before checkout.");

        // Simulate checking out the book for 2 weeks
        library.checkOutBook(book.getTitle(), 2);

        assertNotNull(book.getDueDate(), "Due date should not be null after checkout.");
        assertEquals(LocalDate.now().plusWeeks(2), book.getDueDate(),
                "Due date should be set to 2 weeks from now.");
    }

    @Test
    @DisplayName("Check In Book Test")
    void checkInBookTest() {
        // First check out the book
        library.checkOutBook(book.getTitle(), 2); // Check out the book to set a due date
        assertNotNull(book.getDueDate(), "Due date should be set by checkout.");

        // Now check in the book
        library.checkInBook(book.getTitle());

        assertNull(book.getDueDate(), "Due date should be null after check-in.");
    }
}

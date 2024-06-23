package cen3024LMS;

import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDate;

/**
 * Name: Justin Winker
 * Course: CEN3024C Software Development
 * Date: 06/14/2024
 * This is the main class for the Library Management System, responsible for interfacing with the user
 * and managing library operations such as adding, removing, listing books and setting a status for books either checked in or out.
 */

public class LibraryMain {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter the file path for the library data
        System.out.println("Please enter the database file you would like to open (include .txt): ");
        String filePath = scanner.nextLine();

        // Create a Library object. If the file doesn't exist, it will be created.
        Library library = new Library(filePath);

        // Print the initial list of books loaded from file
        System.out.println("      Library Database ");
        System.out.println("----------------------------");
        System.out.println(library.listBooks());

        // Handle user commands in a loop
        while (true) {
            System.out.println("What would you like to do? Commands are as listed - add book, remove by ID, remove by title, check out, check in, list books or quit (Please note these commands ARE case sensitive):");
            String command = scanner.nextLine().trim();

            if ("quit".equals(command)) {
                System.out.println("Exiting the Library Management System.");
                break;
            }

            switch (command) {
                case "add book":
                    System.out.println("Enter the book ID, Title, and Author (separated by commas):");
                    String[] bookDetails = scanner.nextLine().split(",");
                    if (bookDetails.length == 3) {
                        int id = Integer.parseInt(bookDetails[0].trim());
                        String title = bookDetails[1].trim();
                        String author = bookDetails[2].trim();
                        library.addBook(id, title, author);
                        System.out.println(library.listBooks());
                    } else {
                        System.out.println("Invalid input format.");
                    }
                    break;

                case "remove by ID":
                    System.out.println("Enter the ID of the book to remove:");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    library.removeBookByID(id);
                    System.out.println("Book removed.");
                    System.out.println(library.listBooks());
                    break;

                case "remove by title":
                    System.out.println("Enter the title of the book to remove:");
                    String title = scanner.nextLine().trim();
                    library.removeBookByTitle(title); // Call the method directly from the Library object
                    System.out.println("Book removed.");
                    System.out.println(library.listBooks());
                    break;


                case "check out":
                    System.out.println("Enter the title of the book to check out:");
                    title = scanner.nextLine().trim();
                    System.out.println("How many weeks would you like to check out the book for (1 to 3)?");
                    int weeks = scanner.nextInt();  // Get weeks directly from user
                    scanner.nextLine();  // Consume the newline left-over after reading integer
                    library.checkOutBook(title, weeks);
                    System.out.println(library.listBooks());
                    break;


                case "check in":
                    System.out.println("Enter the title of the book to check in:");
                    title = scanner.nextLine().trim();
                    library.checkInBook(title);
                    System.out.println(library.listBooks());
                    break;


                case "list books":
                    System.out.println("Current list of books:");
                    System.out.println(library.listBooks());
                    break;

                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
        scanner.close();
    }
}


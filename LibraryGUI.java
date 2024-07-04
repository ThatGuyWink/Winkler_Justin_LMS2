import cen3024LMS.Library;

/*
 * Name: Justin Winker
 * Course: CEN3024C Software Development
 * Date: 06/14/2024
 * Represents a library management system for handling books. This class manages a collection of books and interfaces with a file to persist the collection's state

 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class LibraryGUI extends JFrame {
    private JLabel databaseLabel;
    private JTextField databaseTextField;
    private JButton loadFileButton, addBookButton, removeByBarcodeButton, removeByTitleButton, checkInButton, checkOutButton;
    private JTextField barcodeTextField, titleTextField;
    private JTextArea displayArea;
    private ArrayList<String> books; // Simple storage for books
    private File currentFile; // Store the current file

    /*
     * Constructor for LibraryGUI.
     * Initializes the user interface for the library management system.
     */

    public LibraryGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        databaseLabel = new JLabel("Enter file name (include .txt):");
        databaseTextField = new JTextField(10);
        databaseTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, databaseTextField.getPreferredSize().height));
        loadFileButton = new JButton("Load File");
        addBookButton = new JButton("Add Book");
        removeByBarcodeButton = new JButton("Remove by ID");
        removeByTitleButton = new JButton("Remove by Title");
        checkInButton = new JButton("Check In Book");
        checkOutButton = new JButton("Check Out Book");
        barcodeTextField = new JTextField(10);
        barcodeTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, barcodeTextField.getPreferredSize().height));
        titleTextField = new JTextField(10);
        titleTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleTextField.getPreferredSize().height));
        displayArea = new JTextArea(20, 40);
        displayArea.setEditable(false);
        books = new ArrayList<>();

        leftPanel.add(databaseLabel);
        leftPanel.add(databaseTextField);
        leftPanel.add(loadFileButton);
        leftPanel.add(addBookButton);
        leftPanel.add(new JLabel("Book ID:"));
        leftPanel.add(barcodeTextField);
        leftPanel.add(removeByBarcodeButton);
        leftPanel.add(new JLabel("Book Title:"));
        leftPanel.add(titleTextField);
        leftPanel.add(removeByTitleButton);
        leftPanel.add(checkInButton);
        leftPanel.add(checkOutButton);

        add(leftPanel, BorderLayout.WEST);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        loadFileButton.addActionListener(this::loadFile);
        addBookButton.addActionListener(this::addBook);
        removeByBarcodeButton.addActionListener(this::removeByBarcode);
        removeByTitleButton.addActionListener(this::removeByTitle);
        checkInButton.addActionListener(this::checkInBook);
        checkOutButton.addActionListener(this::checkOutBook);

        addBookButton.setVisible(false);
        removeByBarcodeButton.setVisible(false);
        removeByTitleButton.setVisible(false);
        checkInButton.setVisible(false);
        checkOutButton.setVisible(false);

        setVisible(true);
    }


    /*
     * Method: loadFile
     * Loads books from a file and displays them in the GUI.
     * ActionEvent e - The event triggering this method.
     */

    private void loadFile(ActionEvent e) {
        String filename = databaseTextField.getText();
        currentFile = new File(filename);
        databaseTextField.setText("");
        if (!currentFile.exists()) {
            JOptionPane.showMessageDialog(this, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try (Scanner scanner = new Scanner(currentFile)) {
                books.clear();
                while (scanner.hasNextLine()) {
                    books.add(scanner.nextLine());
                }
                displayArea.setText(String.join("\n", books));
                JOptionPane.showMessageDialog(this, "File loaded successfully!");
                addBookButton.setVisible(true);
                removeByBarcodeButton.setVisible(true);
                removeByTitleButton.setVisible(true);
                checkInButton.setVisible(true);
                checkOutButton.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * Method: addBook
     * Adds a new book to the library with a status of 'Available' and no due date.
     * ActionEvent e - The event triggering this method.
     */

    private void addBook(ActionEvent e) {
        String bookDetails = JOptionPane.showInputDialog(this, "Enter ID, Title, and Author (comma separated):"); // still writing to a .txt file
        if (bookDetails != null && bookDetails.split(",").length == 3) {
            bookDetails += ", Available, null";  // Append default status and due date
            books.add(bookDetails);
            displayArea.setText(String.join("\n", books));
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            saveBooksToFile();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter ID, Title, and Author correctly.");
        }
        clearTextFields(); // Clearing input fields after action
    }

    /*
     * Method: removeByBarcode
     * Removes a book from the library based on its ID.
     * ActionEvent e - The event triggering this method.
     */

    private void removeByBarcode(ActionEvent e) {
        String barcode = barcodeTextField.getText().trim();
        boolean found = books.removeIf(b -> b.split(",")[0].trim().equals(barcode));
        if (found) {
            displayArea.setText(String.join("\n", books));
            JOptionPane.showMessageDialog(this, "Book removed successfully!");
            saveBooksToFile();
        } else {
            JOptionPane.showMessageDialog(this, "No book found with ID: " + barcode);
        }
        barcodeTextField.setText(""); // Clear the text field after operation
    }

    /*
     * Method: removeByTitle
     * Removes a book from the library based on its title.
     * ActionEvent e - The event triggering this method.
     */

    private void removeByTitle(ActionEvent e) {
        String title = titleTextField.getText().trim();
        boolean found = books.removeIf(b -> b.split(",")[1].trim().equalsIgnoreCase(title));
        if (found) {
            displayArea.setText(String.join("\n", books));
            JOptionPane.showMessageDialog(this, "Book removed successfully!");
            saveBooksToFile();
        } else {
            JOptionPane.showMessageDialog(this, "No book found with title: " + title);
        }
        titleTextField.setText(""); // Clear the text field after operation
    }

    /*
     * Method: checkOutBook
     * Checks out a book for a specified period.
     * ActionEvent e - The event triggering this method.
     */

    private void checkOutBook(ActionEvent e) {
        String title = JOptionPane.showInputDialog(this, "Enter the title of the book to check out:");
        if (title != null && !title.isEmpty()) {
            boolean updated = false;
            for (int i = 0; i < books.size(); i++) {
                String[] parts = books.get(i).split(",");
                if (parts[1].trim().equalsIgnoreCase(title) && parts[3].trim().equals("Available")) {
                    String[] options = {"1 week", "2 weeks", "3 weeks"};
                    int weeks = JOptionPane.showOptionDialog(null, "Select the number of weeks for checkout:", "Checkout Duration",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, options[0]);
                    if (weeks == -1) return;  // if user closes the dialog or clicks cancel, do nothing

                    parts[3] = "Checked out";
                    parts[4] = LocalDate.now().plusWeeks(weeks + 1).toString(); // weeks + 1 because index starts at 0
                    books.set(i, String.join(",", parts));
                    updated = true;
                    break;
                }
            }
            if (updated) {
                displayArea.setText(String.join("\n", books));
                JOptionPane.showMessageDialog(this, "Book checked out successfully.");
                saveBooksToFile();
            } else {
                JOptionPane.showMessageDialog(this, "Book is either not available or already checked out.");
            }
        }
        titleTextField.setText(""); // Clear the text field after operation
    }

    /*
     * Method: checkInBook
     * Checks in a previously checked out book.
     * ActionEvent e - The event triggering this method.
     */

    private void checkInBook(ActionEvent e) {
        String title = JOptionPane.showInputDialog(this, "Enter the title of the book to check in:");
        if (title != null && !title.isEmpty()) {
            boolean updated = false;
            for (int i = 0; i < books.size(); i++) {
                String[] parts = books.get(i).split(",");
                if (parts[1].trim().equalsIgnoreCase(title) && parts[3].trim().equals("Checked out")) {
                    parts[3] = "Available";
                    parts[4] = "null";  // Reset the due date
                    books.set(i, String.join(",", parts));
                    updated = true;
                    break;
                }
            }
            if (updated) {
                displayArea.setText(String.join("\n", books));
                JOptionPane.showMessageDialog(this, "Book checked in successfully.");
                saveBooksToFile();
            } else {
                JOptionPane.showMessageDialog(this, "Book is either not available or already checked in.");
            }
        }
        titleTextField.setText(""); // Clear the text field after operation
    }

    /*
     * Method: saveBooksToFile
     * Saves the current state of the book list to a file.
     */

    private void saveBooksToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter(currentFile))) {
            for (String book : books) {
                out.println(book);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Clears all text fields after an action is completed to prepare for the next input.
     */
    private void clearTextFields() {
        databaseTextField.setText("");
        barcodeTextField.setText("");
        titleTextField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryGUI::new);
    }
}


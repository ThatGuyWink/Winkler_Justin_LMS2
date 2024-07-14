import cen3024LMS.Library;

/*
 * Name: Justin Winker
 * Course: CEN3024C Software Development
 * Date: 06/14/2024
 * Represents a library management system for handling books. This class manages a collection of books and interfaces with a file to persist the collection's state

 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;

public class LibraryGUI extends JFrame {
    private JLabel databaseLabel;
    private JTextField databaseTextField;
    private JButton loadFileButton, addBookButton, removeByBarcodeButton, removeByTitleButton, checkInButton, checkOutButton;
    private JTextField barcodeTextField, titleTextField;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    /*
     * Constructor: LibraryGUI
     * Initializes the GUI components and layouts for the Library Management System.
     */

    public LibraryGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1500, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"ID", "Title", "Author", "Status", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        bookTable.setFillsViewportHeight(true);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        databaseLabel = new JLabel("Database:");
        databaseTextField = new JTextField(10);
        databaseTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, databaseTextField.getPreferredSize().height));
        loadFileButton = new JButton("Load Data");
        addBookButton = new JButton("Add Book");
        removeByBarcodeButton = new JButton("Remove by ID");
        removeByTitleButton = new JButton("Remove by Title");
        checkInButton = new JButton("Check In Book");
        checkOutButton = new JButton("Check Out Book");
        barcodeTextField = new JTextField(10);
        barcodeTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, barcodeTextField.getPreferredSize().height));
        titleTextField = new JTextField(10);
        titleTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleTextField.getPreferredSize().height));

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
        add(new JScrollPane(bookTable), BorderLayout.CENTER); // Add the table in a scroll pane

        loadFileButton.addActionListener(this::loadBooks);
        addBookButton.addActionListener(this::addBook);
        removeByBarcodeButton.addActionListener(this::removeByBarcode);
        removeByTitleButton.addActionListener(this::removeByTitle);
        checkInButton.addActionListener(this::checkInBook);
        checkOutButton.addActionListener(this::checkOutBook);

        setVisible(true);
    }

    /*
     * Method: connectToDatabase
     * Establishes a connection to the SQL Server database.
     * Returns a Connection object or null if a connection failure occurs.
     */

    private Connection connectToDatabase() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=BookLibrary;encrypt=true;trustServerCertificate=true;";
            String username = "SA";
            String password = "CEN3024csd1";
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /*
     * Method: loadBooks
     * Fetches and displays all books from the database into the JTable.
     * - ActionEvent e: the event that triggered this method
     */

    private void loadBooks(ActionEvent e) {
        String sql = "SELECT ID, Title, Author, Status, DueDate FROM Books ORDER BY ID";
        try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            tableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getString("Status"),
                        rs.getObject("DueDate") == null ? "N/A" : rs.getObject("DueDate").toString()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Method: addBook
     * Adds book to the database and displays to the JTables
     * - ActionEvent e: the event that triggered this method
     */

    private void addBook(ActionEvent e) {
        String bookDetails = JOptionPane.showInputDialog(this, "Enter ID, Title, and Author (comma separated):", "Library Management System", JOptionPane.QUESTION_MESSAGE);
        if (bookDetails != null && bookDetails.split(",").length == 3) {
            String[] parts = bookDetails.split(",");
            String sql = "INSERT INTO Books (ID, Title, Author, Status, DueDate) VALUES (?, ?, ?, 'Available', NULL)";
            try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(parts[0].trim()));
                pstmt.setString(2, parts[1].trim());
                pstmt.setString(3, parts[2].trim());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Library Management System", JOptionPane.INFORMATION_MESSAGE);
                loadBooks(e); // Refresh the display area
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to add book: " + ex.getMessage(), "Library Management System", JOptionPane.ERROR_MESSAGE);
            }
            databaseTextField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter ID, Title, and Author correctly.", "Library Management System", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * Method: removeByBarcode
     * Removes book by ID (barcode)
     * - ActionEvent e: the event that triggered this method
     */

    private void removeByBarcode(ActionEvent e) {
        String id = barcodeTextField.getText().trim();
        String sql = "DELETE FROM Books WHERE ID = ?";
        try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Book removed successfully!");
                loadBooks(e); // Refresh the display area
            } else {
                JOptionPane.showMessageDialog(this, "No book found with ID: " + id);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to remove book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        databaseTextField.setText("");
    }

    /*
     * Method: removeByTitle
     * Removes book by the title of the book
     * - ActionEvent e: the event that triggered this method
     */

    private void removeByTitle(ActionEvent e) {
        String title = titleTextField.getText().trim();
        String sql = "DELETE FROM Books WHERE Title = ?";
        try (Connection conn = connectToDatabase(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Book removed successfully!");
                loadBooks(e); // Refresh the display area
            } else {
                JOptionPane.showMessageDialog(this, "No book found with title: " + title);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to remove book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        databaseTextField.setText("");
    }

    /*
     * Method: checkOutBook
     * Checks out book and changes the status from Available to Checked Out and adds a return date value based on users selection between 1-3 weeks
     * - ActionEvent e: the event that triggered this method
     */
    private void checkOutBook(ActionEvent e) {
        String title = JOptionPane.showInputDialog(this, "Enter the title of the book to check out:", "Library Management System", JOptionPane.QUESTION_MESSAGE);
        if (title != null && !title.isEmpty()) {
            String checkAvailableSql = "SELECT Status FROM Books WHERE Title = ? AND Status = 'Available'";
            try (Connection conn = connectToDatabase(); PreparedStatement checkStmt = conn.prepareStatement(checkAvailableSql)) {
                checkStmt.setString(1, title);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    Object[] options = {"1 week", "2 weeks", "3 weeks"};
                    int weeks = JOptionPane.showOptionDialog(null, "Select the number of weeks for checkout:",
                            "Library Management System", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, options[0]);
                    if (weeks >= 0) {
                        LocalDate dueDate = LocalDate.now().plusWeeks(weeks + 1);
                        String sql = "UPDATE Books SET Status = 'Checked out', DueDate = ? WHERE Title = ? AND Status = 'Available'";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, dueDate.toString());
                            pstmt.setString(2, title);
                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Book checked out successfully. Due back on: " + dueDate, "Library Management System", JOptionPane.INFORMATION_MESSAGE);
                            loadBooks(e);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Checkout cancelled.", "Library Management System", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book is either not available or already checked out.", "Library Management System", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to check out book: " + ex.getMessage(), "Library Management System", JOptionPane.ERROR_MESSAGE);
            } databaseTextField.setText("");
        }
    }


    /*
     * Method: checkInBook
     * Add the books back to the database and sets status back to available
     * - ActionEvent e: the event that triggered this method
     */

    private void checkInBook(ActionEvent e) {
        String title = JOptionPane.showInputDialog(this, "Enter the title of the book to check in:", "Library Management System", JOptionPane.QUESTION_MESSAGE);
        if (title != null && !title.isEmpty()) {
            String checkStatusSql = "SELECT Status FROM Books WHERE Title = ? AND Status = 'Checked out'";
            try (Connection conn = connectToDatabase(); PreparedStatement checkStmt = conn.prepareStatement(checkStatusSql)) {
                checkStmt.setString(1, title);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    String sql = "UPDATE Books SET Status = 'Available', DueDate = NULL WHERE Title = ? AND Status = 'Checked out'";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, title);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Book checked in successfully.", "Library Management System", JOptionPane.INFORMATION_MESSAGE);
                        loadBooks(e);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Book is either not available or already checked in.", "Library Management System", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to check in book: " + ex.getMessage(), "Library Management System", JOptionPane.ERROR_MESSAGE);
            } databaseTextField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryGUI::new);
    }
}


package ExpenseTracker;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Vector;

public class ExpenseTracker extends JFrame {

    private JTextField amountField, categoryField, descriptionField;
    private JLabel totalLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private final String FILE_NAME = "expenses.csv";

    public ExpenseTracker() {
        setTitle("Personal Expense Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        amountField = new JTextField();
        categoryField = new JTextField();
        descriptionField = new JTextField();
        JButton addButton = new JButton("Add Expense");

        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(new JLabel("Date (Auto):"));
        inputPanel.add(amountField);
        inputPanel.add(categoryField);
        inputPanel.add(descriptionField);
        inputPanel.add(addButton);

        // Table to display expenses
        String[] columns = {"Amount", "Category", "Description", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);

        // Bottom panel with total
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: ₹0.0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add button logic
        addButton.addActionListener(e -> addExpense());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());

        setVisible(true);
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String description = descriptionField.getText();
            String date = LocalDate.now().toString();

            if (category.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            Object[] row = {amount, category, description, date};
            tableModel.addRow(row);

            updateTotal();
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        }
    }

    private void updateTotal() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += Double.parseDouble(tableModel.getValueAt(i, 0).toString());
        }
        totalLabel.setText("Total: ₹" + total);
    }

    private void clearFields() {
        amountField.setText("");
        categoryField.setText("");
        descriptionField.setText("");
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Vector<?> row = tableModel.getDataVector().elementAt(i);
                bw.write(String.join(",", row.toString().replaceAll("[\\[\\]]", "").split(", ")));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Expenses saved to file!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file.");
        }
    }

    private void loadFromFile() {
        tableModel.setRowCount(0); // clear current data
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(data);
            }
            updateTotal();
            JOptionPane.showMessageDialog(this, "Expenses loaded from file!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTracker::new);
    }
}

package sms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class tes extends JFrame implements ActionListener {
    private JButton loadButton;
    private JTable table;
    private DefaultTableModel tableModel;

    public tes() {
        // Set up the main window
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Set up the table
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Age");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Batch");
        table = new JTable(tableModel);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Set up the load button
        loadButton = new JButton("Load Data");
        loadButton.addActionListener(this);

        // Add the button and scroll pane to a panel
        JPanel panel = new JPanel();
        panel.add(loadButton);
        panel.add(scrollPane);

        // Add the panel to the main window
        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        tes app = new tes();
        app.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadButton) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish a connection to the database
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sms", "root", "");

                // Create a statement and execute a query
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM student");

                // Clear the table
                tableModel.setRowCount(0);

                // Add the data to the table
                while (rs.next()) {
                    String id = rs.getString("s_id");
                    String name = rs.getString("name");
                    String age = rs.getString("age");
                    String gender = rs.getString("gender");
                    String batch = rs.getString("batch");
                    tableModel.addRow(new Object[]{id, name, age, gender, batch});
                }

                // Close the connection
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


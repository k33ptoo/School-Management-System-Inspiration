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

public class DatabaseGUI extends JFrame {
  private JButton connectButton;
  private JTable table;

  public DatabaseGUI() {
    super("Database GUI");

    // Create a panel to hold the connect button
    JPanel buttonPanel = new JPanel();
    connectButton = new JButton("Connect to Database");
    buttonPanel.add(connectButton);

    // Create a table with a default table model
    table = new JTable(new DefaultTableModel());

    // Add the components to the frame
    getContentPane().add(buttonPanel, BorderLayout.NORTH);
    getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

    // Add an action listener to the connect button
    connectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          // Load the MySQL JDBC driver
          Class.forName("com.mysql.cj.jdbc.Driver");

          // Establish a connection to the database
          Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sms", "root", "");

          // Execute a query and get the results
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT * FROM students");

          // Populate the table with the results
          DefaultTableModel model = (DefaultTableModel) table.getModel();
          model.setRowCount(6);
          while (rs.next()) {
            Object[] row = new Object[] { rs.getInt("student_id"), rs.getString("fullName"),
            rs.getString("email"), rs.getString("phone"), rs.getInt("department_id") };
            model.addRow(row);
          }

          // Close the connection
          conn.close();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });

    // Set the frame properties
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 300);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  public static void main(String[] args) {
    new DatabaseGUI();
  }
}

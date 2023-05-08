package sms;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ConnectionView extends JFrame {
    
    private JTextField urlTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private DBHandler dbHandler;
    
    public ConnectionView() {
        super("Connection View");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new GridLayout(4, 2));
        
        JLabel urlLabel = new JLabel("URL:");
        urlTextField = new JTextField();
        JLabel usernameLabel = new JLabel("Username:");
        usernameTextField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        connectButton = new JButton("Connect");
        
        mainPanel.add(urlLabel);
        mainPanel.add(urlTextField);
        mainPanel.add(usernameLabel);
        mainPanel.add(usernameTextField);
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(new JLabel(""));
        mainPanel.add(connectButton);
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle connect button action
                String url = urlTextField.getText();
                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());
                
                dbHandler = new DBHandler(url, username, password);
                if (dbHandler.isConnected()) {
                    JOptionPane.showMessageDialog(null, "Connected to database");
                } else {
                    JOptionPane.showMessageDialog(null, "Could not connect to database");
                }
            }
        });
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new ConnectionView();
    }
}

class DBHandler {
    
    private Connection conn;
    
    public DBHandler(String url, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isConnected() {
        return conn != null;
    }
    
    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    
    public int executeUpdate(String query) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

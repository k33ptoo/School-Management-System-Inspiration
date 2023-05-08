package sms;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * The class that allows access to a database for reading and writing data
 * purposes
 * 
 * @author Artiom
 *
 */
public class DBHandler {
	/**
	 * Login to connect to the database
	 */
	private static String login;

	/**
	 * Password to connect to the database
	 */
	private static String password;

	/**
	 * Database URL
	 */
	static String databaseUrl;

	/**
	 * The var that stores students table's name
	 */
	private final static String studentsTable;

	/**
	 * The var that stores courses table's name
	 */
	private final static String coursesTable;

	/**
	 * The var that stores faculties table's name
	 */
	private final static String facultiesTable;

	/**
	 * Default constructor
	 */
	public DBHandler() {

	}

	/**
	 * Static initializers
	 */
	static {
		login = "root";
		databaseUrl = "jdbc:mysql://localhost:3306/sms";

		studentsTable = "students";
		coursesTable = "courses";
		facultiesTable = "departments";
	}

	/**
	 * @return The login to connect to the database
	 */
	public static String getLogin() {
		return login;
	}

	/**
	 * @param login - The login to set to connect to the database
	 */
	public static void setLogin(final String login) {
		DBHandler.login = login;
	}

	/**
	 * @return The password to connect to the database
	 */
	public static String getPassword() {
		return password;
	}

	/**
	 * @param password - The password to set to connect to the database
	 */
	public static void setPassword(final String password) {
		DBHandler.password = password;
	}

	/**
	 * @param databaseUrl - the database url to set
	 */
	public static void setDatabaseUrl(final String databaseUrl) {
		DBHandler.databaseUrl = databaseUrl;
	}

	/**
	 * @return The database URL
	 */
	public static String getDatabaseUrl() {
		return databaseUrl;
	}

	/**
	 * @return The students table's name
	 */
	public static String getStudentsTable() {
		return studentsTable;
	}

	/**
	 * @return The faculties table's name
	 */
	public static String getFacultiesTable() {
		return facultiesTable;
	}

	/**
	 * @return The courses table's name
	 */
	public static String getCoursesTable() {
		return coursesTable;
	}

	/**
	 * Checks if a certain table already exists in the database
	 * 
	 * @param tableName - Table's name that is wanted to be checked
	 * @return True if table exists, false otherwise
	 */
	public static boolean checkIfTableExists(final String tableName) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);

			// Check if a table with tableName name already exists
			DatabaseMetaData dbmData = connection.getMetaData();
			ResultSet resultSet = dbmData.getTables(null, null, tableName, null);
			while (resultSet.next()) {
				if (resultSet.getString(3).equals(tableName)) {
					// Return true if the table has been found
					return true;
				}
			}

			connection.close();
			resultSet.close();

			// Return false if no table has been found
			return false;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if an exception has been thrown
			return false;
		}
	}

	/**
	 * Creates a table of students, courses and faculties
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean createTables() {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			if (!checkIfTableExists(studentsTable)) {
				// Creating a table of students
				statement.executeUpdate("create table " + studentsTable + "(ID INTEGER not NULL AUTO_INCREMENT, "
						+ " Name varchar(50), " + "Surname varchar(50), " + "Age INTEGER, " + "Gender varchar(6), "
						+ "Course varchar(50), " + "Started varchar(25),  " + "Graduation varchar(25), "
						+ "PRIMARY KEY ( id ))");
			}

			if (!checkIfTableExists(coursesTable)) {
				// Creating a table of courses
				statement.executeUpdate("create table " + coursesTable + "(ID INTEGER not NULL AUTO_INCREMENT, "
						+ " Name varchar(50), " + "Faculty varchar(50), " + "Duration INTEGER, " + "Attendees INTEGER, "
						+ "PRIMARY KEY ( id ))");
			}

			if (!checkIfTableExists(facultiesTable)) {
				// Creating a table of faculties
				statement.executeUpdate("create table " + facultiesTable + "(ID INTEGER not NULL AUTO_INCREMENT, "
						+ " Name varchar(50), " + "Courses INTEGER, " + "Attendees INTEGER, " + "PRIMARY KEY ( id ))");
			}

			connection.close();
			statement.close();

			// Return true if no exception has been thrown
			return true;

		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if an exception has been thrown
			return false;
		}
	}

	/**
	 * Adds a new student to the table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean addStudent() {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("insert into " + studentsTable
					+ " (Name, Surname, Age, Gender, Course, Started, Graduation) values " + "(?, ?, ?, ?, ?, ?, ?)");

			// Getting the duration of the course in order to calculate Graduation date
			// field
			PreparedStatement preparedStatement2 = connection
					.prepareStatement("select Duration from Courses where Name = " + "\""
							+ ManagementView.courseSelectionBox.getSelectedItem().toString() + "\"");
			ResultSet resultSet = preparedStatement2.executeQuery();
			resultSet.next();
			final int courseDuration = resultSet.getInt("Duration");

			preparedStatement.setString(1, ManagementView.nameField.getText());
			preparedStatement.setString(2, ManagementView.surnameField.getText());
			preparedStatement.setInt(3, Integer.parseInt(ManagementView.ageField.getText()));
			preparedStatement.setString(4, ManagementView.genderSelectionBox.getSelectedItem().toString());
			preparedStatement.setString(5, ManagementView.courseSelectionBox.getSelectedItem().toString());

			final String inputDate = ManagementView.startedDateField.getText();
			LocalDate startedDate = LocalDate.of(Integer.parseInt(inputDate.substring(0, 4)),
					Integer.parseInt(inputDate.substring(5, 7)), Integer.parseInt(inputDate.substring(8, 10)));
			preparedStatement.setString(6, startedDate.toString());

			LocalDate graduationDate = startedDate.plusMonths(courseDuration);
			preparedStatement.setString(7, graduationDate.toString());

			preparedStatement.executeUpdate();

			connection.close();
			preparedStatement.close();

			updateStudents();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if an exception has been thrown
			return false;
		} catch (Exception e) {
			e.printStackTrace();

			// Return false if an exception has been thrown
			return false;
		}
	}

	/**
	 * Updates the contents of the table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean updateStudents() {
		int howManyColumns = 0, currentColumn = 0;

		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("select * from " + studentsTable);

			// Reading data from table
			ResultSet resultSet = preparedStatement.executeQuery();
			ResultSetMetaData rsmData = resultSet.getMetaData();

			howManyColumns = rsmData.getColumnCount();

			DefaultTableModel recordTable = (DefaultTableModel) ManagementView.table.getModel();
			recordTable.setRowCount(0);

			while (resultSet.next()) {
				Vector columnData = new Vector();

				for (currentColumn = 1; currentColumn <= howManyColumns; currentColumn++) {
					columnData.add(resultSet.getString("ID"));
					columnData.add(resultSet.getString("Name"));
					columnData.add(resultSet.getString("Age"));
					columnData.add(resultSet.getString("Gender"));
					columnData.add(resultSet.getString("department"));
					columnData.add(resultSet.getString("Course"));
					columnData.add(resultSet.getString("Batch"));
				}

				recordTable.addRow(columnData);
			}

			updateAttendees();

			connection.close();
			preparedStatement.close();
			resultSet.close();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		}
	}

	/**
	 * Deletes the selected student from the table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean deleteStudent() {
		// Getting row that user selected
		DefaultTableModel recordTable = (DefaultTableModel) ManagementView.table.getModel();
		int selectedRow = ManagementView.table.getSelectedRow();
		ManagementView.table.clearSelection();

		try {
			// Geting the ID of the student in the selected row
			final int ID = Integer.parseInt(recordTable.getValueAt(selectedRow, 0).toString());

			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection
					.prepareStatement("delete from " + studentsTable + " where id = ?");

			preparedStatement.setInt(1, ID);
			preparedStatement.executeUpdate();

			connection.close();
			preparedStatement.close();

			updateStudents();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		}
	}

	/**
	 * Adds a faculty to the faculties table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean addFaculty(final String facultyName) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement(
					"insert into " + facultiesTable + " (Name, Courses, Attendees) values " + "(?, ?, ?)");

			preparedStatement.setString(1, facultyName);
			preparedStatement.setInt(2, 0);
			preparedStatement.setInt(3, 0);

			preparedStatement.executeUpdate();

			connection.close();
			preparedStatement.close();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		}
	}

	/**
	 * Adds a course to the courses table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean addCourse(final String courseName, final String faculty, final int duration) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement(
					"insert into " + coursesTable + " (Name, Faculty, Duration, Attendees) values " + "(?, ?, ?, ?)");

			preparedStatement.setString(1, courseName);
			preparedStatement.setString(2, faculty);
			preparedStatement.setInt(3, duration);
			preparedStatement.setInt(4, 0);

			preparedStatement.executeUpdate();

			connection.close();
			preparedStatement.close();

			updateAttendees();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		}
	}

	/**
	 * Gets all the faculties from the faculties table
	 * 
	 * @return An array with all the faculties
	 */
	public static String[] getFaculties() {
		Vector<String> faculties = new Vector<String>();

		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("select Name from faculties");
			ResultSet resultSet = preparedStatement.executeQuery();

			// Add every name of the faculty to the "faculties" vector
			while (resultSet.next()) {
				faculties.add(resultSet.getString("Name"));
			}

			connection.close();
			preparedStatement.close();
			resultSet.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Convert "faculties" vector to String array and return it
		return faculties.toArray(new String[0]);
	}

	/**
	 * Gets all the courses from the courses table
	 * 
	 * @return An array with all the courses
	 */
	public static String[] getCourses() {
		Vector<String> courses = new Vector<String>();

		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("select Name from courses");
			ResultSet resultSet = preparedStatement.executeQuery();

			// Add every name of the courses to the "courses" vector
			while (resultSet.next()) {
				courses.add(resultSet.getString("Name"));
			}

			connection.close();
			preparedStatement.close();
			resultSet.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Convert "courses" vector to String array and return it
		return courses.toArray(new String[0]);
	}

	/**
	 * Updates the number of attendees in faculties and courses tables
	 */
	private static void updateAttendees() {
		updateCoursesAttendees();
		updateFacultiesAttendees();
	}

	/**
	 * Updates the number of attendees in courses table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	private static boolean updateCoursesAttendees() {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("select Course from " + studentsTable);
			Statement statement = connection.createStatement();

			// Setting number of courses and attendees to 0 initially, in order to avoid
			// wrong calculations
			statement.executeUpdate("update " + getCoursesTable() + " set Attendees = 0");

			// Reading courses that students attend from the table
			ResultSet resultSet = preparedStatement.executeQuery();
			HashMap<String, Integer> coursesAttendees = new HashMap<String, Integer>();

			// Calculating the number of attendees to the courses
			MAINLOOP: while (resultSet.next()) {
				String currentCourse = resultSet.getString("Course");

				for (String key : coursesAttendees.keySet()) {
					// If currentCourse is already in the HashMap, increment the value of attendees
					if (currentCourse.equals(key)) {
						coursesAttendees.put(key, coursesAttendees.get(key) + 1);
						continue MAINLOOP;
					}
				}

				coursesAttendees.put(currentCourse, 1);
			}

			// Update the number of attendees to the courses in the courses table
			for (String key : coursesAttendees.keySet()) {
				statement.executeUpdate("update " + coursesTable + " set Attendees = " + coursesAttendees.get(key)
						+ " where Name = " + "\"" + key + "\"");
			}

			connection.close();
			preparedStatement.close();
			statement.close();
			resultSet.close();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		}
	}

	/**
	 * Updates the number of attendees in faculties table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	private static boolean updateFacultiesAttendees() {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = null, preparedStatement2 = null;
			Statement statement = connection.createStatement();
			ResultSet resultSet = null, resultSet2 = null;

			// Setting number of courses and attendees to 0 initially, in order to avoid
			// wrong calculations
			statement.executeUpdate("update " + facultiesTable + " set Attendees = 0, Courses = 0");

			// Getting the faculties of courses and number of attendees
			preparedStatement = connection.prepareStatement("select Faculty, Attendees from " + coursesTable);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				final String faculty = resultSet.getString("Faculty");
				final int courseAttendees = resultSet.getInt("Attendees");

				preparedStatement2 = connection.prepareStatement(
						"select Attendees, Courses from " + facultiesTable + " where Name = " + "\"" + faculty + "\"");
				resultSet2 = preparedStatement2.executeQuery();

				resultSet2.next();
				final int currentNumberOfAttendees = resultSet2.getInt("Attendees");
				final int currentNumberOfCourses = resultSet2.getInt("Courses");

				statement.executeUpdate("update " + facultiesTable + " set Attendees = "
						+ (courseAttendees + currentNumberOfAttendees) + " where Name = " + "\"" + faculty + "\"");

				statement.executeUpdate("update " + facultiesTable + " set Courses = " + (currentNumberOfCourses + 1)
						+ " where Name = " + "\"" + faculty + "\"");
			}

			connection.close();
			preparedStatement.close();
			if (preparedStatement2 != null)
				preparedStatement2.close();
			resultSet.close();
			if (resultSet2 != null)
				resultSet2.close();
			statement.close();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Searches if there is already an element with a certain name in a certain table
	 * 
	 * @param tableName - The table in which user wants to check if element already
	 *                  exists
	 * @param name      - The name of the element user wants to check
	 * @return true if the element has been found, false otherwise
	 */
	public static boolean checkIfElementExists(final String tableName, final String name) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement("select Name from " + tableName);

			// Get all the elements' name
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				if (resultSet.getString("Name").equals(name)) {
					// Return true if an element has been found
					return true;
				}
			}

			connection.close();
			preparedStatement.close();
			resultSet.close();

			// Return false if no element has been found in the table
			return false;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if an exception has been thrown
			return false;
		}
	}

	/**
	 * Gets the number of attendees in a course or faculty
	 * 
	 * @param tableName - The table in which user wants to check the number of
	 *                  attendees(Faculties/Courses table)
	 * @param element   - The course/faculty name in which user wants to check the
	 *                  number of attendees
	 * @return The number of attendees in a faculty/course.
	 */
	public static int getNumberOfAttendees(final String tableName, final String element) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection
					.prepareStatement("select Attendees from " + tableName + " where Name = " + "\"" + element + "\"");

			// Get all the elements' name
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			int attendees = resultSet.getInt("Attendees");

			connection.close();
			preparedStatement.close();
			resultSet.close();

			return attendees;

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Deletes the students that attend a certain course
	 * 
	 * @param course - The course's name which attendees should be deleted
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean deleteCourseAttendees(final String course) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			statement.executeUpdate("delete from " + getStudentsTable() + " where Course = " + "\"" + course + "\"");

			updateStudents();

			connection.close();
			statement.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Deletes a course from the courses table
	 * 
	 * @param course - The course's name which should be deleted
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean deleteCourse(final String course) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			statement.executeUpdate("delete from " + getCoursesTable() + " where Name = " + "\"" + course + "\"");

			updateStudents();

			connection.close();
			statement.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Deletes a faculty from the faculties table
	 * 
	 * @param faculty - The faculty name which should be deleted
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean deleteFaculty(final String faculty) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			statement.executeUpdate("delete from " + getFacultiesTable() + " where Name = " + "\"" + faculty + "\"");

			updateStudents();

			connection.close();
			statement.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Deletes all the courses in a certain faculty
	 * 
	 * @param faculty - The faculty whose courses should be deleted
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean deleteFacultyCourses(final String faculty) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			// Getting the courses in that faculty, in order to delete students attending
			// them
			PreparedStatement preparedStatement = connection.prepareStatement(
					"select Name from " + getCoursesTable() + " where Faculty = " + "\"" + faculty + "\"");
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				deleteCourseAttendees(resultSet.getString("Name"));
			}

			// Deleting the course
			statement.executeUpdate("delete from " + getCoursesTable() + " where Faculty = " + "\"" + faculty + "\"");

			updateStudents();

			connection.close();
			statement.close();
			preparedStatement.close();
			resultSet.close();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Gets the number of courses in a faculty
	 * 
	 * @param faculty - The faculty's name whose number of courses should be read
	 * @return The number of courses in a faculty
	 */
	public static int getNumberOfCourses(final String faculty) {
		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			PreparedStatement preparedStatement = connection.prepareStatement(
					"select Courses from " + getFacultiesTable() + " where Name = " + "\"" + faculty + "\"");

			// Get Courses field's value
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			int courses = resultSet.getInt("Courses");

			connection.close();
			preparedStatement.close();

			return courses;

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Updates the contents of the database, taking into account changes from table
	 * 
	 * @return True if no exception has been thrown, false otherwise
	 */
	public static boolean updateDatabase() {
		// Getting row and column that user selected
		int selectedRow = ManagementView.table.getSelectedRow();
		int selectedColumn = ManagementView.table.getSelectedColumn();

		try {
			Connection connection = DriverManager.getConnection(databaseUrl, login, password);
			Statement statement = connection.createStatement();

			// If a cell has been selected
			if (selectedRow > -1 && selectedColumn > -1) {
				// Geting the selected field of the selected student and changing it in database
				if (selectedColumn == 1) {
					statement.executeUpdate("update " + studentsTable + " set Name = " + "\""
							+ ManagementView.table.getValueAt(selectedRow, selectedColumn).toString() + "\""
							+ " where id = "
							+ Integer.parseInt(ManagementView.table.getValueAt(selectedRow, 0).toString()));
				} else if (selectedColumn == 2) {
					statement.executeUpdate("update " + studentsTable + " set Surname = " + "\""
							+ ManagementView.table.getValueAt(selectedRow, selectedColumn).toString() + "\""
							+ " where id = "
							+ Integer.parseInt(ManagementView.table.getValueAt(selectedRow, 0).toString()));
				} else if (selectedColumn == 3) {
					statement.executeUpdate("update " + studentsTable + " set Age = "
							+ Integer.parseInt(ManagementView.table.getValueAt(selectedRow, selectedColumn).toString())
							+ " where id = "
							+ Integer.parseInt(ManagementView.table.getValueAt(selectedRow, 0).toString()));
				} else if (selectedColumn == 4) {
					statement.executeUpdate("update " + studentsTable + " set Gender = " + "\""
							+ ManagementView.table.getValueAt(selectedRow, selectedColumn).toString() + "\""
							+ " where id = "
							+ Integer.parseInt(ManagementView.table.getValueAt(selectedRow, 0).toString()));
				}
			}

			connection.close();
			statement.close();

			// Return true if no exception has been thrown
			return true;
		} catch (SQLException e) {
			e.printStackTrace();

			// Return false if exception has been thrown
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();

			return false;
		}
	}
}

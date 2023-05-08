package sms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * The class that holds the front-end table-management part of the application
 * and manages the actions performed out there
 * 
 * @author Artiom
 *
 */
public class ManagementView {

	/**
	 * The contents of the management window where you read and write students data
	 */
	static JFrame managementFrame;

	/**
	 * The table containing all students
	 */
	static JTable table;

	/**
	 * The field where user should write the student's name
	 */
	static JTextField nameField;

	/**
	 * The field where user should write the student's surname
	 */
	static JTextField surnameField;

	/**
	 * The field where user should write the student's age
	 */
	static JTextField ageField;

	/**
	 * The field where user should write the date when the student started attending
	 * the course
	 */
	static JTextField startedDateField;

	/**
	 * The box that user uses in order to select student's gender
	 */
	static JComboBox genderSelectionBox;

	/**
	 * The box that allows user to select a course for a student
	 */
	static JComboBox courseSelectionBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Reading messages in dependance of the selected language(by default ENG)
				Translator.getMessagesFromXML();

				try {
					ManagementView window = new ManagementView();
					window.managementFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ManagementView() {
		initialize();
		// Clear the selection in the table, to avoid issues with updateDatabase method
		// when cells are selected
		table.clearSelection();
		// Make it visible in constructor, in order to make tests in
		// ManagementViewTest.java work
		managementFrame.setVisible(true);
		DBHandler.updateStudents();
	}

	/**
	 * Updates the list of courses
	 */
	private void updateCourses() {
		// Get the lists of courses
		DefaultComboBoxModel courses = new DefaultComboBoxModel(DBHandler.getCourses());
		courseSelectionBox.setModel(courses);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		managementFrame = new JFrame();
		managementFrame.setBounds(100, 100, 860, 540);
		managementFrame.setResizable(false);
		managementFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		managementFrame.setTitle(Translator.getValue("sms"));
		managementFrame.getContentPane().setLayout(null);

		// The panel where students table is located
		JPanel tablePanel = new JPanel();
		tablePanel.setBorder(new LineBorder(SystemColor.textHighlight, 5));
		tablePanel.setBounds(260, 10, 575, 395);
		managementFrame.getContentPane().add(tablePanel);
		tablePanel.setLayout(null);

		// The scroll pane that allows navigation through table
		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(10, 10, 555, 375);
		tablePanel.add(tableScrollPane);

		// Initializing the table and setting its model
		table = new JTable();
		tableScrollPane.setViewportView(table);
		table.setColumnSelectionAllowed(true);
		table.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { Translator.getValue("ID"), Translator.getValue("name"), Translator.getValue("surname"),
						Translator.getValue("age"), Translator.getValue("gender"), Translator.getValue("course"),
						Translator.getValue("started"), Translator.getValue("graduation") }) {
			boolean[] columnEditables = new boolean[] { false, true, true, true, true, false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});

		// Creating a sorter for the table
		TableRowSorter tableSorter = new TableRowSorter(table.getModel());
		table.setRowSorter(tableSorter);

		// Creating a Table Listener to detect cell modifications
		table.getModel().addTableModelListener(new TableModelListener() {

			// Actions to perform when a cell has been edited
			public void tableChanged(TableModelEvent e) {
				if (!DBHandler.updateDatabase()) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("checkInput"),
							Translator.getValue("sms"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// The panel where all buttons are located
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder(new LineBorder(new Color(0, 120, 215), 5));
		buttonsPanel.setBackground(UIManager.getColor("Button.background"));
		buttonsPanel.setBounds(10, 415, 825, 80);
		managementFrame.getContentPane().add(buttonsPanel);

		// The button to press to delete an information from the table
		JButton deleteButton = new JButton(Translator.getValue("delete"));
		deleteButton.setName("deleteButton");

		// Actions to perform when "delete" button clicked
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If no row has been selected
				if (table.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("noStudentSelected"),
							Translator.getValue("sms"), JOptionPane.ERROR_MESSAGE);
				} else {
					// Asking the user if they are sure about that
					if (JOptionPane.showConfirmDialog(managementFrame, Translator.getValue("warningDelete"),
							Translator.getValue("sms"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						if (DBHandler.deleteStudent()) {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("studentSuccessfullyDeleted"), Translator.getValue("sms"),
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("somethingWrongUnexpected"), Translator.getValue("sms"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		deleteButton.setFont(new Font("Tahoma", Font.PLAIN, 16));

		// The button to press to add a student to the table
		JButton addButton = new JButton(Translator.getValue("add"));
		addButton.setName("addButton");

		// Actions to perform when "add" button clicked
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.clearSelection();

				if (DBHandler.getFaculties().length == 0) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("cannotAddStudent"),
							Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					return;
				}

				// If one of the fields are empty warn user about that
				if (nameField.getText().equals("") || surnameField.getText().equals("") || ageField.getText().equals("")
						|| startedDateField.getText().equals("")) {

					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("fillEmptyFields"),
							Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						// Check if the written data is written correctly according to the format
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						format.setLenient(false);
						format.parse(startedDateField.getText());
					} catch (ParseException ex) {
						ex.printStackTrace();

						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("dateFormatError"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);

						return;
					}

					if (DBHandler.addStudent()) {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("studentSuccessfullyAdded"),
								Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("somethingWrongInput"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});
		buttonsPanel.setLayout(new GridLayout(0, 5, 0, 0));

		addButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonsPanel.add(addButton);

		// The button to press to update an information in the table
		JButton updateButton = new JButton(Translator.getValue("update"));

		// Actions to perform when "update" button clicked
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.clearSelection();
				DBHandler.updateStudents();
			}
		});

		updateButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonsPanel.add(updateButton);
		buttonsPanel.add(deleteButton);

		// The button to press to exit the application
		JButton exitButton = new JButton(Translator.getValue("exit"));
		exitButton.setName("exitButton");

		// Actions to perform when "exit" button clicked
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(managementFrame, Translator.getValue("confirmDialog"),
						Translator.getValue("sms"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					managementFrame.dispose();
					System.exit(0);
				}
			}
		});

		// The button that user have to press in order to disconnect from the current
		// database
		JButton disconnectButton = new JButton(Translator.getValue("disconnect"));
		disconnectButton.setName("disconnectButton");

		// Actions to perform when "disconnect" button has been clicked
		disconnectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(managementFrame, Translator.getValue("confirmDialog"),
						Translator.getValue("sms"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					// Return back to the connection window
					ConnectionView.main(null);
					managementFrame.dispose();
				}
			}
		});

		disconnectButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonsPanel.add(disconnectButton);

		exitButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonsPanel.add(exitButton);

		// The panel where user writes information about a student
		JPanel studentPanel = new JPanel();
		studentPanel.setBorder(new LineBorder(SystemColor.textHighlight, 5));
		studentPanel.setBounds(10, 10, 240, 395);
		managementFrame.getContentPane().add(studentPanel);
		studentPanel.setLayout(null);

		// The text that informs the user where they have to write the student's name
		JLabel nameText = new JLabel(Translator.getValue("name"));
		nameText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		nameText.setBounds(10, 22, 67, 19);
		studentPanel.add(nameText);

		// Initializing name text field
		nameField = new JTextField();
		nameField.setName("nameField");
		nameField.setBounds(85, 23, 143, 22);
		studentPanel.add(nameField);
		nameField.setColumns(10);

		// The text that informs the user where they have to write the student's surname
		JLabel surnameText = new JLabel(Translator.getValue("surname"));
		surnameText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		surnameText.setBounds(10, 54, 67, 19);
		studentPanel.add(surnameText);

		// Initializing surname text field
		surnameField = new JTextField();
		surnameField.setName("surnameField");
		surnameField.setColumns(10);
		surnameField.setBounds(85, 51, 143, 22);
		studentPanel.add(surnameField);

		// The text that informs the user where they have to write the student's age
		JLabel ageText = new JLabel(Translator.getValue("age"));
		ageText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		ageText.setBounds(10, 86, 67, 19);
		studentPanel.add(ageText);

		// Initializing age text field
		ageField = new JTextField();
		ageField.setName("ageField");
		ageField.setColumns(10);
		ageField.setBounds(85, 83, 143, 22);
		studentPanel.add(ageField);

		// The text that informs the user where they have to write the student's
		// attended course
		JLabel courseText = new JLabel(Translator.getValue("course"));
		courseText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		courseText.setBounds(10, 156, 67, 19);
		studentPanel.add(courseText);

		// The text that informs the user where they have to write the date when student
		// started attending the course
		JLabel startedDateText = new JLabel(Translator.getValue("started"));
		startedDateText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		startedDateText.setBounds(10, 188, 67, 19);
		studentPanel.add(startedDateText);

		// Initializing startedDate text field
		startedDateField = new JTextField();
		startedDateField.setName("startedDateField");
		startedDateField.setColumns(10);
		startedDateField.setBounds(85, 185, 143, 22);
		startedDateField.setText(Translator.getValue("dateFormat"));
		studentPanel.add(startedDateField);

		// The text that informs the user where they have to select student's gender
		JLabel genderText = new JLabel(Translator.getValue("gender"));
		genderText.setFont(new Font("Tahoma", Font.PLAIN, 16));
		genderText.setBounds(10, 120, 67, 19);
		studentPanel.add(genderText);

		// Initializing the box where user selects the student's gender
		genderSelectionBox = new JComboBox();
		genderSelectionBox.setName("genderSelectionBox");
		genderSelectionBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		// genderSelectionBox.setModel(new DefaultComboBoxModel(sms.Gender.values()));
		genderSelectionBox.setBounds(85, 120, 143, 22);
		studentPanel.add(genderSelectionBox);

		// Button that adds a new faculty
		JButton addFacultyButton = new JButton(Translator.getValue("addFaculty"));
		addFacultyButton.setName("addFacultyButton");

		// Actions to perform when "add faculty" button clicked
		addFacultyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String facultyName = "";

				facultyName = JOptionPane.showInputDialog(managementFrame, Translator.getValue("typeNameFaculty"));

				if (facultyName == null || facultyName.equals("")) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("emptyNameFaculty"),
							Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
				} else {
					if (DBHandler.checkIfElementExists(DBHandler.getFacultiesTable(), facultyName)) {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("facultyAlreadyExists"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					} else {
						if (DBHandler.addFaculty(facultyName)) {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("facultySuccessfullyAdded"), Translator.getValue("success"),
									JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(managementFrame, Translator.getValue("facultyNotAdded"),
									Translator.getValue("success"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		addFacultyButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addFacultyButton.setBounds(10, 220, 220, 30);
		studentPanel.add(addFacultyButton);

		// Button that adds a new course
		JButton addCourseButton = new JButton(Translator.getValue("addCourse"));
		addCourseButton.setName("addCourseButton");
		addCourseButton.addActionListener(new ActionListener() {

			// Actions to perform when "add course" button clicked
			public void actionPerformed(ActionEvent e) {
				// If there are no faculties there is no way to add a course
				if (DBHandler.getFaculties().length == 0) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("cannotAddCourse"),
							Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					return;
				}

				String courseName = "", faculty = "";
				int duration = 0;

				courseName = JOptionPane.showInputDialog(managementFrame, Translator.getValue("typeNameCourse"));

				// If no name has been written for the course
				if (courseName == null || courseName.equals("")) {
					JOptionPane.showMessageDialog(managementFrame, Translator.getValue("emptyNameCourse"),
							Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					String[] faculties = DBHandler.getFaculties();
					faculty = (String) JOptionPane.showInputDialog(null, Translator.getValue("chooseFaculty"),
							Translator.getValue("sms"), JOptionPane.QUESTION_MESSAGE, null, faculties, faculties[0]);

					// If no faculty has been selected for the course
					if (faculty == null || faculty.equals("")) {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("courseNotAddedNoFaculty"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						// In case the user types letters for the duration
						try {
							duration = Integer.parseInt(JOptionPane.showInputDialog(managementFrame,
									Translator.getValue("courseTypeDuration")));
						} catch (NumberFormatException ex) {
							ex.printStackTrace();

							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("courseNotAddedNoDuration"), Translator.getValue("error"),
									JOptionPane.ERROR_MESSAGE);

							return;
						}

						if (DBHandler.checkIfElementExists(DBHandler.getCoursesTable(), courseName)) {
							JOptionPane.showMessageDialog(managementFrame, Translator.getValue("courseAlreadyExists"),
									Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
						} else {
							if (DBHandler.addCourse(courseName, faculty, duration)) {
								JOptionPane.showMessageDialog(managementFrame,
										Translator.getValue("courseSuccessfullyAdded"), Translator.getValue("success"),
										JOptionPane.INFORMATION_MESSAGE);

								updateCourses();
							} else {
								JOptionPane.showMessageDialog(managementFrame, Translator.getValue("courseNotAdded"),
										Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});

		addCourseButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		addCourseButton.setBounds(10, 260, 220, 30);
		studentPanel.add(addCourseButton);

		// Initializing the course selection box
		courseSelectionBox = new JComboBox();
		courseSelectionBox.setFont(new Font("Tahoma", Font.PLAIN, 16));
		courseSelectionBox.setBounds(85, 154, 143, 22);
		updateCourses();
		studentPanel.add(courseSelectionBox);

		// Button that allows to delete a faculty
		JButton deleteFacultyButton = new JButton(Translator.getValue("deleteFaculty"));
		deleteFacultyButton.setName("deleteFacultyButton");

		// Actions to perform when "Delete Faculty" button clicked
		deleteFacultyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.clearSelection();

				String faculty = (String) JOptionPane.showInputDialog(null, Translator.getValue("sms"),
						Translator.getValue("chooseFacultyDelete"), JOptionPane.QUESTION_MESSAGE, null,
						DBHandler.getFaculties(), DBHandler.getFaculties()[0]);

				// If no faculty has been selected
				if (faculty == null) {
					return;
				}

				// If there are students attending the courses in this faculty
				if (DBHandler.getNumberOfCourses(faculty) > 0) {
					if (JOptionPane.showConfirmDialog(managementFrame, Translator.getValue("deleteFacultyWithCourses"),
							Translator.getValue("sms"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						if (DBHandler.deleteFacultyCourses(faculty)) {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("coursesFromFacultySuccessfullyDeleted"),
									Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);

							if (DBHandler.deleteFaculty(faculty)) {
								JOptionPane.showMessageDialog(managementFrame, Translator.getValue("facultyDeleted"),
										Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(managementFrame,
										Translator.getValue("somethingWrongTryAgain"), Translator.getValue("error"),
										JOptionPane.ERROR_MESSAGE);
							}

						} else {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("somethingWrongTryAgain"), Translator.getValue("error"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					if (DBHandler.deleteFaculty(faculty)) {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("facultyDeleted"),
								Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("somethingWrongTryAgain"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					}
				}
				updateCourses();
			}
		});

		deleteFacultyButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		deleteFacultyButton.setBounds(10, 300, 220, 30);
		studentPanel.add(deleteFacultyButton);

		// Button that allows to delete a course
		JButton deleteCourseButton = new JButton(Translator.getValue("deleteCourse"));
		deleteCourseButton.setName("deleteCourseButton");

		// Actions to perform when "Delete Course" button clicked
		deleteCourseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				table.clearSelection();

				String course = (String) JOptionPane.showInputDialog(null, Translator.getValue("sms"),
						Translator.getValue("chooseCourseDelete"), JOptionPane.QUESTION_MESSAGE, null,
						DBHandler.getCourses(), DBHandler.getCourses()[0]);

				// If no course has been selected
				if (course == null) {
					return;
				}

				// If there are students attending the course
				if (DBHandler.getNumberOfAttendees(DBHandler.getCoursesTable(), course) > 0) {
					if (JOptionPane.showConfirmDialog(managementFrame, Translator.getValue("deleteCourseWithStudents"),
							Translator.getValue("deleteCourse"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						if (DBHandler.deleteCourseAttendees(course)) {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("studentsAttendingSuccessfullyDeleted"),
									Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);

							if (DBHandler.deleteCourse(course)) {
								JOptionPane.showMessageDialog(managementFrame, Translator.getValue("courseDeleted"),
										Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(managementFrame,
										Translator.getValue("somethingWrongTryAgain"), "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(managementFrame,
									Translator.getValue("somethingWrongTryAgain"), Translator.getValue("error"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					if (DBHandler.deleteCourse(course)) {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("courseDeleted"),
								Translator.getValue("success"), JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(managementFrame, Translator.getValue("somethingWrongTryAgain"),
								Translator.getValue("error"), JOptionPane.ERROR_MESSAGE);
					}
				}
				updateCourses();
			}
		});

		deleteCourseButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		deleteCourseButton.setBounds(10, 340, 220, 30);
		studentPanel.add(deleteCourseButton);
	}
}
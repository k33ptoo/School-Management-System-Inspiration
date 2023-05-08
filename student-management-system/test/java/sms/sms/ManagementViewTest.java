package sms;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class that tests ManagementView class' GUI
 * 
 * @author Artiom
 *
 */
public class ManagementViewTest {

	private FrameFixture managementFrame;
	private ManagementView managementView;

	@Before
	public void setUp() {
		managementView = new ManagementView();
		managementFrame = new FrameFixture(ManagementView.managementFrame);
	}

	@After
	public void tearDown() {
		managementFrame.cleanUp();
	}

	@Test
	public void emptyFieldsTest() {
		managementFrame.button("addButton").click();
		if (DBHandler.getCourses().length > 0)
			managementFrame.optionPane().requireErrorMessage().requireMessage("Please fill in all the fields!");
		else
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a student yet!\nAdd a faculty and a course first!");
	}

	@Test
	public void deleteWithoutSelection() {
		managementFrame.button("deleteButton").click();
		managementFrame.optionPane().requireErrorMessage().requireMessage("No student has been selected!");
	}

	@Test
	public void disconnectWarning() {
		managementFrame.button("disconnectButton").click();
		managementFrame.optionPane().requireMessage("Do you want to disconnect from the current database?");
	}

	@Test
	public void addCourseSuccessfully() {
		if (DBHandler.checkIfElementExists(DBHandler.getCoursesTable(), "NewCourse"))
			DBHandler.deleteCourse("NewCourse");

		managementFrame.button("addCourseButton").click();
		if (DBHandler.getFaculties().length > 0) {
			managementFrame.optionPane().requireMessage("Type the name of the course");
			managementFrame.optionPane().textBox().enterText("NewCourse");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().textBox().enterText("12");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().requireMessage("The course has been added successfully!");
		} else
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a course!\nAdd a faculty first");

	}

	@Test
	public void addCourseWithoutName() {
		managementFrame.button("addCourseButton").click();
		if (DBHandler.getFaculties().length > 0) {
			managementFrame.optionPane().requireMessage("Type the name of the course");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().requireMessage("The course hasn't been added!\nPlease type a name for it!");
		} else
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a course!\nAdd a faculty first");

	}

	@Test
	public void addCourseWithWrongDuration() {
		managementFrame.button("addCourseButton").click();
		if (DBHandler.getFaculties().length > 0) {
			managementFrame.optionPane().requireMessage("Type the name of the course");
			managementFrame.optionPane().textBox().enterText("NewCourse");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().textBox().enterText("one year");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().requireMessage(
					"The course hasn't been added!\nPlease type the duration of the course without using other characters than digits!");
		} else
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a course!\nAdd a faculty first");

	}

	@Test
	public void addCourseThatExists() {
		if (!DBHandler.checkIfElementExists(DBHandler.getCoursesTable(), "CourseAlreadyExists"))
			DBHandler.addCourse("CourseAlreadyExists", "Automatica", 12);

		managementFrame.button("addCourseButton").click();
		if (DBHandler.getFaculties().length > 0) {
			managementFrame.optionPane().requireMessage("Type the name of the course");
			managementFrame.optionPane().textBox().enterText("CourseAlreadyExists");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().textBox().enterText("12");
			managementFrame.optionPane().buttonWithText("OK").click();
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("The course hasn't been added!\nThe course CourseAlreadyExists already exists!");
		} else
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a course!\nAdd a faculty first");

	}

	@Test
	public void addFacultyThatExists() {
		if (!DBHandler.checkIfElementExists(DBHandler.getFacultiesTable(), "FacultyAlreadyExists"))
			DBHandler.addFaculty("FacultyAlreadyExists");

		managementFrame.button("addFacultyButton").click();
		managementFrame.optionPane().requireMessage("Type the name of the faculty");
		managementFrame.optionPane().textBox().enterText("FacultyAlreadyExists");
		managementFrame.optionPane().buttonWithText("OK").click();
		managementFrame.optionPane().requireErrorMessage()
				.requireMessage("The faculty hasn't been added!\nThe faculty FacultyAlreadyExists already exists!");
	}

	@Test
	public void addFacultySuccessfully() {
		if (DBHandler.checkIfElementExists(DBHandler.getFacultiesTable(), "NewFaculty"))
			DBHandler.deleteFaculty("NewFaculty");

		managementFrame.button("addFacultyButton").click();
		managementFrame.optionPane().requireMessage("Type the name of the faculty");
		managementFrame.optionPane().textBox().enterText("NewFaculty");
		managementFrame.optionPane().buttonWithText("OK").click();
		managementFrame.optionPane().requireMessage("The faculty has been added successfully!");
	}

	@Test
	public void addStudentSuccessfully() {
		if (DBHandler.getFaculties().length == 0)
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a student yet!\nAdd a faculty and a course first!");
		else {
			managementFrame.textBox("nameField").enterText("Walter");
			managementFrame.textBox("surnameField").enterText("White");
			managementFrame.textBox("ageField").enterText("52");
			managementFrame.textBox("startedDateField").deleteText();
			managementFrame.textBox("startedDateField").enterText("2021-04-10");
			managementFrame.button("addButton").click();
			managementFrame.optionPane().requireMessage("The student has been added successfully!");
		}
	}

	@Test
	public void addStudentWithWrongAge() {
		if (DBHandler.getFaculties().length == 0)
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a student yet!\nAdd a faculty and a course first!");
		else {
			managementFrame.textBox("nameField").enterText("Walter");
			managementFrame.textBox("surnameField").enterText("White");
			managementFrame.textBox("ageField").enterText("haerga");
			managementFrame.textBox("startedDateField").deleteText();
			managementFrame.textBox("startedDateField").enterText("2021-04-10");
			managementFrame.button("addButton").click();
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("Something went wrong!\nCheck the credentials!");
		}
	}

	@Test
	public void addStudentWithWrongStartedDate() {
		if (DBHandler.getFaculties().length == 0)
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("You can't add a student yet!\nAdd a faculty and a course first!");
		else {
			managementFrame.textBox("nameField").enterText("Walter");
			managementFrame.textBox("surnameField").enterText("White");
			managementFrame.textBox("ageField").enterText("52");
			managementFrame.textBox("startedDateField").deleteText();
			managementFrame.textBox("startedDateField").enterText("hraeha");
			managementFrame.button("addButton").click();
			managementFrame.optionPane().requireErrorMessage()
					.requireMessage("Please type a correct data according to the format YYYY-MM-DD!");
		}
	}

}

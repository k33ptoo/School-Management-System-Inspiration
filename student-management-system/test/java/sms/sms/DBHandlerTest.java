package sms;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

/**
 * The class that tests DBHandler class' methods
 * 
 * @author Artiom
 *
 */
public class DBHandlerTest {

	public DBHandlerTest() {
		// In case I set a password for the database
		// DBHandler.setPassword("password");
	}

	@Before
	public void init() {
		// Create tables in order to test methods that modifies them
		DBHandler.createTables();
	}

	@Test
	public void checkIfTableExistsTest() throws SQLException {

		boolean result = DBHandler.checkIfTableExists("Test");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("students");
		assertEquals(true, result);

		result = DBHandler.checkIfTableExists("courses");
		assertEquals(true, result);

		result = DBHandler.checkIfTableExists("faculties");
		assertEquals(true, result);

		result = DBHandler.checkIfTableExists("Students");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("coures");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("5161521");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("=-/*-+");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("");
		assertEquals(false, result);

		result = DBHandler.checkIfTableExists("   ");
		assertEquals(false, result);

	}

	@Test
	public void addFacultyTest() throws SQLException {
		boolean result = DBHandler.addFaculty("Test");
		assertEquals(true, result);

		result = DBHandler.addFaculty("students");
		assertEquals(true, result);

		result = DBHandler.addFaculty("automatica");
		assertEquals(true, result);

		result = DBHandler.addFaculty("calculatoare");
		assertEquals(true, result);

		result = DBHandler.addFaculty("electronica");
		assertEquals(true, result);

		result = DBHandler.addFaculty("y y y y y");
		assertEquals(true, result);

		result = DBHandler.addFaculty("5161521");
		assertEquals(true, result);

		result = DBHandler.addFaculty("=-/*-+");
		assertEquals(true, result);

		result = DBHandler.addFaculty("");
		assertEquals(true, result);

		result = DBHandler.addFaculty("   ");
		assertEquals(true, result);
	}

	@Test
	public void addCourse() throws SQLException {
		boolean result = DBHandler.addCourse("matematica", "Test", 12);
		assertEquals(true, result);

		result = DBHandler.addCourse("bigint", "students", 999999999);
		assertEquals(true, result);

		result = DBHandler.addCourse("negativeint", "automatica", -9515141);
		assertEquals(true, result);

		result = DBHandler.addCourse("zero", "calculatoare", 0);
		assertEquals(true, result);

		result = DBHandler.addCourse("fizica", "electronica", 6);
		assertEquals(true, result);
	}
}

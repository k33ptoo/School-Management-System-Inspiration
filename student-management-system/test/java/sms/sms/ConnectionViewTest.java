package sms;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class that tests ConnectionView class' GUI
 * 
 * @author Artiom
 *
 */
public class ConnectionViewTest {

	private FrameFixture connectionFrame;
	private ConnectionView connectionView;

	@Before
	public void setUp() {
		connectionView = new ConnectionView();
		connectionFrame = new FrameFixture(ConnectionView.connectionFrame);
	}

	@After
	public void tearDown() {
		connectionFrame.cleanUp();
	}

	@Test
	public void emptyFieldsTest() {
		connectionFrame.button("connectButton").click();
		connectionFrame.optionPane().requireErrorMessage().requireMessage("Please fill in all the empty fields!");
	}

	@Test
	public void wrongDatabaseUrl() {
		connectionFrame.textBox("loginField").enterText("root");
		connectionFrame.textBox("passwordField").enterText("simplepassword123");
		connectionFrame.textBox("databaseUrlField").enterText("dawgfaea");
		connectionFrame.button("connectButton").click();
		connectionFrame.optionPane().requireErrorMessage().requireMessage(
				"Connection with the database hasn't been established!\nPlease check your credentials!");
	}

	@Test
	public void correctCredentials() {
		connectionFrame.textBox("loginField").enterText("root");
		connectionFrame.button("connectButton").click();
		connectionFrame.optionPane().requireMessage("Connection with the database has been successfully established!");
	}
}

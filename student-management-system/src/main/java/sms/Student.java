package sms;

import java.util.Date;

enum Gender {
	Male, Female
}

/**
 * The class that holds and manages the information about students
 * 
 * @author Artiom
 *
 */
public class Student {
	/**
	 * The name of the student
	 */
	private String name;

	/**
	 * The surname of the student
	 */
	private String surname;

	/**
	 * The age of the student
	 */
	private int age;

	/**
	 * The gender of the student
	 */
	private Gender gender;

	/**
	 * The course that the student attends
	 */
	private String course;

	/**
	 * The date when the student started the course
	 */
	private Date started;

	/**
	 * The date when the student started the course
	 */
	private Date graduation;

	/**
	 * The unique id of the student
	 */
	int id;

	/**
	 * The counter that is increased by one everytime a new instance is created,
	 * used for ID system
	 */
	static int counter;

	/**
	 * Counter is set to 1 initially
	 */
	static {
		counter = 1;
	}

	/**
	 * Default constructor
	 */
	public Student() {
		// The unique id of the student is set to value of the counter
		this.id = counter;
		// The counter after that is increased by one
		counter++;
	}

	/**
	 * @return The name of the student
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name - The name to set to the student
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The surname of the student
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param surname - The surname to set to the student
	 */
	public void setSurname(final String surname) {
		this.surname = surname;
	}

	/**
	 * @return The age of the student
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age - The age to set to the student
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * @return The course that the student attends
	 */
	public String getCourse() {
		return course;
	}

	/**
	 * @param course - The course to set to the student
	 */
	public void setCourse(final String course) {
		this.course = course;
	}

	/**
	 * @return The id of the student
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return The date when student started the course
	 */
	public Date getStarted() {
		return started;
	}

	/**
	 * @param started - The date when student started attending the course
	 */
	public void setStarted(final Date started) {
		this.started = started;
	}

	/**
	 * @return The gender of the student
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender - The gender to set to the student
	 */
	public void setGender(final Gender gender) {
		this.gender = gender;
	}

	/**
	 * @return the date when student will graduate
	 */
	public Date getGraduation() {
		return graduation;
	}

	/**
	 * @param graduation - The graduation's date to set to the student
	 */
	public void setGraduation(final Date graduation) {
		this.graduation = graduation;
	}
}

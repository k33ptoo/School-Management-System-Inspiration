CREATE DATABASE SMS

CREATE TABLE student(
	s_id INT(3) ZEROFILL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  age INT NOT NULL,
  gender enum('m','f') NOT NULL,
  batch year NOT NULL
)

CREATE TABLE faculty (
    id int(11) NOT NULL AUTO_INCREMENT,
    dept_name varchar(20) NOT NULL,
    building varchar(20) NOT NULL,
  	PRIMARY KEY (f_id,f_name)
  )
  
  CREATE TABLE departement (
    dept_name varchar(20) NOT NULL PRIMARY KEY,
  	building varchar(20) NOT NULL,
  )
  
CREATE TABLE course (
  c_id char(4) NOT NULL PRIMARY KEY ,
  title varchar(30) NOT NULL,
  f_name varchar(20) NOT NULL,
  credits int(11) DEFAULT NULL,
  FOREIGN KEY (f_name) REFERENCES faculty(f_name)
  )


# SET FOREIGN_KEY_CHECKS = 0;
# drop table if exists course;
# drop table if exists faculty;
# drop table if exists student;
# SET FOREIGN_KEY_CHECKS = 1;
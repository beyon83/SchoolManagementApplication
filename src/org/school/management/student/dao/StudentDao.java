package org.school.management.student.dao;

import java.util.List;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Student;
import org.school.management.model.Subject;
import org.school.management.model.Teacher;

public interface StudentDao extends GenericDao<Student, Long> {

	List<Student> getStudentsByTeacher(String teacherName);
	Student getStudentWithSubjects(long id);
	Student getStudentByName(String username);
	Student getStudentByTeacher(Teacher teacher, String studentUsername);
	List<Subject> getSubjectsByTeacher(Teacher teacher);
	
}

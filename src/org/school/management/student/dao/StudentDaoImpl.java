package org.school.management.student.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Student;
import org.school.management.model.Subject;
import org.school.management.model.Teacher;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDaoImpl extends GenericDaoImpl<Student, Long> implements StudentDao {
	
	public StudentDaoImpl() {
		super(Student.class);
	}

	@Override
	public List<Student> getStudentsByTeacher(String teacherName) {
		
		/** Get collection of all registered students */
		Collection<Student> allStudents = getAllEntities(); 
		
		/** Here add students only bounded to this teacher */
		List<Student> studentsByTeacher = new ArrayList<>();
		
		/** Loop through all students in collection */
		for(Student student : allStudents) { 
			Hibernate.initialize(student.getSubjects());
			for(int i = 0; i < student.getTeachers().size(); i++) {
				/** Compare passed argument's teacher's name with teacher names bounded with students */
				if(teacherName.equals(student.getTeachers().get(i).getUsername())) {
					studentsByTeacher.add(student);
				}
			}
		}
		
		return studentsByTeacher;
	}
	
	@Override
	public List<Subject> getSubjectsByTeacher(Teacher teacher) {
		
		List<Subject> subjects = new ArrayList<>();
		
		for(Subject subject : teacher.getSubjects()) {
			Hibernate.initialize(teacher.getSubjects());
			subjects.add(subject);
		}
		
		return subjects;
	}
	
	@Override
	public Student getStudentByTeacher(Teacher teacher, String studentUsername) {
		List<Student> students = teacher.getStudents();
		Student student = new Student();
		for(int i = 0; i < students.size(); i++) {
			if(students.get(i).getUsername().equals(studentUsername)) {
				student = students.get(i);
			}
		}
		return student;
	}
	
	@Override
	public Student getStudentWithSubjects(long id) {
		Student student = getEntityById(id);
		Hibernate.initialize(student.getSubjects());
		Hibernate.initialize(student.getTeachers());
		Hibernate.initialize(student.getGrades());
		return student;
	}

	@Override
	public Student getStudentByName(String username) {
		Query query = getSession().createQuery("FROM Student WHERE username = :username");
		query.setParameter("username", username);
		
		Student student = (Student) query.uniqueResult();
		Hibernate.initialize(student.getTeachers());
		
		for(Subject subject : student.getSubjects()) {
			Hibernate.initialize(subject.getSubjectTitle());
		}
		
		Hibernate.initialize(student.getGrades());
		
		return student;
	}

}

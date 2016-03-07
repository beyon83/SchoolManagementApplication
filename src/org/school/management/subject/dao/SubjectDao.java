package org.school.management.subject.dao;

import java.util.List;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Student;
import org.school.management.model.Subject;

public interface SubjectDao extends GenericDao<Subject, Long> {
	
	List<Subject> getAllSubjects();

	Subject getSubjectByTitle(String subjectTitle);

	Subject getSubjectById(long subjectId);
	
	List<String> getUnattendedSubjects(Student student);

	List<String> getUnassignedSubjectsForTeacher();
	
}

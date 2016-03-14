package org.school.management.subject.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Student;
import org.school.management.model.Subject;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectDaoImpl extends GenericDaoImpl<Subject, Long> implements SubjectDao {

	public SubjectDaoImpl() {
		super(Subject.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Subject> getAllSubjects() {
		Query query = getSession().createQuery("FROM Subject");
		List<Subject> subjects = query.list();
		for(Subject subject : subjects) {
			Hibernate.initialize(subject.getSubjectTitle());
			Hibernate.initialize(subject.getStudents());
		}
		return subjects;
	}

	@Override
	public Subject getSubjectByTitle(String subjectTitle) {
		Query query = getSession().createQuery("FROM Subject WHERE subjectTitle = :subjectTitle");
		query.setParameter("subjectTitle", subjectTitle);
		Subject subject = (Subject) query.uniqueResult();
		Hibernate.initialize(subject.getStudents());
		return subject;
	}

	@Override
	public Subject getSubjectById(long subjectId) {
		Subject subject = getEntityById(subjectId);
		Hibernate.initialize(subject.getStudents());
		return subject;
	}

	@Override
	public List<String> getUnattendedSubjects(Student student) {
		
		List<Subject> allSubjects = getAllSubjects();
		
		ArrayList<String> s1 = new ArrayList<>();
		ArrayList<String> s2 = new ArrayList<>();
		
		for(Subject s : student.getSubjects()) {
			s1.add(s.getSubjectTitle());
		}
		
		for(Subject s : allSubjects) {
			s2.add(s.getSubjectTitle());
		}
		
		ArrayList<String> s3 = new ArrayList<>(s2);
		s3.removeAll(s1);
		
		return s3;
	}
	
	@Override
	public List<String> getUnassignedSubjectsForTeacher() {
		
		List<Subject> allSubjects = getAllSubjects();
		
		ArrayList<String> unassignedSubjects = new ArrayList<>();
		
		for(Subject subject : allSubjects) {
			if(subject.getTeacher() == null) {
				unassignedSubjects.add(subject.getSubjectTitle());
			}
		}
		
		return unassignedSubjects;
	}

}

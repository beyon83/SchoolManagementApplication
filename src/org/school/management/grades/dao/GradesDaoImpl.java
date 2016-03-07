package org.school.management.grades.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Grades;
import org.school.management.model.Student;
import org.school.management.model.Subject;
import org.school.management.student.dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GradesDaoImpl extends GenericDaoImpl<Grades, Long> implements GradesDao {
	
	@Autowired
	private StudentDao studentDao;
	
	public GradesDaoImpl() {
		super(Grades.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Grades> getGradesByStudent(long studentId, long subjectId) {
		Query query = getSession().createQuery("FROM Grades WHERE student = :studentId AND subject = :subjectId");
		query.setLong("studentId", studentId);
		query.setLong("subjectId", subjectId);
		List<Grades> grades = query.list();
		return grades;
	}

	@Override
	public List<Integer> getTotalGradesByStudent(Student student) {
		List<Integer> studentGrades = new java.util.ArrayList<>();
		Student student2 = studentDao.getEntityById(student.getId());
		Hibernate.initialize(student2.getGrades());
		for(Grades grade : student2.getGrades()) {
			Hibernate.initialize(grade.getGrade());
			studentGrades.add(grade.getGrade());
		}
		return studentGrades;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Grades> getGradesOfStudent(Student student) {
		Query query = getSession().createQuery("FROM Grades WHERE student = :studentId");
		query.setLong("studentId", student.getId());
		List<Grades> grades = query.list();
		return grades;
	}
	
	public List<Double> getAverageOfEachSubject(Student student) {
		
		student = studentDao.getStudentByName(student.getUsername());
		Set<Subject> subjects = student.getSubjects();
		
		Hibernate.initialize(student.getSubjects());
		Hibernate.initialize(student.getGrades());
		 
		ArrayList<Double> subjectAverages = new ArrayList<>();
		
		for(Subject subject : subjects) { // Subjects of logged student
			double sum = 0;
			int counter = 0;
			for(Grades grade : student.getGrades()) { // Grades of logged student
				if(subject.getSubjectTitle().equals(grade.getSubject().getSubjectTitle())) {
					System.out.println("SUBJECT: " + subject.getSubjectTitle() + " | GRADE: " + grade.getGrade());
					sum += grade.getGrade();
					counter++;
				}
			}
			subjectAverages.add(sum / counter);
		}
		
		return subjectAverages;
	}

	@Override
	public Double getTotalAverageOfStudent(Student student) {
		
		List<Double> avgOfEachSubject = getAverageOfEachSubject(student);
		
		double sum = 0;
		double total = 0;
		int counter = 0;
		
		for(int i = 0; i < avgOfEachSubject.size(); i++) {
			/** Avoid not-a-number issue */
			if(!Double.isNaN(avgOfEachSubject.get(i))) {
				sum += avgOfEachSubject.get(i);
				counter++;
			}
		}
		
		if(sum > 0) {
			total = (double) (sum / counter);
		} else {
			total = sum;
		}
		
		return total;
	}
	
}

package org.school.management.grades.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

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
	
	static final Logger logger = Logger.getLogger(GradesDaoImpl.class.getName());
	
	public GradesDaoImpl() {
		super(Grades.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Grades> getGradesByStudent(long studentId, long subjectId) {
		Query query = getSession().createQuery("FROM Grades WHERE student = :studentId AND subject = :subjectId");
		query.setLong("studentId", studentId);
		query.setLong("subjectId", subjectId);
		return query.list();
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
		return query.list();
	}
	
	public List<Double> getAverageOfEachSubject(Student student) {
		
		Student student2 = studentDao.getStudentByName(student.getUsername());
		Set<Subject> subjects = student2.getSubjects();
		
		Hibernate.initialize(student2.getSubjects());
		Hibernate.initialize(student2.getGrades());
		 
		ArrayList<Double> subjectAverages = new ArrayList<>();
		
		for(Subject subject : subjects) { // Subjects of logged student
			double sum = 0;
			int counter = 0;
			for(Grades grade : student2.getGrades()) { // Grades of logged student
				if(subject.getSubjectTitle().equals(grade.getSubject().getSubjectTitle())) {
					logger.info("SUBJECT: " + subject.getSubjectTitle() + " | GRADE: " + grade.getGrade());
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
		
		int sum = 0;
		double total;
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

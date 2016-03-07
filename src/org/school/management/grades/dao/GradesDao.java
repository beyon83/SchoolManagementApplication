package org.school.management.grades.dao;

import java.util.List;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Grades;
import org.school.management.model.Student;

public interface GradesDao extends GenericDao<Grades, Long> {

	List<Grades> getGradesByStudent(long studentId, long subjectId);

	List<Integer> getTotalGradesByStudent(Student student);

	Double getTotalAverageOfStudent(Student student);

	List<Grades> getGradesOfStudent(Student student);

}

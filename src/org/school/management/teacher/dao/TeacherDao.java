package org.school.management.teacher.dao;

import java.util.List;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Teacher;

public interface TeacherDao extends GenericDao<Teacher, Long> {
	
	Teacher getTeacherByUsername(String username);
	List<Teacher> getAllTeachers();

}

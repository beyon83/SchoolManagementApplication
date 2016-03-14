package org.school.management.teacher.dao;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Teacher;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherDaoImpl extends GenericDaoImpl<Teacher, Long> implements TeacherDao {

	public TeacherDaoImpl() {
		super(Teacher.class);
	}

	@Override
	public Teacher getTeacherByUsername(String username) {
		Query query = getSession().createQuery("FROM Teacher WHERE username = :username");
		query.setParameter("username", username);
		Teacher teacher = (Teacher) query.uniqueResult();
		Hibernate.initialize(teacher.getSubjects());
		return teacher;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Teacher> getAllTeachers() {
		Query query = getSession().createQuery("FROM Teacher");
		List<Teacher> teachers = query.list();
		for(Teacher teacher : teachers) {
			Hibernate.initialize(teacher.getFirstName());
		}
		return teachers;
	}

}

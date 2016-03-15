package org.school.management.admin.dao;

import java.util.List;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Admin;
import org.school.management.model.Student;

public interface AdminDao extends GenericDao<Admin, Long> {

	List<Student> paginatedListOfStudents(int page);

	List<Integer> paginationNumbers(int page);

}

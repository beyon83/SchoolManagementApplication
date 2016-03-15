package org.school.management.admin.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Admin;
import org.school.management.model.Student;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDaoImpl extends GenericDaoImpl<Admin, Long> implements AdminDao {
	
	private int numberOfPages;
	static final Logger logger = Logger.getLogger(AdminDaoImpl.class.getName());

	public AdminDaoImpl() {
		super(Admin.class);
	}
	
	@Override
	public List<Integer> paginationNumbers(int page) {
		
		ArrayList<Integer> list = new ArrayList<>();
		
		if(page == 1 || page == 2) {
			if(numberOfPages <= 5) {
				for(int i = 1; i <= numberOfPages; i++) {
					list.add(i);
				}
			} else if(numberOfPages > 5) {
				for(int i = 1; i <= 5; i++) {
					list.add(i);
				}
			}
		} else if(page > 2 && page < numberOfPages - 1) { 
			for(int i = page - 2; i <= page + 2; i++) {
				list.add(i);
			}
		} else if((page + 2) > numberOfPages || (page + 2) == numberOfPages) {
			list.clear();
			for(int i = page - 2; i <= numberOfPages; i++) {
				list.add(i);
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Student> paginatedListOfStudents(int page) {
		
		int maxRowsPerPage = 5;
		
		Query selectQuery = getSession().createQuery("FROM Student");
		selectQuery.setFirstResult((int) ((page - 1) * maxRowsPerPage));
		selectQuery.setMaxResults(maxRowsPerPage);
		List<Student> students = selectQuery.list();
		
		Query countQuery = getSession().createQuery("SELECT COUNT(*) FROM Student");
		Long countResult = (Long) countQuery.uniqueResult();
		
		double decimalPages = (double) countResult / maxRowsPerPage; // divide count by whatever maxRowsPerPage is, to get decimal number
		this.numberOfPages = (int) Math.ceil(decimalPages); // round up to the next whole integer to get correct number of pages
		
		return students;
	}

}

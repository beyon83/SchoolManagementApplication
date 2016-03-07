package org.school.management.admin.dao;

import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Admin;

public class AdminDaoImpl extends GenericDaoImpl<Admin, Long> implements AdminDao {

	public AdminDaoImpl() {
		super(Admin.class);
	}

}

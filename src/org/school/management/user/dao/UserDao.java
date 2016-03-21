package org.school.management.user.dao;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.User;

public interface UserDao extends GenericDao<User, Long> {

	User getEntityByName(String username);

	boolean isUsernameTaken(String username);
	
	byte[] getImage(long id);

}

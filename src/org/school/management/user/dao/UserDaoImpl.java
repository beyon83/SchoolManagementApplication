package org.school.management.user.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements UserDao {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public UserDaoImpl() {
		super(User.class);
	}

	@Override
	public User saveEntity(User entity) {
		entity.setPassword(passwordEncoder.encode(entity.getPasswordTransient()));
		return super.saveEntity(entity);
	}
	
	@Override
	public User getEntityByName(String username) {
		Criteria criteria = getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("username", username));
		User user;
		if(criteria.list() == null) {
			user = (User) criteria.uniqueResult();
		} else {
			user = (User) criteria.list().get(0);
		}
		return user;
	}

	@Override
	public boolean isUsernameTaken(String username) {
		Query query = getSession().createQuery("FROM User WHERE username = :username");
		query.setParameter("username", username);
		return query.list().size() > 0 ? true : false;
	}

	@Override
	public byte[] getImage(long id) {
		return getSession().get(User.class, id).getProfileImage();
	}
	
}

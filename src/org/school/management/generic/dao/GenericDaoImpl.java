package org.school.management.generic.dao;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class GenericDaoImpl<E, K extends Serializable> implements GenericDao<E, K> {
	
	public Class<E> persistentClass;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public GenericDaoImpl(Class<E> persistentClass) {
		this.persistentClass = persistentClass;
	}
	
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public Class<E> getPersistentClass() {
		return persistentClass;
	}
	
	@Override
	public void deleteEntity(E entity) {
		getSession().delete(entity);
	}

	@Override
	public void updateEntity(E entity) {
		getSession().update(entity);
	}

	@Override
	public E saveEntity(E entity) {
		getSession().save(entity);
		return entity;
	}

	@Override
	public E getEntityById(K key) {
		return getSession().get(persistentClass, key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<E> getAllEntities() {
		Criteria criteria = getSession().createCriteria(persistentClass);
		return criteria.list();
	}
	
}

package org.school.management.generic.dao;

import java.util.Collection;

public interface GenericDao<E, K> {
	
	void deleteEntity(E entity);
	void updateEntity(E entity);
	E saveEntity(E entity);
	E getEntityById(K key);
	Collection<E> getAllEntities();

}

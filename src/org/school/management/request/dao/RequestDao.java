package org.school.management.request.dao;

import java.util.Set;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Request;

public interface RequestDao extends GenericDao<Request, Long> {

	Set<Request> getAllRequests();

}

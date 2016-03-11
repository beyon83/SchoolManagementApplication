package org.school.management.request.dao;

import java.util.List;
import java.util.Set;

import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Request;
import org.springframework.stereotype.Repository;

@Repository
public class RequestDaoImpl extends GenericDaoImpl<Request, Long> implements RequestDao {

	public RequestDaoImpl() {
		super(Request.class);
	}

	@Override
	public Set<Request> getAllRequests() {
		java.util.List<Request> allRequests = (List<Request>) getAllEntities();
		Set<Request> requests = new java.util.HashSet<>();
		for(Request request : allRequests) {
			requests.add(request);
		}
		return requests;
	}
	
}

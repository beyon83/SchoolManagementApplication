package org.school.management.absence.dao;

import org.hibernate.Query;
import org.school.management.generic.dao.GenericDaoImpl;
import org.school.management.model.Absence;
import org.springframework.stereotype.Repository;

@Repository
public class AbsenceDaoImpl extends GenericDaoImpl<Absence, Long> implements AbsenceDao {

	public AbsenceDaoImpl() {
		super(Absence.class);
	}

	@Override
	public Long getAbsencesOfStudent(long id) {
		Query query = getSession().createQuery("SELECT COUNT(*) FROM Absence WHERE student = :id");
		query.setLong("id", id);
		long count = (long) query.uniqueResult();
		return count;
	}

}

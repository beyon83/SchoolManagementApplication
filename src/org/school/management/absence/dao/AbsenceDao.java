package org.school.management.absence.dao;

import org.school.management.generic.dao.GenericDao;
import org.school.management.model.Absence;

public interface AbsenceDao extends GenericDao<Absence, Long> {

	Long getAbsencesOfStudent(long id);

}

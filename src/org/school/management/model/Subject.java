package org.school.management.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="subjects")
public class Subject {

	@Id
	@GeneratedValue
	private long subjectId;

	private String subjectTitle;

	/** For third column in "subjects" table - "teacher_id" */
	@OneToOne
	@JoinColumn(name="teacherId")
	private Teacher teacher;
	
	@ManyToMany(mappedBy="subjects", fetch=FetchType.LAZY)
	private Set<Student> students = new HashSet<>();
	
	public Subject() {
		
	}
	
	public String getSubjectTitle() {
		return subjectTitle;
	}

	public void setSubjectTitle(String subjectTitle) {
		this.subjectTitle = subjectTitle;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public long getSubjectId() {
		return subjectId;
	}
	
	public Set<Student> getStudents() {
		return students;
	}

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	@Override
	public String toString() {
		return "Subject [subjectId=" + subjectId + ", subjectTitle=" + subjectTitle + ", teacher=" + teacher
				+ ", students=" + students + "]";
	}

}

package org.school.management.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="teachers")
public class Teacher extends User {

	private static final long serialVersionUID = 1L;

	//	@OneToMany(mappedBy = "teacher", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="teacher_subjects", joinColumns=@JoinColumn(name="teacher_id"), inverseJoinColumns=@JoinColumn(name="subject_id"))
	private Set<Subject> subjects = new HashSet<>();
	
	@ManyToMany(fetch=FetchType.EAGER) // treba prebaciti na LAZY, ali baca: HibernateException: collection is not associated with any session
	@JoinTable(name="teacher_students", joinColumns=@JoinColumn(name="teacher_id"), inverseJoinColumns=@JoinColumn(name="student_id"))
	private List<Student> students = new ArrayList<>();
	
	public Teacher() {
		
	}
	
	public Teacher(Set<Subject> subjects) {
		this.subjects = subjects;
	}
	
	public Set<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	@Override
	public String toString() {
		return "Teacher [subjects=" + subjects + ", students=" + students + "]";
	}

}

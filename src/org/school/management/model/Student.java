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
@Table(name="students")
public class Student extends User {

	private static final long serialVersionUID = 1L;

	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(name="student_subjects", joinColumns=@JoinColumn(name="student_id"), inverseJoinColumns=@JoinColumn(name="subject_id"))
	private Set<Subject> subjects = new HashSet<>();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="student_teacher", joinColumns=@JoinColumn(name="student_id"), inverseJoinColumns=@JoinColumn(name="teacher_id"))
	private List<Teacher> teachers = new ArrayList<>();
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="student_grades", joinColumns=@JoinColumn(name="student_id"), inverseJoinColumns=@JoinColumn(name="grade_id"))
	private List<Grades> grades = new ArrayList<>();
	
	/** Getter ans Setters */
	public Set<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}

	public List<Teacher> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}
	
	public List<Grades> getGrades() {
		return grades;
	}

	public void setGrades(List<Grades> grades) {
		this.grades = grades;
	}

}

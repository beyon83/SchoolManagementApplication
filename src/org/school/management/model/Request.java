package org.school.management.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="requests")
public class Request {
	
	@Id 
	@GeneratedValue
	private long requestId;
	
	@ManyToOne
	private Student student;
	
	@OneToOne
	private Subject subject;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public long getRequestId() {
		return requestId;
	}

	@Override
	public String toString() {
		return "Request [requestId=" + requestId + ", student=" + student + ", subject=" + subject + "]";
	}
	
}

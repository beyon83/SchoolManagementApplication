package org.school.management.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.school.management.absence.dao.AbsenceDao;
import org.school.management.grades.dao.GradesDao;
import org.school.management.model.Absence;
import org.school.management.model.Admin;
import org.school.management.model.Grades;
import org.school.management.model.Request;
import org.school.management.model.Student;
import org.school.management.model.Subject;
import org.school.management.model.Teacher;
import org.school.management.model.User;
import org.school.management.request.dao.RequestDao;
import org.school.management.student.dao.StudentDao;
import org.school.management.subject.dao.SubjectDao;
import org.school.management.teacher.dao.TeacherDao;
import org.school.management.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private TeacherDao teacherDao;

	@Autowired
	private SubjectDao subjectDao;
	
	@Autowired
	private AbsenceDao absenceDao;
	
	@Autowired
	private GradesDao gradesDao;
	
	@Autowired
	private RequestDao requestDao;

	/** index page, home page */
	@RequestMapping({"/", "/home"})
	public String home(Authentication authentication, Principal principal, HttpSession session, Model model) {
		
		if(principal != null) {
			String loggedUser = principal.getName();
			User user = userDao.getEntityByName(loggedUser);
			session.setAttribute("loggedUser", user);
		}
		
		if(authentication != null) {
			String authority = authentication.getAuthorities().toString();
			if(authority.equals("[Admin]")) {
				java.util.Set<Request> requests = requestDao.getAllRequests();
				model.addAttribute("requests", requests);
			}
		}
		
		return "home";
	}
	
	/** Login form */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	/** Logout function */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/";
	}
	
	/** Admin's registration form */
	@RequestMapping("/register-admin")
	public String registerAdmin(@ModelAttribute("admin") Admin admin) {
		return "register-admin";
	}
	
	/** Admin got registered */
	@RequestMapping("/admin-registered")
	public String adminRegistered(User user, Admin admin) {
		
		user.setAuthority("Admin");
		userDao.saveEntity(user);
		
		admin.setAuthority("Admin");
		userDao.saveEntity(admin);
		return "redirect:/";
	}
	
	/** Teacher's registration form */
	@RequestMapping("/register-teacher")
	public String registerTeacher(@ModelAttribute("teacher") Teacher teacher, Model model) {
		
		/** Fetch all subjects available, and put them inside <select> tag as options */
//		List<Subject> listOfSubjects = subjectDao.getAllSubjects();
		List<String> unassignedSubjects = subjectDao.getUnassignedSubjectsForTeacher();
		model.addAttribute("subjects", unassignedSubjects);
		
		return "register-teacher";
	}
	
	/** Teacher got registered */
	@RequestMapping("/teacher-registered")
	public String teacherRegistered(User user, Teacher teacher, @RequestParam("getSelectedSubjects") String[] selectedSubjects) {
		
		user.setAuthority("Teacher");
		userDao.saveEntity(user);
		
		teacher.setAuthority("Teacher");
		
		Subject subject = new Subject();
		
		for(int i = 0; i < selectedSubjects.length; i++) {
			/** Dynamically add one or more subjects for current teacher */
			subject = subjectDao.getSubjectByTitle(selectedSubjects[i]);
			teacher.getSubjects().add(subject);
			subject.setTeacher(teacher);
		}
		
		userDao.saveEntity(teacher);
		subjectDao.updateEntity(subject);
		
		return "redirect:/";
	}
	
	/** Student's registration form */
	@RequestMapping("/register-student")
	public String registerStudent(@ModelAttribute("student") Student student, Model model) {
		
		/** Fetch all subjects available, and put them inside <select> tag as options */
		List<Subject> listOfSubjects = subjectDao.getAllSubjects();
		model.addAttribute("subjects", listOfSubjects);
		
		return "register-student";
	}
	
	/** Student got registered */
	@RequestMapping("/student-registered")
	public String studentRegistered(User user, Student student, @RequestParam("getSelectedSubjects") String[] selectedSubjects) {
		
		user.setAuthority("Student");
		userDao.saveEntity(user);
		
		student.setAuthority("Student");
		
	    Subject subject = new Subject();
	    Teacher teacher = new Teacher();
	    
		for(int i = 0; i < selectedSubjects.length; i++) {
			/** Dynamically add one or more subjects for the current student */
			subject = subjectDao.getSubjectByTitle(selectedSubjects[i]);
			student.getSubjects().add(subject);
//			teacher = teacherDao.getTeacherByUsername(subject.getTeacher().getUsername());
//			teacher.getSubjects().add(subject);
			
			teacher = subject.getTeacher();
			
			subject.setTeacher(teacher);
			teacher.getStudents().add(student);
			student.getTeachers().add(teacher);
		}
		
 		userDao.saveEntity(student);
		userDao.updateEntity(teacher);
		return "redirect:/";
	}
	
	/** Create new subject */
	@RequestMapping("/add-course")
	public String addCourse(@ModelAttribute("subject") Subject subject, Model model) {
		List<Teacher> teachers = teacherDao.getAllTeachers();
		model.addAttribute("teachers", teachers);
		return "add-course";
	}
	
	/** New subject is added */
	@RequestMapping("/course-added")
	public String courseAdded(Subject subject, @RequestParam(value="assignedTeacher", required=false) String assignedTeacher) {
		if(assignedTeacher != null) {
			Teacher teacher = new Teacher();
			teacher = teacherDao.getTeacherByUsername(assignedTeacher);
			subject.setTeacher(teacher);
		}
		subjectDao.saveEntity(subject);
		return "redirect:/";
	}
	
	/** List all subjects */
	@RequestMapping("/courses")
	public String courses(Model model, Authentication authentication) {
		
		if(authentication != null) {
			String loggedStudent = authentication.getName();
			
			/** Get authority of the logged user */
			String authority = authentication.getAuthorities().toString();
			
			if(authority.equals("[Student]")) {
				Student student = studentDao.getStudentByName(loggedStudent);
				model.addAttribute("loggedStudent", student.getUsername());
			}
		}
		
		List<Subject> subjects = subjectDao.getAllSubjects();
		model.addAttribute("subjects", subjects);
		
		return "courses";
	}
	
	/** Edit subject when teacher is not assigned to the subject */
	@RequestMapping("/edit-subject")
	public String editSubject(@RequestParam("id") long subjectId, Model model) {
		Subject subject = subjectDao.getEntityById(subjectId);
		List<Teacher> teachers = teacherDao.getAllTeachers();
		model.addAttribute("subject", subject);
		model.addAttribute("teachers", teachers);
		return "edit-subject";
	}
	
	/** Edit subjects (assign teacher to the subject) */
	@RequestMapping("/subject-edited")
	public String subjectEdited(@RequestParam("id") long subjectId, @RequestParam("assignedTeacher") String assignedTeacher) {
		Subject subject = subjectDao.getEntityById(subjectId);
		Teacher teacher = teacherDao.getTeacherByUsername(assignedTeacher);
		subject.setTeacher(teacher);
		teacher.getSubjects().add(subject);
		subjectDao.updateEntity(subject); // update "subjects", assign teacher
		teacherDao.updateEntity(teacher); // update "teacher_subjects"
		return "redirect:/courses";
	}
	
	/** Fetch all registered students */
	@RequestMapping("/get-all-students")
	public String getAllStudents(Model model) {
		List<Student> students = (List<Student>) studentDao.getAllEntities();
		model.addAttribute("students", students);
		return "students";
	}
	
	/** Fetch all students by admin */
	@RequestMapping("/admin-get-all-students")
	public String getStudentsByAdmin(Model model) {
		List<Student> students = (List<Student>) studentDao.getAllEntities();
		model.addAttribute("students", students);
		return "admin-students";
	}
	
	/** Obtain particular student while logged as admin */
	@RequestMapping("/admin-student")
	public String getStudentByAdmin(@RequestParam("id") long id, Model model) {
		
		Student student = studentDao.getStudentWithSubjects(id);
		
//		List<Subject> subjects = subjectDao.getAllSubjects();
		List<String> subjects = subjectDao.getUnattendedSubjects(student);
		
		model.addAttribute("student", student);
		model.addAttribute("subjects", subjects);
		
		return "admin-student";
	}
	
	/** Assigning class to student by admin */
	@RequestMapping("/class-assigned/{id}")
	public String assignClassToStudent(@RequestParam(value="assignedClasses", required=false) String[] assignedClasses, @PathVariable("id") long id, Model model) {
		
		model.addAttribute("id", id);
		
		Student student = studentDao.getStudentWithSubjects(id);
		Subject subject = new Subject();
		Teacher teacher = new Teacher();
		
		for(int i = 0; i < assignedClasses.length; i++) {
			subject = subjectDao.getSubjectByTitle(assignedClasses[i]);
			student.getSubjects().add(subject);
			
			teacher = subject.getTeacher();
			
			subject.setTeacher(teacher);
			teacher.getStudents().add(student);
			student.getTeachers().add(teacher);
		}
		
		userDao.updateEntity(student);
		userDao.updateEntity(teacher);
		
		return "redirect:/admin-student";
	}
	
	/** List students */
	@RequestMapping("/get-students")
	public String getStudents(Model model, Principal principal) {
		
		String teacherName = principal.getName();
		
		/** Get teacher from the DB who is currently logged in */
		Teacher teacher = teacherDao.getTeacherByUsername(teacherName);
		
		/** Return only students bounded to this particular teacher */
		List<Student> students = studentDao.getStudentsByTeacher(teacherName);
		
		/** Return only subjects bounded to this teacher */
		List<Subject> subjects = studentDao.getSubjectsByTeacher(teacher);
		
		model.addAttribute("students", students);
		model.addAttribute("teacher", teacher);
		model.addAttribute("subjects", subjects);
		return "students";
	}
	
	/** View student account */
	@RequestMapping("/student")
	public String getStudent(@RequestParam("student") String studentUsername, @RequestParam("subjectId") long subjectId, Model model, Principal principal) {
		
		String teacherName = principal.getName();
		Teacher teacher = teacherDao.getTeacherByUsername(teacherName);
		Student student = studentDao.getStudentByTeacher(teacher, studentUsername);
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		
		List<Grades> grades = gradesDao.getGradesByStudent(student.getId(), subjectId);
		
		Double totalAverage = gradesDao.getTotalAverageOfStudent(student);
		
		Subject subject = subjectDao.getEntityById(subjectId);
		
		model.addAttribute("student", student);
		model.addAttribute("numbOfAbsences", count);
		model.addAttribute("grades", grades);
		model.addAttribute("subject", subject);
		model.addAttribute("totalAverage", totalAverage);
		return "student";
	}
	
	/** Assign grade to student by teacher */
	@RequestMapping("/assign-grade")
	public String assignGrade(@RequestParam("studentId") long studentId, @RequestParam("subjectId") long subjectId, @RequestParam("grade") int selectedGrade, @RequestParam("attendingClass") String attendingClass, Model model) {
		
		/** Obtain selected student by ID */
		Student student = studentDao.getStudentWithSubjects(studentId);
		model.addAttribute("student", student);
		
		/** Obtain current student's subject */
		Subject subject = subjectDao.getSubjectById(subjectId);
		
		/** Insert grade for selected student */
		if(selectedGrade > 0) {
			Grades grade = new Grades();
			grade.setGrade(selectedGrade);
			grade.setStudent(student);
			student.getGrades().add(grade);
			grade.setSubject(subject);
			gradesDao.saveEntity(grade);
			studentDao.updateEntity(student);
			subjectDao.updateEntity(subject);
		}
		
		/** Insert absences for selected student */
		if(attendingClass.equals("false")) {
			Absence absence = new Absence();
			absence.setStudent(student);
			absence.setSubject(subject);
			absenceDao.saveEntity(absence);
		}
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		List<Grades> grades = gradesDao.getGradesByStudent(student.getId(), subjectId);
		
		model.addAttribute("numbOfAbsences", count);
		model.addAttribute("grades", grades);
		model.addAttribute("subject", subject);
		
		return "student";
	}
	
	/** View student's account by student */
	@RequestMapping("/student-account")
	public String studentAccount(Principal principal, Model model) {
		String username = principal.getName();
		Student student = studentDao.getStudentByName(username);
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		
		Double totalAverage = gradesDao.getTotalAverageOfStudent(student);
		
		model.addAttribute("student", student);
		model.addAttribute("numbOfAbsences", count);
		model.addAttribute("totalAverage", totalAverage);
		return "student-account";
	}
	
	@RequestMapping("/subject-request")
	public String subjectRequest(@RequestParam("id") long id, Model model, Principal principal) {
		
		String loggedStudent = principal.getName();
		
		Student senderOfRequest = studentDao.getStudentByName(loggedStudent);
		Subject requestedSubject = subjectDao.getEntityById(id);
		
		Request request = new Request();
		request.setSubject(requestedSubject);
		request.setStudent(senderOfRequest);
		requestDao.saveEntity(request); // save request
		
		model.addAttribute("requestedSubject", requestedSubject);
		return "subject-request";
	}
	
	@RequestMapping("/review-requests")
	public String getRequest(Authentication authentication, Model model) {
		
		if(authentication != null) {
			String authority = authentication.getAuthorities().toString();
			if(authority.equals("[Admin]")) {
				Set<Request> requests = requestDao.getAllRequests();
				model.addAttribute("requests", requests);
			}
		}
		
		return "review-request";
	}
	
	@RequestMapping("/request-replied/{student}/{subjectId}/{requestId}")
	public String requestReplied(@RequestParam("request") String requestReply, @PathVariable("student") String studentName, @PathVariable("subjectId") long subjectId, @PathVariable("requestId") long requestId) {
		
		if(requestReply.equals("Accept")) {
			Student student = studentDao.getStudentByName(studentName);
			Subject subject = subjectDao.getSubjectById(subjectId);
			Teacher teacher = teacherDao.getTeacherByUsername(subject.getTeacher().getUsername());
			
			teacher.getStudents().add(student);
			student.getSubjects().add(subject);
			student.getTeachers().add(teacher);
			subject.getStudents().add(student);
			
			studentDao.updateEntity(student);
			subjectDao.updateEntity(subject);
			teacherDao.updateEntity(teacher);
		}
		
		Request request = requestDao.getEntityById(requestId);
		requestDao.deleteEntity(request);
		
		return "redirect:/review-requests";
	}
	
}

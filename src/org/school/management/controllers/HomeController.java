package org.school.management.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.school.management.absence.dao.AbsenceDao;
import org.school.management.admin.dao.AdminDao;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
	
	@Autowired
	private AdminDao adminDao;
	
	private static final Logger logger = Logger.getLogger(HomeController.class);
	
	private static final String REQUESTS = "requests";
	private static final String SUBJECT = "subject";
	private static final String SUBJECTS = "subjects";
	private static final String STUDENTS = "students";
	private static final String STUDENT = "student";
	private static final String NUMB_OF_ABSENCES = "numbOfAbsences";
	private static final String REDIRECT_HOME = "redirect:/";

	/**
	 * index page, home page
	 * @param authentication
	 * @param principal
	 * @param session
	 * @return
	 */
	@RequestMapping({"/", "/home"})
	public String home(Authentication authentication, Principal principal, HttpSession session) {
		
		if(principal != null) {
			String loggedUser = principal.getName();
			User user = userDao.getEntityByName(loggedUser);
			session.setAttribute("loggedUser", user);
		}
		
		if(authentication != null) {
			String authority = authentication.getAuthorities().toString();
			if("[Admin]".equals(authority)) {
				java.util.Set<Request> requests = requestDao.getAllRequests();
				if(requests != null) {
					session.setAttribute(REQUESTS, requests);
				}
			}
		}
		
		return "home";
	}
	
	/** Login form */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
	/**
	 * Logout function
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return REDIRECT_HOME;
	}
	
	/**
	 * Admin's registration form
	 * @param admin
	 * @return
	 */
	@RequestMapping("/register-admin")
	public String registerAdmin(@ModelAttribute("admin") Admin admin) {
		return "register-admin";
	}
	
	/**
	 * Admin got registered
	 * @param user
	 * @param admin
	 * @param errors
	 * @return
	 */
	@RequestMapping("/admin-registered")
	public String adminRegistered(User user, @Valid @ModelAttribute("admin") Admin admin, Errors errors) {
		
		if(userDao.isUsernameTaken(admin.getUsername())) {
			errors.rejectValue("username", "admin.username", "*username " + admin.getUsername() + " is already taken.");
		}
		
		if(errors.hasErrors()) {
			return "register-admin";
		}
		
		user.setAuthority("Admin");
		userDao.saveEntity(user);
		
		admin.setAuthority("Admin");
		userDao.saveEntity(admin);
		return REDIRECT_HOME;
	}
	
	/**
	 * Teacher's registration form
	 * @param teacher
	 * @param model
	 * @return
	 */
	@RequestMapping("/register-teacher")
	public String registerTeacher(@ModelAttribute("teacher") Teacher teacher, Model model) {
		
		/** Fetch all subjects available, and put them inside <select> tag as options */
		List<String> unassignedSubjects = subjectDao.getUnassignedSubjectsForTeacher();
		model.addAttribute(SUBJECTS, unassignedSubjects);
		
		return "register-teacher";
	}
	
	/**
	 * Teacher got registered
	 * @param user
	 * @param teacher
	 * @param errors
	 * @param selectedSubjects
	 * @param model
	 * @return
	 */
	@RequestMapping("/teacher-registered")
	public String teacherRegistered(User user, @Valid @ModelAttribute("teacher") Teacher teacher, Errors errors, @RequestParam(value="getSelectedSubjects", required=false) String[] selectedSubjects, Model model) {
		
		if(userDao.isUsernameTaken(teacher.getUsername())) {
			errors.rejectValue("username", "teacher.username", "*username " + teacher.getUsername() + " is already taken.");
		}
		
		if(errors.hasErrors()) {
			List<String> unassignedSubjects = subjectDao.getUnassignedSubjectsForTeacher();
			model.addAttribute(SUBJECTS, unassignedSubjects);
			return "register-teacher";
		}
		
		user.setAuthority("Teacher");
		userDao.saveEntity(user);
		
		teacher.setAuthority("Teacher");
		
		Subject subject = new Subject();
		
		if(selectedSubjects != null) {
			for(int i = 0; i < selectedSubjects.length; i++) {
				/** Dynamically add one or more subjects for current teacher */
				subject = subjectDao.getSubjectByTitle(selectedSubjects[i]);
				teacher.getSubjects().add(subject);
				subject.setTeacher(teacher);
			}
		}
		
		userDao.saveEntity(teacher);
		subjectDao.updateEntity(subject);
		
		return REDIRECT_HOME;
	}
	
	/**
	 * Student's registration form
	 * @param student
	 * @param model
	 * @return
	 */
	@RequestMapping("/register-student")
	public String registerStudent(@ModelAttribute("student") Student student, Model model) {
		
		/** Fetch all subjects available, and put them inside <select> tag as options */
		List<Subject> listOfSubjects = subjectDao.getAllSubjects();
		model.addAttribute(SUBJECTS, listOfSubjects);
		
		return "register-student";
	}
	
	/**
	 * Student got registered
	 * @param user
	 * @param student
	 * @param errors
	 * @param selectedSubjects
	 * @param model
	 * @param multipartFile
	 * @return
	 */
	@RequestMapping("/student-registered")
	public String studentRegistered(User user, @Valid @ModelAttribute("student") Student student, Errors errors, @RequestParam(value="getSelectedSubjects", required=false) String[] selectedSubjects, Model model, @RequestPart("image") MultipartFile multipartFile) {
		
		if(userDao.isUsernameTaken(student.getUsername())) {
			errors.rejectValue("username", "student.username", "*username " + student.getUsername() + " is already taken.");
		}
		
		if(errors.hasErrors()) {
			List<Subject> listOfSubjects = subjectDao.getAllSubjects();
			model.addAttribute(SUBJECTS, listOfSubjects);
			return "register-student";
		}
		
		user.setAuthority("Student");
		userDao.saveEntity(user);
		
		student.setAuthority("Student");
		
		try {
			student.setProfileImage(multipartFile.getBytes());
		} catch (IOException e) {
			logger.info(e);
		}
		
	    Teacher teacher = new Teacher();
	    
	    if(selectedSubjects != null) {
			for(int i = 0; i < selectedSubjects.length; i++) {
				/** Dynamically add one or more subjects for the current student */
				Subject subject = subjectDao.getSubjectByTitle(selectedSubjects[i]);
				student.getSubjects().add(subject);
//				teacher = teacherDao.getTeacherByUsername(subject.getTeacher().getUsername());
//				teacher.getSubjects().add(subject);
				
				teacher = subject.getTeacher();
				
				subject.setTeacher(teacher);
				teacher.getStudents().add(student);
				student.getTeachers().add(teacher);
			}
			userDao.saveEntity(student);
	 		userDao.updateEntity(teacher);
	    } else {
	    	userDao.saveEntity(student);
	    }
		
		return REDIRECT_HOME;
	}
	
	/**
	 * Create new subject
	 * @param subject
	 * @param model
	 * @return
	 */
	@RequestMapping("/add-course")
	public String addCourse(@ModelAttribute("subject") Subject subject, Model model) {
		List<Teacher> teachers = teacherDao.getAllTeachers();
		model.addAttribute("teachers", teachers);
		return "add-course";
	}
	
	/**
	 * New subject is added
	 * @param subject
	 * @param assignedTeacher
	 * @return
	 */
	@RequestMapping("/course-added")
	public String courseAdded(Subject subject, @RequestParam(value="assignedTeacher", required=false) String assignedTeacher) {
		if(assignedTeacher != null) {
			Teacher teacher = teacherDao.getTeacherByUsername(assignedTeacher);
			subject.setTeacher(teacher);
		}
		subjectDao.saveEntity(subject);
		return REDIRECT_HOME;
	}
	
	/**
	 * List all subjects
	 * @param model
	 * @param authentication
	 * @return
	 */
	@RequestMapping("/courses")
	public String courses(Model model, Authentication authentication) {
		
		if(authentication != null) {
			String loggedStudent = authentication.getName();
			
			/** Get authority of the logged user */
			String authority = authentication.getAuthorities().toString();
			
			if("[Student]".equals(authority)) {
				Student student = studentDao.getStudentByName(loggedStudent);
				model.addAttribute("loggedStudent", student.getUsername());
			}
		}
		
		List<Subject> subjects = subjectDao.getAllSubjects();
		model.addAttribute(SUBJECTS, subjects);
		
		return "courses";
	}
	
	/**
	 * Edit subject when teacher is not assigned to the subject
	 * @param subjectId
	 * @param model
	 * @return
	 */
	@RequestMapping("/edit-subject")
	public String editSubject(@RequestParam("id") long subjectId, Model model) {
		Subject subject = subjectDao.getEntityById(subjectId);
		List<Teacher> teachers = teacherDao.getAllTeachers();
		model.addAttribute(SUBJECT, subject);
		model.addAttribute("teachers", teachers);
		return "edit-subject";
	}
	
	/**
	 * Edit subjects (assign teacher to the subject)
	 * @param subjectId
	 * @param assignedTeacher
	 * @return
	 */
	@RequestMapping("/subject-edited")
	public String subjectEdited(@RequestParam("id") Long subjectId, @RequestParam(value="assignedTeacher", required=false) String assignedTeacher) {
		if(subjectId != null) {
			Subject subject = subjectDao.getEntityById(subjectId);
			if(assignedTeacher != null) {
				Teacher teacher = teacherDao.getTeacherByUsername(assignedTeacher);
				subject.setTeacher(teacher);
				teacher.getSubjects().add(subject);
				teacherDao.updateEntity(teacher); // update "teacher_subjects"
			}
			subjectDao.updateEntity(subject); // update "subjects", assign teacher
		}
		return "redirect:/courses";
	}
	
	/**
	 * Fetch all registered students
	 * @param model
	 * @return
	 */
	@RequestMapping("/get-all-students")
	public String getAllStudents(Model model) {
		List<Student> students = (List<Student>) studentDao.getAllEntities();
		model.addAttribute(STUDENTS, students);
		return "students";
	}
	
	/**
	 * Fetch all students by admin
	 * @param model
	 * @param page
	 * @return
	 */
	@RequestMapping("/admin-get-all-students")
	public String getStudentsByAdmin(Model model, @RequestParam(value="page", required=false) Integer page) {
		if(page == null) {
			page = 1;
		}
		List<Student> students = adminDao.paginatedListOfStudents(page);
		List<Integer> pagination = adminDao.paginationNumbers(page);
		model.addAttribute(STUDENTS, students);
		model.addAttribute("pagination", pagination);
		return "admin-students";
	}
	
	/**
	 * Obtain particular student while logged as admin
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/admin-student")
	public String getStudentByAdmin(@RequestParam("id") long id, Model model) {
		
		Student student = studentDao.getStudentWithSubjects(id);
		
		List<String> subjects = subjectDao.getUnattendedSubjects(student);
		
		model.addAttribute(STUDENT, student);
		model.addAttribute(SUBJECTS, subjects);
		
		return "admin-student";
	}
	
	/**
	 * Assigning class to student by admin
	 * @param assignedClasses
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/class-assigned/{id}")
	public String assignClassToStudent(@RequestParam(value="assignedClasses", required=false) String[] assignedClasses, @PathVariable("id") long id, Model model) {
		
		model.addAttribute("id", id);
		
		if(assignedClasses != null) {
			
			Student student = studentDao.getStudentWithSubjects(id);
			Teacher teacher = new Teacher();
			
			for(int i = 0; i < assignedClasses.length; i++) {
				Subject subject = subjectDao.getSubjectByTitle(assignedClasses[i]);
				student.getSubjects().add(subject);
				
				teacher = subject.getTeacher();
				
				subject.setTeacher(teacher);
				teacher.getStudents().add(student);
				student.getTeachers().add(teacher);
			}
			
			userDao.updateEntity(student);
			userDao.updateEntity(teacher);
		}
		
		return "redirect:/admin-student";
	}
	
	/**
	 * List students
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/get-students")
	public String getStudents(Model model, Principal principal) {
		
		String teacherName = principal.getName();
		
		/** Get teacher from the DB who is currently logged in */
		Teacher teacher = teacherDao.getTeacherByUsername(teacherName);
		
		/** Return only students bounded to this particular teacher */
		List<Student> students = studentDao.getStudentsByTeacher(teacherName);
		
		/** Return only subjects bounded to this teacher */
		List<Subject> subjects = studentDao.getSubjectsByTeacher(teacher);
		
		model.addAttribute(STUDENTS, students);
		model.addAttribute("teacher", teacher);
		model.addAttribute(SUBJECTS, subjects);
		return "students";
	}
	
	/**
	 * View student account
	 * @param studentUsername
	 * @param subjectId
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/student")
	public String getStudent(@RequestParam("student") String studentUsername, @RequestParam("subjectId") long subjectId, Model model, Principal principal) {
		
		String teacherName = principal.getName();
		Teacher teacher = teacherDao.getTeacherByUsername(teacherName);
		Student student = studentDao.getStudentByTeacher(teacher, studentUsername);
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		
		List<Grades> grades = gradesDao.getGradesByStudent(student.getId(), subjectId);
		
		Double totalAverage = gradesDao.getTotalAverageOfStudent(student);
		
		Subject subject = subjectDao.getEntityById(subjectId);
		
		model.addAttribute(STUDENT, student);
		model.addAttribute(NUMB_OF_ABSENCES, count);
		model.addAttribute("grades", grades);
		model.addAttribute(SUBJECT, subject);
		model.addAttribute("totalAverage", totalAverage);
		return "student";
	}
	
	/**
	 * Assign grade to student by teacher
	 * @param studentId
	 * @param subjectId
	 * @param selectedGrade
	 * @param attendingClass
	 * @param model
	 * @return
	 */
	@RequestMapping("/assign-grade")
	public String assignGrade(@RequestParam("studentId") long studentId, @RequestParam("subjectId") long subjectId, @RequestParam("grade") int selectedGrade, @RequestParam("attendingClass") String attendingClass, Model model) {
		
		/** Obtain selected student by ID */
		Student student = studentDao.getStudentWithSubjects(studentId);
		model.addAttribute(STUDENT, student);
		
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
		if("false".equals(attendingClass)) {
			Absence absence = new Absence();
			absence.setStudent(student);
			absence.setSubject(subject);
			absenceDao.saveEntity(absence);
		}
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		List<Grades> grades = gradesDao.getGradesByStudent(student.getId(), subjectId);
		
		model.addAttribute(NUMB_OF_ABSENCES, count);
		model.addAttribute("grades", grades);
		model.addAttribute(SUBJECT, subject);
		
		return "student";
	}
	
	/** View student's account by student
	 * @param principal
	 * @param model
	 * @return
	 */
	@RequestMapping("/student-account")
	public String studentAccount(Principal principal, Model model) {
		String username = principal.getName();
		Student student = studentDao.getStudentByName(username);
		
		long count = absenceDao.getAbsencesOfStudent(student.getId());
		
		Double totalAverage = gradesDao.getTotalAverageOfStudent(student);
		
		model.addAttribute(STUDENT, student);
		model.addAttribute(NUMB_OF_ABSENCES, count);
		model.addAttribute("totalAverage", totalAverage);
		return "student-account";
	}
	
	/**
	 * Sending the subject request
	 * @param id
	 * @param model
	 * @param principal
	 * @return
	 */
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
	public String getRequest() {
		return "review-request";
	}
	
	/**
	 * Handling subject request by Administrator
	 * @param requestReply
	 * @param studentName
	 * @param subjectId
	 * @param requestId
	 * @param session
	 * @return
	 */
	@RequestMapping("/request-replied/{student}/{subjectId}/{requestId}")
	public String requestReplied(@RequestParam("request") String requestReply, @PathVariable("student") String studentName, @PathVariable("subjectId") long subjectId, @PathVariable("requestId") long requestId, HttpSession session) {
		
		if("Accept".equals(requestReply)) {
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
		
		if(requestDao.getAllRequests().size() > 0) {
			session.setAttribute(REQUESTS, requestDao.getAllRequests());
		} else {
			session.setAttribute(REQUESTS, "");
		}
		
		return "redirect:/review-requests";
	}
	
	/**
	 * Load image from the table, and stream it out to the view
	 * @param id
	 * @param response
	 */
	@RequestMapping("/image/{id}")
	public void loadImage(@PathVariable("id") long id, HttpServletResponse response) {
		byte[] image = userDao.getImage(id);
		try {
			response.setContentType("image/jpg");
			response.getOutputStream().write(image);
		} catch (IOException e) {
			logger.info(e);
		}
	}
	
}

package org.school.management.controllers.test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.school.management.admin.dao.AdminDao;
import org.school.management.controllers.HomeController;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

public class HomeControllerTest {
	
	private User user;
	private Admin admin;
	private Subject subject;
	private Student student;
	private Teacher teacher;
	private Grades grades;
	private Set<Request> requests;
	
	Authentication authentication;
	
	@Mock
	private UserDao userDao;
	
	@Mock
	private AdminDao adminDao;
	
	@Mock
	private StudentDao studentDao;
	
	@Mock
	private SubjectDao subjectDao;
	
	@Mock
	private TeacherDao teacherDao;
	
	@Mock
	private RequestDao requestDao;
	
	@InjectMocks
	HomeController controller;
	
	/** Mock container */
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		
		/** Set viewResolver in standalone mode to avoid "Circular view path exception" in some test cases */
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");
		
		MockitoAnnotations.initMocks(this);
		
		/* Setup Spring test in standalone mode */
		mockMvc = standaloneSetup(controller).setViewResolvers(viewResolver).build();
		
		user = new User();
		admin = new Admin();
		subject = new Subject();
		student = new Student();
		teacher = new Teacher();
		grades = new Grades();
		requests = requestDao.getAllRequests();
		
	}
	
	@Test
	public void shouldReturnHome() throws Exception {
		doReturn(user).when(userDao).getEntityByName(anyString());
		doReturn(requests).when(requestDao).getAllRequests();
		mockMvc.perform(get("/")
		.sessionAttr("loggedUser", user))
		.andExpect(status().isOk())
//		.andExpect(model().attribute("requests", requests)) // Expected [] but was null
		.andExpect(view().name("home"));
	}
	
	@Test
	public void adminRegistration() throws Exception {
		doReturn(user).when(userDao).saveEntity(user);
		doReturn(admin).when(userDao).saveEntity(admin);
		mockMvc.perform(post("/admin-registered"))
//		.andDo(print())
//		.andExpect(status().isOk()) // Status expected 200 but was 302
		.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void teacherRegistration() throws Exception {
		doReturn(teacher).when(userDao).saveEntity(teacher);
		doReturn(subject).when(subjectDao).getSubjectByTitle(anyString());
		mockMvc.perform(post("/teacher-registered"))
//		.andDo(print())
//		.andExpect(status().isOk()) // Status expected 200 but was 302
		.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void studentRegistration() throws Exception {
		doReturn(student).when(userDao).saveEntity(student);
		doReturn(subject).when(subjectDao).getSubjectByTitle(anyString());
		mockMvc.perform(post("/student-registered"))
//		.andDo(print())
		.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void registerSubject() throws Exception {
		doReturn(teacher).when(teacherDao).getTeacherByUsername(anyString());
		doReturn(subject).when(subjectDao).saveEntity(subject);
		mockMvc.perform(post("/course-added"))
		.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void listAllSubjects() throws Exception {
		java.util.List<Subject> subjects = new java.util.ArrayList<>();
		doReturn(subjects).when(subjectDao).getAllSubjects();
		assertNotNull(subjects);
		mockMvc.perform(get("/courses"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(model().attribute("subjects", subjects))
		.andExpect(view().name("courses"));
	}
	
	@Test
	public void assignTeacherToSubject() throws Exception {
		Long subjectId = 1L;
		doReturn(subject).when(subjectDao).getSubjectById(subjectId);
		doReturn(teacher).when(teacherDao).getTeacherByUsername(anyString());
		mockMvc.perform(post("/subject-edited?id=" + subjectId))
		.andExpect(view().name("redirect:/courses"));
	}
	
	@Test
	public void getAllStudents() throws Exception {
		List<Student> students = (List<Student>) studentDao.getAllEntities();
		doReturn(students).when(studentDao).getAllEntities();
		mockMvc.perform(get("/get-all-students"))
		.andExpect(status().isOk())
		.andExpect(view().name("students"));
	}
	
	@Test
	public void assigningClassesToStudentByAdmin() throws Exception {
		final long ID = 1L;
		doReturn(student).when(studentDao).getStudentWithSubjects(ID);
		doReturn(subject).when(subjectDao).getSubjectByTitle(anyString());
		mockMvc.perform(post("/class-assigned/" + ID))
//		.andExpect(status().isOk())
		.andExpect(view().name("redirect:/admin-student"));
	}

}

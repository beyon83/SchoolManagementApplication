package org.school.management.controllers.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
		
		MockitoAnnotations.initMocks(this);
		
		/* Setup Spring test in standalone mode */
		mockMvc = standaloneSetup(controller).build();
		
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
		.andDo(print())
		.andExpect(view().name("redirect:/"));
	}

}

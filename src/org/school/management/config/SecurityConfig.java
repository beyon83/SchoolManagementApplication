package org.school.management.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private static final String ROLE_TEACHER = "Teacher";
	private static final String ROLE_ADMIN = "Admin";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication(); za hard code korisnika
		auth.jdbcAuthentication().passwordEncoder(passwordEncoder).dataSource(dataSource)
		.usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE BINARY username=?")
		.authoritiesByUsernameQuery("SELECT username, authority FROM users WHERE BINARY username=?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/logout").permitAll()
		.antMatchers("/login").permitAll()
		.antMatchers("/home").permitAll()
//		.antMatchers("/register-admin").permitAll() // za registraciju admina nakon dropanja tabele
		.antMatchers("/register-admin").hasAuthority(ROLE_ADMIN)
		.antMatchers("/register-teacher").hasAuthority(ROLE_ADMIN)
		.antMatchers("/register-student").hasAuthority(ROLE_ADMIN)
		.antMatchers("/register-student").hasAuthority(ROLE_ADMIN)
		.antMatchers("/admin-get-all-students").hasAuthority(ROLE_ADMIN)
		.antMatchers("/admin-student").hasAuthority(ROLE_ADMIN)
		.antMatchers("/class-assigned").hasAuthority(ROLE_ADMIN)
		.antMatchers("/class-assigned/*").hasAuthority(ROLE_ADMIN)
		.antMatchers("/add-course").hasAuthority(ROLE_ADMIN)
		.antMatchers("/course-added").hasAuthority(ROLE_ADMIN)
		.antMatchers("/edit-subject").hasAuthority(ROLE_ADMIN)
		.antMatchers("/subject-edited").hasAuthority(ROLE_ADMIN)
		.antMatchers("/review-requests").hasAuthority(ROLE_ADMIN)
		.antMatchers("/request-replied/**").hasAuthority(ROLE_ADMIN)
		.antMatchers("/get-students").hasAuthority(ROLE_TEACHER)
		.antMatchers("/student").hasAuthority(ROLE_TEACHER)
		.antMatchers("/students").hasAuthority(ROLE_TEACHER)
		.antMatchers("/assign-grade").hasAuthority(ROLE_TEACHER)
		.antMatchers("/subject-request").hasAuthority("Student")
		.antMatchers("/registered").permitAll()
		.antMatchers("/admin-registered").permitAll()
		.antMatchers("/teacher-registered").permitAll()
		.antMatchers("/student-registered").permitAll()
		.antMatchers("/student-account").permitAll()
		.antMatchers("/resources/**").permitAll()
		.antMatchers("/courses").permitAll()
		.antMatchers("/school-members").permitAll()
		.anyRequest().denyAll()
		.and()
		.formLogin().loginPage("/login").failureUrl("/login?error")
		.defaultSuccessUrl("/home");
	}

}

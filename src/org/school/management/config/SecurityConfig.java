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

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
//		auth.inMemoryAuthentication(); za hard code korisnika
		auth.jdbcAuthentication().passwordEncoder(passwordEncoder).dataSource(dataSource)
		.usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE BINARY username=?")
		.authoritiesByUsernameQuery("SELECT username, authority FROM users WHERE BINARY username=?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.authorizeRequests()
		.antMatchers("/").permitAll()
		.antMatchers("/logout").permitAll()
		.antMatchers("/login").permitAll()
		.antMatchers("/home").permitAll()
//		.antMatchers("/register-admin").permitAll() // za registraciju admina nakon dropanja tabele
		.antMatchers("/register-admin").hasAuthority("Admin")
		.antMatchers("/register-teacher").hasAuthority("Admin")
		.antMatchers("/register-student").hasAuthority("Admin")
		.antMatchers("/register-student").hasAuthority("Admin")
		.antMatchers("/admin-get-all-students").hasAuthority("Admin")
		.antMatchers("/admin-student").hasAuthority("Admin")
		.antMatchers("/class-assigned").hasAuthority("Admin")
		.antMatchers("/class-assigned/*").hasAuthority("Admin")
		.antMatchers("/add-course").hasAuthority("Admin")
		.antMatchers("/course-added").hasAuthority("Admin")
		.antMatchers("/edit-subject").hasAuthority("Admin")
		.antMatchers("/subject-edited").hasAuthority("Admin")
		.antMatchers("/review-requests").hasAuthority("Admin")
		.antMatchers("/request-replied/**").hasAuthority("Admin")
		.antMatchers("/get-students").hasAuthority("Teacher")
		.antMatchers("/student").hasAuthority("Teacher")
		.antMatchers("/students").hasAuthority("Teacher")
		.antMatchers("/assign-grade").hasAuthority("Teacher")
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

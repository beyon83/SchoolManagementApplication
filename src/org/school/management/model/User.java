package org.school.management.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "users")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@NotBlank(message = "*username cannot be empty.")
	@Size(min = 5, max = 20, message = "*username must contain at least 5, and max 20 characters.")
	@Pattern(regexp = "^\\w{5,20}$", message = "*username can only consist of numbers and letters.")
	private String username;

	@Transient
	@NotBlank(message = "*password cannot be empty.")
	@Size(min = 5, max = 20, message = "*password must contain at least 5, and max 20 characters.")
	@Pattern(regexp = "^\\w{5,20}$", message = "*password can only consist of numbers and letters.")
	private String passwordTransient;

	private String password;

	@NotBlank
	@Size(min = 2, max = 20, message = "*first name must contain at least 2, and max 20 characters.")
	@Pattern(regexp = "^[A-Z][a-z]{2,20}$", message = "*first name can only consist of letters (first letter must be upper case).")
	private String firstName;

	@NotBlank
	@Size(min = 2, max = 20, message = "*last name must contain at least 2, and max 20 characters.")
	@Pattern(regexp = "^[A-Z][a-z]{2,20}$", message = "*last name can only consist of letters (first letter must be upper case).")
	private String lastName;
	
	@Lob
	private byte[] profileImage;

	private boolean enabled = true;
	private String authority;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPasswordTransient() {
		return passwordTransient;
	}

	public void setPasswordTransient(String passwordTransient) {
		this.passwordTransient = passwordTransient;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public long getId() {
		return id;
	}
	
	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enabled=" + enabled + ", authority=" + authority + "]";
	}

}

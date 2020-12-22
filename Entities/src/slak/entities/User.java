package slak.entities;

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity

public class User implements Serializable {

	   
	@Id
	@GeneratedValue
	private Integer OID;
	private String name;
	private String foreName;
	private Date birthDate;
	private String email;
	private String password;
	@ManyToOne(cascade = REMOVE)
	private Company company;
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	private static final long serialVersionUID = 1L;

	public User() {
		super();
	}   
	public Integer getOID() {
		return this.OID;
	}

	public void setOID(Integer OID) {
		this.OID = OID;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getForeName() {
		return this.foreName;
	}

	public void setForeName(String foreName) {
		this.foreName = foreName;
	}   
	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}   
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}   
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
   
}

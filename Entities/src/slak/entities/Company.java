package slak.entities;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Company
 *
 */
@Entity

public class Company implements Serializable {

	
	private String name;
	private String website;   
	@Id
	private Integer OID;
	private static final long serialVersionUID = 1L;

	public Company() {
		super();
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}   
	public Integer getOID() {
		return this.OID;
	}

	public void setOID(Integer OID) {
		this.OID = OID;
	}
   
}

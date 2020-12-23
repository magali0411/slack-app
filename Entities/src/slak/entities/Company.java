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
@Table(name="Company")
public class Company implements Serializable {

	
	 @Id
	 @GeneratedValue
	 private Integer id;
	 private String name;
	 private String website;

	 public void setId(Integer id){
	    this.id = id;
	 }
	    
	 public Integer getId(){
	    return id;
	 }

	 public void setName(String name){
	    this.name = name;
	 }
	    
	 public String getName(){
	     return name;
	 }

	 public void setWebsite(String website){
	    this.website = website;
	 }
	    
	 public String getWebsite(){
	     return website;
	 }
   
}

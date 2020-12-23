package slak.entities;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.CascadeType.REMOVE;

/**
 * Entity implementation class for Entity: Channel
 *
 */
@Entity
@Table(name="Channel")
public class Channel implements Serializable {
	   
	 @Id
	 @GeneratedValue
	 private Integer id;
	 private String name;
	 private String description;
	 private boolean privat;
	 private Date dateCreation;

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

	 public void setDescription(String description){
	    this.description = description;
	 }
	    
	 public String getDescription(){
	     return description;
	 }

	 public void setPrivat(boolean privat){
	    this.privat = privat;
	 }
	    
	 public boolean getPrivat(){
	     return privat;
	 }

	 public void setDateCreation(Date dateCreation){
	    this.dateCreation = dateCreation;
	 }
	    
	 public Date getDateCreation(){
	     return dateCreation;
	 }
   
}

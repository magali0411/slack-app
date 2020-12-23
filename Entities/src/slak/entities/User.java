package slak.entities;

import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
@Table(name="User")
public class User implements Serializable {

	   
	  @Id
	    @GeneratedValue
	    private Integer id;
	    private String name;
	    private String foreName;
	    private String email;
	    private String nickName;
	    private String password;
	    private Date birthDate;
	    
	    @OneToMany(mappedBy = "emitter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    //@JsonIgnore
	    private List<Message> messages;
	    @ManyToOne
	    //@JsonBackReference
	    @JoinColumn(name = "company_fk")
	    private Company company;

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

	    public void setForeName(String foreName){
	       this.foreName = foreName;
	    }
	    
	    public String getForeName(){
	        return foreName;
	    }

	    public void setEmail(String email){
	       this.email = email;
	    }
	    
	    public String getEmail(){
	        return email;
	    }

	    public void setNickName(String nickName){
	       this.nickName = nickName;
	    }
	    
	    public String getNickName(){
	        return nickName;
	    }

	    public void setPassword(String password){
	       this.password = password;
	    }
	    
	    public String getPassword(){
	        return password;
	    }

	    public void setBirthDate(Date birthDate){
	       this.birthDate = birthDate;
	    }
	    
	    public Date getBirthDate(){
	        return birthDate;
	    }


	    public void setMessages(List<Message> messages){
	       this.messages = messages;
	    }
	    
	    public List<Message> getMessages(){
	        return messages;
	    }  

	    public void setCompany(Company company){
	       this.company = company;
	    }
	    
	    public Company getCompany(){
	        return company;
	    }
   
}

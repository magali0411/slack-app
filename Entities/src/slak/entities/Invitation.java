package slak.entities;

import java.io.Serializable;
import java.lang.Integer;
import java.util.Date;
import javax.persistence.*;
import slak.entities.User;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.DiscriminatorType.CHAR;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static javax.persistence.CascadeType.REMOVE;

/**
 * Entity implementation class for Entity: Invitation
 *
 */
@Entity

@Access(FIELD)
public class Invitation implements Serializable {

	   
	 @Id
	    @GeneratedValue
	    private Integer OID;
	    private String message;
	    private Date invitationDate;
	    private Date joinDdate;
	    @ManyToOne
	    @JoinColumn(name = "emitter_fk")
	    private User emitter;
	    @ManyToOne
	    @JoinColumn(name = "receiver_fk")
	    private Channel receiver;

	    public void setId(Integer id){
	       this.OID = id;
	    }
	    
	    public Integer getId(){
	        return OID;
	    }

	    public void setMessage(String message){
	       this.message = message;
	    }
	    
	    public String getMessage(){
	        return message;
	    }

	    public void setInvitationDate(Date invitationDate){
	       this.invitationDate = invitationDate;
	    }
	    
	    public Date getInvitationDate(){
	        return invitationDate;
	    }

	    public void setJoinDdate(Date joinDdate){
	       this.joinDdate = joinDdate;
	    }
	    
	    public Date getJoinDdate(){
	        return joinDdate;
	    }

	    public void setEmitter(User emitter){
	       this.emitter = emitter;
	    }
	    
	    public User getEmitter(){
	        return emitter;
	    }

	    public void setReceiver(Channel receiver){
	       this.receiver = receiver;
	    }
	    
	    public Channel getReceiver(){
	        return receiver;
	    }
}

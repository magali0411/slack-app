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
@NamedQueries(
{ 
   @NamedQuery(name = "allInvitations", query = "select a FROM Invitation a where a.emitter.id = :user_id"),
   @NamedQuery(name = "allInvitationsFor", query = "select a FROM Invitation a where a.receiver.id = :user_id")
})
@Table(name="Invitation")
public class Invitation implements java.io.Serializable{

    @Id
    @GeneratedValue
    private Integer id;
    private String message;
    private Date invitationDate;
    private Date joinDate;
    @ManyToOne
   // @JsonBackReference(value="emitter_b")
    @JoinColumn(name = "emitter_fk")
    private User emitter;
    @ManyToOne
  //  @JsonBackReference(value="receiver_b")
    @JoinColumn(name = "receiver_fk")
    private User receiver;

    public void setId(Integer id){
       this.id = id;
    }
    
    public Integer getId(){
        return id;
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

    public void setJoinDate(Date joinDate){
       this.joinDate = joinDate;
    }
    
    public Date getJoinDate(){
        return joinDate;
    }

    public void setEmitter(User emitter){
       this.emitter = emitter;
    }
    
    public User getEmitter(){
        return emitter;
    }

    public void setReceiver(User receiver){
       this.receiver = receiver;
    }
    
    public User getReceiver(){
        return receiver;
    }
}
package slak.entities;

import java.awt.TrayIcon.MessageType;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import javax.persistence.*;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

/**
 * Entity implementation class for Entity: Message
 *
 */
@Entity
@NamedQueries(
{ 
   @NamedQuery(name = "allMessages", query = "select a FROM Message a where a.emitter.id = :user_id")
})
@Table(name="Message")
public class Message implements java.io.Serializable{

    @Id
    @GeneratedValue
    private Integer id;
    private int parentId;
    private String content;
    private String type;
    private Date date;
    @ManyToOne
    //@JsonBackReference(value="emitter_b")
    @JoinColumn(name = "emitter_fk")
    private User emitter;
    @ManyToOne
    //@JsonBackReference(value="channel_b")
    @JoinColumn(name = "channel_fk")
    private Channel channel;

    public void setId(Integer id){
       this.id = id;
    }
    
    public Integer getId(){
        return id;
    }

    public void setParentId(int parentId){
       this.parentId = parentId;
    }
    
    public int getParentId(){
        return parentId;
    }

    public void setContent(String content){
       this.content = content;
    }
    
    public String getContent(){
        return content;
    }

    public void setType(String type){
       this.type = type;
    }
    
    public String getType(){
        return type;
    }

    public void setDate(Date date){
       this.date = date;
    }
    
    public Date getDate(){
        return date;
    }

    public void setEmitter(User emitter){
       this.emitter = emitter;
    }
    
    public User getEmitter(){
        return emitter;
    }

    public void setChannel(Channel channel){
       this.channel = channel;
    }
    
    public Channel getChannel(){
        return channel;
    }
}
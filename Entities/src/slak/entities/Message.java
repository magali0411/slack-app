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

@Inheritance(strategy = SINGLE_TABLE)
public class Message implements Serializable {

	
	private MessageType type;
	private String content;
	private Date date;   
	private User emitter;
	private Channel channel;
	
	@Id
	@GeneratedValue
	private Integer OID;
	private static final long serialVersionUID = 1L;

	public Message() {
		super();
	}   
	public MessageType getType() {
		return this.type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}   
	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}   
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}   
	public Integer getOID() {
		return this.OID;
	}

	public void setOID(Integer OID) {
		this.OID = OID;
	}
	public User getEmitter() {
		return emitter;
	}
	public void setEmitter(User emitter) {
		this.emitter = emitter;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
   
}

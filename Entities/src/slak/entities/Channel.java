package slak.entities;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.util.List;

import javax.persistence.*;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.CascadeType.REMOVE;

/**
 * Entity implementation class for Entity: Channel
 *
 */
@Entity

public class Channel implements Serializable {
	   
	@Id
	@GeneratedValue
	private Integer OID;
	private String name;
	private Boolean privateChannel;
	private String description;
	private String dateCreation;
	@OneToMany(fetch = EAGER, cascade = REMOVE)
	private List<Message> messages;
	
	private static final long serialVersionUID = 1L;

	public Channel() {
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
	public Boolean getPrivateChannel() {
		return this.privateChannel;
	}

	public void setPrivateChannel(Boolean privateChannel) {
		this.privateChannel = privateChannel;
	}   
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}   
	public String getDateCreation() {
		return this.dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
   
}

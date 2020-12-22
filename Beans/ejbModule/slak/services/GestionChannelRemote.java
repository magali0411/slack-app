package slak.services;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.Remote;

import slak.entities.Channel;
import slak.entities.Message;

@Remote
public interface GestionChannelRemote {
	
	void edit(Channel c) throws UnknownChannel;
	
	void remove(Channel c) throws UnknownChannel;
	
	void choseName(String name) throws RemoteException;
	
	void choseDescription(String desc) throws RemoteException;
	
	void create() throws RemoteException;
	
	List<Message> getMessages(Channel c) throws UnknownChannel;

}

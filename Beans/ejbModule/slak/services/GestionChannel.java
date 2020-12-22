package slak.services;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import slak.DAO.ChannelDAO;
import slak.entities.Message;
import slak.entities.Channel;

/**
 * Session Bean implementation class Channel
 */
@Stateful
@LocalBean
public class GestionChannel implements GestionChannelRemote {
	
	ChannelDAO dao;

    /**
     * Default constructor. 
     */
    public GestionChannel() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void edit(Channel c) throws UnknownChannel {
		dao.merge(c);
		
	}

	@Override
	public void choseName(String name) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void choseDescription(String desc) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Message> getMessages(Channel c) throws UnknownChannel {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Channel c) throws UnknownChannel {
		// TODO Auto-generated method stub
		//dao.delete(c);
		
	}

}

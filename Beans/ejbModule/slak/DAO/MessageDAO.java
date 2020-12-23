package slak.DAO;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import slak.entities.Message;

/**
 * Session Bean implementation class MessageDAO
 */
@Stateless
@LocalBean
public class MessageDAO {


	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	private Logger log;

	@PersistenceContext
	private EntityManager em;


	public void persist(Message message) {
		em.persist(message);
	}

	public List<Message> getMessages_() {
		return findAllOrderedByContent();
	}

	public Message getFirstMessage_() {
		return findAllOrderedByContent().get(0);
	}

	public void merge(Message message) {
		em.merge(message);
	}

	public void delete(Message message) { // FP201213
		em.remove(message);
	}

	public void deleteAll() {
		em.createQuery("DELETE FROM Message a").executeUpdate();
		em.flush();
		em.clear();
	}

	public Message findById(Integer id) {
		return em.find(Message.class, id);
	}


	public List<Message>  findByUserId(int user_id) {
		List <Message> result = (List <Message>)em.createNamedQuery("allMessages")
				.setParameter("user_id", user_id).getResultList();
		return result;
	}

	public List<Message> findAllOrderedByContent() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Message> criteria = cb.createQuery(Message.class);
		Root<Message> message = criteria.from(Message.class);
		criteria.select(message).orderBy(cb.asc(message.get("content")));
		List<Message> result = em.createQuery(criteria).getResultList();
		return result;
	}
	

	public void deleteById(int id) {
		Message fmessage = findById(id);
		delete(fmessage);
	}
   
}
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

import slak.entities.Channel;


/**
 * Session Bean implementation class ChannelDAO
 */

@Stateless
public class ChannelDAO {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	public void persist(Channel channel) {
		em.persist(channel);
	}

	public List<Channel> getChannels() {
		return findAllOrderedByName();
	}

	public Channel getFirstChannel() {
		return findAllOrderedByName().get(0);
	}

	public void merge(Channel channel) {
		em.merge(channel);
	}

	public void delete(Channel channel) { // FP201213
		em.remove(channel);
	}

	public void deleteAll() {
		em.createQuery("DELETE FROM Channel a").executeUpdate();
		em.flush();
		em.clear();
	}

	public Channel findById(Integer id) {
		return em.find(Channel.class, id);
	}

	public List<Channel> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Channel> criteria = cb.createQuery(Channel.class);
		Root<Channel> channel = criteria.from(Channel.class);
		criteria.select(channel).orderBy(cb.asc(channel.get("name")));
		List<Channel> result = em.createQuery(criteria).getResultList();
		return result;
	}

	public void deleteById(int id) {
		Channel fchannel = findById(id);
		delete(fchannel);
	}

	public Channel findByName(String string) {
		List<Channel> channels = findAllOrderedByName();
		return channels.get(0);
	}

    
}
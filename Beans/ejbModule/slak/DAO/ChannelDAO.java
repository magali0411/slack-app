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
import slak.services.UnknownChannel;


/**
 * Session Bean implementation class ChannelDAO
 */

@Stateless
public class ChannelDAO {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;
    
    
    public void register(Channel channel ) {
        if (channel.getName() == null)
            channel.setName("noname");
        log.info("Registering " + channel.getName());
        em.persist(channel);
    }    
    
    public Channel findById(Long id) {
        return em.find(Channel.class, id);
    }
    
    public List < Channel > findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery < Channel > criteria = cb.createQuery(Channel.class);
        Root < Channel > channel = criteria.from(Channel.class);
        criteria.select(channel).orderBy(cb.asc(channel.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
    public Channel create() {
        Channel  channel = new Channel();
        register(channel);
        return channel;
    }    
    
    public List < Channel > getChannels() {
        return findAllOrderedByName();
    }

    public Channel getFirstChannel() {
        return findAllOrderedByName().get(0);
    }

    public void merge(Channel channel) {
        em.merge(channel);
    }

    public void persist(Channel channel) {
        em.persist(channel);
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM Channel a").executeUpdate();
        em.flush();
        em.clear();
    }    
    
}
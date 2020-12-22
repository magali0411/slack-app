package slak.DAO;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

   @Inject
   private EntityManager em;

   @Inject
   private Logger log;
   
   
   public void register(Message message ) {
       if (message.getContent() == null)
           message.setContent("noname");
       log.info("Registering " + message.getContent());
       em.persist(message);
   }    
   
   public Message findById(Integer id) {
       return em.find(Message.class, id);
   }
   
   public List < Message > findAllOrderedByName() {
       CriteriaBuilder cb = em.getCriteriaBuilder();
       CriteriaQuery < Message > criteria = cb.createQuery(Message.class);
       Root < Message > message = criteria.from(Message.class);
       criteria.select(message).orderBy(cb.asc(message.get("name")));
       return em.createQuery(criteria).getResultList();
   }
   
   public Message create() {
       Message  message = new Message();
       register(message);
       return message;
   }    
   
   public List < Message > getMessages() {
       return findAllOrderedByName();
   }

   public Message getFirstMessage() {
       return findAllOrderedByName().get(0);
   }

   public void merge(Message message) {
       em.merge(message);
   }

   public void persist(Message message) {
       em.persist(message);
   }

   public void deleteAll() {
       em.createQuery("DELETE FROM Message a").executeUpdate();
       em.flush();
       em.clear();
   }    
   
}
package slak.DAO;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import slak.entities.User;

/**
 * Session Bean implementation class UserDAO
 */
@Stateless
@LocalBean
public class UserDAO {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;
    
    
    public void register(User user ) {
        if (user.getName() == null)
            user.setName("noname");
        log.info("Registering " + user.getName());
        em.persist(user);
    }    
    

    
    public User create() {
        User  user = new User();
        register(user);
        return user;
    }    
    
    public List < User > getUsers() {
        return findAllOrderedByName();
    }

    public User getFirstUser() {
        return findAllOrderedByName().get(0);
    }

    public void merge(User user) {
        em.merge(user);
    }

    public void persist(User user) {
        em.persist(user);
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM User a").executeUpdate();
        em.flush();
        em.clear();
    }    
    
    
    
    
    public User findById(Integer id) {
        return em.find(User.class, id);
    }

    public User findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = cb.createQuery(User.class);
        Root<User> user = criteria.from(User.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(user).where(cb.equal(user.get(User_.email), email));
        criteria.select(user).where(cb.equal(user.get("email"), email));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<User> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> criteria = cb.createQuery(User.class);
        Root<User> user = criteria.from(User.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(user).orderBy(cb.asc(user.get(User_.name)));
        criteria.select(user).orderBy(cb.asc(user.get("name")));
        return em.createQuery(criteria).getResultList();
    }

}

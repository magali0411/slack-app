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

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;


	public void persist(User user) {
		em.persist(user);
	}

	public List<User> getUsers() {
		return findAllOrderedByName();
	}

	public User getFirstUser() {
		return findAllOrderedByName().get(0);
	}

	public void merge(User user) {
		em.merge(user);
	}


	public void delete(User user) { // FP201213
		em.remove(user);
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
		User result = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = cb.createQuery(User.class);
		Root<User> user = criteria.from(User.class);
		criteria.select(user).where(cb.equal(user.get("email"), email));
		try {
			result = em.createQuery(criteria).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			clog("no result for user findByEmail");
		}
		return result;
	}

	public List<User> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = cb.createQuery(User.class);
		Root<User> user = criteria.from(User.class);
		criteria.select(user).orderBy(cb.asc(user.get("name")));
		List<User> result = em.createQuery(criteria).getResultList();
		return result;
	}

	public void deleteById(int id) {
		User fuser = findById(id);
		delete(fuser);
	}


}

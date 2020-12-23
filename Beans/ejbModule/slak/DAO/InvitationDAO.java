package slak.DAO;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import slak.entities.Invitation;


@Stateless
public class InvitationDAO {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	private Logger log;

	@PersistenceContext
	private EntityManager em;


	public void persist(Invitation invitation) {
		em.persist(invitation);
	}

	public List<Invitation> getInvitations() {
		return findAllOrderedByMessage();
	}

	public Invitation getFirstInvitation() {
		return findAllOrderedByMessage().get(0);
	}

	public void merge(Invitation invitation) {
		em.merge(invitation);
	}


	public void delete(Invitation invitation) { // FP201213
		em.remove(invitation);
	}

	public void deleteAll() {
		em.createQuery("DELETE FROM Invitation a").executeUpdate();
		em.flush();
		em.clear();
	}

	public Invitation findById(Integer id) {
		return em.find(Invitation.class, id);
	}

	public List<Invitation> findAllOrderedByMessage() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Invitation> criteria = cb.createQuery(Invitation.class);
		Root<Invitation> invitation = criteria.from(Invitation.class);
		criteria.select(invitation).orderBy(cb.asc(invitation.get("message")));
		List<Invitation> result = em.createQuery(criteria).getResultList();
		return result;
	}

	public void deleteById(int id) {
		Invitation finvitation = findById(id);
		delete(finvitation);
	}

	public List<Invitation>  findByUserId(int user_id) {
		List <Invitation> result = (List <Invitation>)em.createNamedQuery("allInvitations")
				.setParameter("user_id", user_id).getResultList();
		return result;
	}

	
	public List<Invitation>  findForUserId(int user_id) {
		List <Invitation> result = (List <Invitation>)em.createNamedQuery("allInvitationsFor")
				.setParameter("user_id", user_id).getResultList();
		return result;
	}

	public Invitation acceptById(int id, Date date) {
		Invitation inv=findById(id);
		inv.setJoinDate(date);
		em.merge(inv);
		return inv;
	}



}
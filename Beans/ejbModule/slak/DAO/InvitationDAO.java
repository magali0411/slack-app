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

import slak.entities.Invitation;



/**
 * Session Bean implementation class InvitationDAO
 */
@Stateless
@LocalBean
public class InvitationDAO {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;
    
    
    public void register(Invitation invitation ) {
        if (invitation.getMessage() == null)
            invitation.setMessage("noname");
        log.info("Registering " + invitation.getMessage());
        em.persist(invitation);
    }    
    
    public Invitation findById(Long id) {
        return em.find(Invitation.class, id);
    }
    
    public List < Invitation > findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery < Invitation > criteria = cb.createQuery(Invitation.class);
        Root < Invitation > invitation = criteria.from(Invitation.class);
        criteria.select(invitation).orderBy(cb.asc(invitation.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
    public Invitation create() {
        Invitation  invitation = new Invitation();
        register(invitation);
        return invitation;
    }    
    
    public List < Invitation > getInvitations() {
        return findAllOrderedByName();
    }

    public Invitation getFirstInvitation() {
        return findAllOrderedByName().get(0);
    }

    public void merge(Invitation invitation) {
        em.merge(invitation);
    }

    public void persist(Invitation invitation) {
        em.persist(invitation);
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM Invitation a").executeUpdate();
        em.flush();
        em.clear();
    }    
    
}
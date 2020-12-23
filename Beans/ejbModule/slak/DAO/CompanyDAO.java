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

import slak.entities.Company;

/**
 * Session Bean implementation class CompanyDAO
 */
@Stateless
@LocalBean
public class CompanyDAO {


	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	private Logger log;

	@PersistenceContext
	private EntityManager em;


	public void persist(Company company) {
		em.persist(company);
	}

	public List<Company> getCompanys() {
		return findAllOrderedByName();
	}

	public Company getFirstCompany() {
		return findAllOrderedByName().get(0);
	}

	public void merge(Company company) {
		em.merge(company);
	}


	public void delete(Company company) { // FP201213
		em.remove(company);
	}

	public void deleteAll() {
		em.createQuery("DELETE FROM Company a").executeUpdate();
		em.flush();
		em.clear();
	}

	public Company findById(Integer id) {
		return em.find(Company.class, id);
	}

	public Company findByEmail(String email) {
		Company result = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> criteria = cb.createQuery(Company.class);
		Root<Company> company = criteria.from(Company.class);
		criteria.select(company).where(cb.equal(company.get("email"), email));
		try {
			result = em.createQuery(criteria).getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			clog("no result for company findByEmail");
		}
		return result;
	}

	public List<Company> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> criteria = cb.createQuery(Company.class);
		Root<Company> company = criteria.from(Company.class);
		criteria.select(company).orderBy(cb.asc(company.get("name")));
		List<Company> result = em.createQuery(criteria).getResultList();
		return result;
	}

	public void deleteById(int id) {
		Company fcompany = findById(id);
		delete(fcompany);
	}
    
}

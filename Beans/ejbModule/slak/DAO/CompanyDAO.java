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

import slak.entities.Company;

/**
 * Session Bean implementation class CompanyDAO
 */
@Stateless
@LocalBean
public class CompanyDAO {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;
    
    
    public void register(Company company ) {
        if (company.getName() == null)
            company.setName("noname");
        log.info("Registering " + company.getName());
        em.persist(company);
    }    
    
    public Company findById(Long id) {
        return em.find(Company.class, id);
    }
    
    public List < Company > findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery < Company > criteria = cb.createQuery(Company.class);
        Root < Company > company = criteria.from(Company.class);
        criteria.select(company).orderBy(cb.asc(company.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
    public Company create() {
        Company  company = new Company();
        register(company);
        return company;
    }    
    
    public List < Company > getCompanys() {
        return findAllOrderedByName();
    }

    public Company getFirstCompany() {
        return findAllOrderedByName().get(0);
    }

    public void merge(Company company) {
        em.merge(company);
    }

    public void persist(Company company) {
        em.persist(company);
    }

    public void deleteAll() {
        em.createQuery("DELETE FROM Company a").executeUpdate();
        em.flush();
        em.clear();
    }    
    
}

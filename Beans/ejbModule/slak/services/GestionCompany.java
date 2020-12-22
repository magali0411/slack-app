package slak.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import slak.entities.Company;
import slak.DAO.CompanyDAO;


/**
 * JAX-RS
 * <p/>
 * This class produces a RESTful service to read/write the contents of the companys
 */

@Path("/companys")
@RequestScoped
public class GestionCompany {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	@Inject
	private Logger log;

	@Inject
	private Validator validator;
	

	

	@Inject
	private CompanyDAO CompanyDAO;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Company> listAllCompanys() {
		List<Company> all = CompanyDAO.findAllOrderedByName();
		List<Company> result = new ArrayList<>();
		result.addAll(all);
		return result;
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Company lookupCompanyById(@PathParam("id") int id) {
		Company company = CompanyDAO.findById(id);
		if (company == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return company;
	}

	@DELETE
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllCompanys() {
		Response.ResponseBuilder builder = null;
		try {
			CompanyDAO.deleteAll();
			builder = Response.ok();
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}
		return builder.build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCompanyById(@PathParam("id") int id) {
		Response.ResponseBuilder builder = null;
		clog("deleteCompanyById =" + id);
		try {
			CompanyDAO.deleteById(id);
			builder = Response.ok();

		} catch (ConstraintViolationException ce) {
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.toString());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}
		Response response = builder.build();
		String resp = response.toString();
		clog(resp);
		return response;
	}

	/**
	 * Creates a new company from the values provided. Performs validation, and will
	 * return a JAX-RS response with either 200 ok, or with a map of fields, and
	 * related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCompany(Company company) {
		Response.ResponseBuilder builder = null;
		if (company.getId() != -1) { // FP201213
			builder = Response.status(400).entity("company allready exists!\n");
			return builder.build();
		}
		try {
			company.setId(null);
			validateCompany(company);
			CompanyDAO.persist(company);
			builder = Response.ok().entity(company);
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<String, String>();
			if (e.toString().contains("Unique Foobar Violation"))
				responseObj.put("foobar", "Foobar taken");
			else
				responseObj.put("error", e.toString());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}
		return builder.build();
	}

	/**
	 * Updates an existing company from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of fields,
	 * and related errors.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCompany(Company company) { // FP201213
		Response.ResponseBuilder builder = null;
		try {
			Company fcompany = CompanyDAO.findById(company.getId());
			if (fcompany == null) {
				builder = Response.status(409).entity("company not found!\n");
			} else {
				validateCompany(company);
				CompanyDAO.merge(company);
				builder = Response.ok().entity(company);
			}
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<String, String>();
			if (e.toString().contains("Unique Foobar Violation"))
				responseObj.put("foobar", "Foobar taken");
			else
				responseObj.put("error", e.toString());
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<String, String>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}
		return builder.build();
	}

	/**
	 * <p>
	 * Validates the given Company variable and throws validation exceptions based on
	 * the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing company with the same foobar is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param company Company to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException          If company with the same foobar already
	 *                                      exists
	 */
	private void validateCompany(Company company) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Company>> violations = validator.validate(company);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the foobar address
		if (foobarAlreadyExists(company)) {
			throw new ValidationException("Unique Foobar Violation");
		}
	}

	/**
	 * 
	 * @param company
	 * @return true if foobar allready exists for another company
	 */
	public boolean foobarAlreadyExists(Company company) {
		Company fcompany = null;
		try {
			//fcompany = CompanyDAO.findByFoobar(company.getFoobar());
		} catch (NoResultException e) {
			// ignore
		}
		return fcompany != null && company.getId() != fcompany.getId();
	}

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation
	 * fields, and their message. This can then be used by clients to show
	 * violations.
	 * 
	 * @param violations A set of violations that needs to be reported
	 * @return JAX-RS response containing all violations
	 */
	private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
		log.fine("Validation completed. violations found: " + violations.size());
		Map<String, String> responseObj = new HashMap<String, String>();
		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
	}

}

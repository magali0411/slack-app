package slak.services;

import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
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

import slak.DAO.UserDAO;
import slak.entities.User;

/**
 * JAX-RS
 * <p/>
 * This class produces a RESTful service to read/write the contents of the users
 */

@Path("/users")
@RequestScoped
public class GestionUser {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}

	private Logger log;

	private Validator validator;
	

	@EJB
	private UserDAO userDao;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> listAllUsers() {
		List<User> all = userDao.findAllOrderedByName();
		List<User> result = new ArrayList<>();
		result.addAll(all);
		return result;
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public User lookupUserById(@PathParam("id") int id) {
		User user = userDao.findById(id);
		if (user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return user;
	}

	@DELETE
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllUsers() {
		Response.ResponseBuilder builder = null;
		try {
			userDao.deleteAll();
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
	public Response deleteUserById(@PathParam("id") int id) {
		Response.ResponseBuilder builder = null;
		clog("deleteUserById =" + id);
		try {
			userDao.deleteById(id);
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
	 * Creates a new user from the values provided. Performs validation, and will
	 * return a JAX-RS response with either 200 ok, or with a map of fields, and
	 * related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(User user) {
		Response.ResponseBuilder builder = null;
		if (user.getId() != -1) { // FP201213
			builder = Response.status(400).entity("user allready exists!\n");
			return builder.build();
		}
		try {
			user.setId(null);
			validateUser(user);
			userDao.persist(user);
			builder = Response.ok().entity(user);
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<String, String>();
			if (e.toString().contains("Unique Email Violation"))
				responseObj.put("email", "Email taken");
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
	 * Updates an existing user from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of fields,
	 * and related errors.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(User user) { // FP201213
		Response.ResponseBuilder builder = null;
		try {
			User fuser = userDao.findById(user.getId());
			if (fuser == null) {
				builder = Response.status(409).entity("user not found!\n");
			} else {
				validateUser(user);
				userDao.merge(user);
				builder = Response.ok().entity(user);
			}
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<String, String>();
			if (e.toString().contains("Unique Email Violation"))
				responseObj.put("email", "Email taken");
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
	 * Validates the given User variable and throws validation exceptions based on
	 * the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing user with the same email is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param user User to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException          If user with the same email already
	 *                                      exists
	 */
	private void validateUser(User user) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<User>> violations = validator.validate(user);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the email address
		if (emailAlreadyExists(user)) {
			throw new ValidationException("Unique Email Violation");
		}
	}

	/**
	 * 
	 * @param user
	 * @return true if emails allready exixts for another user
	 */
	public boolean emailAlreadyExists(User user) {
		User fuser = null;
		try {
			fuser = userDao.findByEmail(user.getEmail());
		} catch (NoResultException e) {
			// ignore
		}
		return fuser != null && user.getId() != fuser.getId();
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

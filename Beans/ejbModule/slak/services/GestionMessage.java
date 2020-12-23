package slak.services;


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

import slak.DAO.ChannelDAO;
import slak.DAO.MessageDAO;
import slak.DAO.UserDAO;
import slak.entities.Message;



/**
 * JAX-RS
 * <p/>
 * This class produces a RESTful service to read/write the contents of the messages
 */

@Path("/messages")
@RequestScoped
public class GestionMessage {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}


	private Logger log;


	private Validator validator;
	
	@EJB
	private UserDAO userDao;

	@EJB
	private ChannelDAO channelDao;
	

	@Inject
	private MessageDAO messageDao;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> listAllMessages() {
		List<Message> all = messageDao.findAllOrderedByContent();
		List<Message> result = new ArrayList<>();
		result.addAll(all);
		return result;
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Message lookupMessageById(@PathParam("id") int id) {
		Message message = messageDao.findById(id);
		if (message == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return message;
	}

	@GET
	@Path("/user/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> lookupMessagesByUserId(@PathParam("id") int id) {
		List<Message> messages = messageDao.findByUserId(id);
		if (messages == null || messages.isEmpty()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return messages;
	}	
	
	
	@DELETE
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllMessages() {
		Response.ResponseBuilder builder = null;
		try {
			messageDao.deleteAll();
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
	public Response deleteMessageById(@PathParam("id") int id) {
		Response.ResponseBuilder builder = null;
		clog("deleteMessageById =" + id);
		try {
			messageDao.deleteById(id);
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
	 * Creates a new message from the values provided. Performs validation, and will
	 * return a JAX-RS response with either 200 ok, or with a map of fields, and
	 * related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createMessage(Message message) {
		Response.ResponseBuilder builder = null;
		if (message.getId() != -1) { // FP201213
			builder = Response.status(400).entity("message allready exists!\n");
			return builder.build();
		}
		try {
			message.setId(null);
			validateMessage(message);
			messageDao.persist(message);
			builder = Response.ok().entity(message);
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
	 * Updates an existing message from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of fields,
	 * and related errors.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMessage(Message message) { // FP201213
		Response.ResponseBuilder builder = null;
		try {
			Message fmessage = messageDao.findById(message.getId());
			if (fmessage == null) {
				builder = Response.status(409).entity("message not found!\n");
			} else {
				validateMessage(message);
				messageDao.merge(message);
				builder = Response.ok().entity(message);
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
	 * Validates the given Message variable and throws validation exceptions based on
	 * the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing message with the same foobar is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param message Message to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException          If message with the same foobar already
	 *                                      exists
	 */
	private void validateMessage(Message message) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Message>> violations = validator.validate(message);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the foobar address
		if (foobarAlreadyExists(message)) {
			throw new ValidationException("Unique Foobar Violation");
		}
	}

	/**
	 * 
	 * @param message
	 * @return true if foobar allready exists for another message
	 */
	public boolean foobarAlreadyExists(Message message) {
		Message fmessage = null;
		try {
			//fmessage = messageDao.findByFoobar(message.getFoobar());
		} catch (NoResultException e) {
			// ignore
		}
		return fmessage != null && message.getId() != fmessage.getId();
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

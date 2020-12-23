package slak.services;

import java.util.ArrayList;
import java.util.Date;
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

import slak.entities.Invitation;
import slak.entities.User;
import slak.DAO.UserDAO;
import slak.DAO.InvitationDAO;



/**
 * JAX-RS
 * <p/>
 * This class produces a RESTful service to read/write the contents of the invitations
 */

@Path("/invitations")
@RequestScoped
public class GestionInvitation {

	private static final boolean LOG = true;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
	}


	private Logger log;

	private Validator validator;
		

	@EJB
	private InvitationDAO InvitationDAO;

	@EJB
	private UserDAO UserDAO;

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Invitation> listAllInvitations() {
		List<Invitation> all = InvitationDAO.findAllOrderedByMessage();
		List<Invitation> result = new ArrayList<>();
		result.addAll(all);
		return result;
	}
	

	
	@GET
	@Path("/user/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Invitation> lookupInvitationsByUserId(@PathParam("id") int id) {
		List<Invitation> invitations = InvitationDAO.findByUserId(id);
		if (invitations == null || invitations.isEmpty()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return invitations;
	}
	
	
	@GET
	@Path("/foruser/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Invitation> lookupInvitationsForUserId(@PathParam("id") int id) {
		List<Invitation> invitations = InvitationDAO.findForUserId(id);
		if (invitations == null || invitations.isEmpty()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return invitations;
	}
	


	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Invitation lookupInvitationById(@PathParam("id") int id) {
		Invitation invitation = InvitationDAO.findById(id);
		//TODO see invitation.emitter
		if (invitation == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return invitation;
	}

	@DELETE
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllInvitations() {
		Response.ResponseBuilder builder = null;
		try {
			InvitationDAO.deleteAll();
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
	public Response deleteInvitationById(@PathParam("id") int id) {
		Response.ResponseBuilder builder = null;
		clog("deleteInvitationById =" + id);
		try {
			InvitationDAO.deleteById(id);
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

	
	private void updateUsers(Invitation invitation) {
		try {
			User emiter = UserDAO.findById(invitation.getEmitter().getId());
			invitation.setEmitter(emiter);
		} catch (Exception e) {
			clog("should not happen 1 in updateUsers");
		}

		
		try {
			User receiver =UserDAO.findById(invitation.getReceiver().getId());
			invitation.setReceiver(receiver);
		} catch (Exception e) {
			clog("should not happen 2 in updateUsers");
		}		
	
	}
	
	
	/**
	 * Creates a new invitation from the values provided. Performs validation, and will
	 * return a JAX-RS response with either 200 ok, or with a map of fields, and
	 * related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInvitation(Invitation invitation) {
		Response.ResponseBuilder builder = null;
		if (invitation.getId() != -1) { // FP201213
			builder = Response.status(400).entity("invitation allready exists!\n");
			return builder.build();
		}
		try {
			invitation.setId(null);
			updateUsers(invitation);
			validateInvitation(invitation);
			InvitationDAO.persist(invitation);
			builder = Response.ok().entity(invitation);
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

	@PUT
	@Path("/accept/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Invitation acceptInvitation(@PathParam("id") int id) {
		Invitation invitation = InvitationDAO.acceptById(id,new Date());

	
		return invitation;
	}
	

	
	/**
	 * Updates an existing invitation from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of fields,
	 * and related errors.
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInvitation(Invitation invitation) { // FP201213
		Response.ResponseBuilder builder = null;
		try {
			Invitation finvitation = InvitationDAO.findById(invitation.getId());
			if (finvitation == null) {
				builder = Response.status(409).entity("invitation not found!\n");
			} else {
				updateUsers(invitation);
				validateInvitation(invitation);
				InvitationDAO.merge(invitation);
				builder = Response.ok().entity(invitation);
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
	 * Validates the given Invitation variable and throws validation exceptions based on
	 * the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing invitation with the same foobar is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param invitation Invitation to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException          If invitation with the same foobar already
	 *                                      exists
	 */
	private void validateInvitation(Invitation invitation) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Invitation>> violations = validator.validate(invitation);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the foobar address
		if (foobarAlreadyExists(invitation)) {
			throw new ValidationException("Unique Foobar Violation");
		}
	}

	/**
	 * 
	 * @param invitation
	 * @return true if foobar allready exists for another invitation
	 */
	public boolean foobarAlreadyExists(Invitation invitation) {
		Invitation finvitation = null;
		try {
			//finvitation = InvitationDAO.findByFoobar(invitation.getFoobar());
		} catch (NoResultException e) {
			// ignore
		}
		return finvitation != null && invitation.getId() != finvitation.getId();
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

//TODO synchro selectbox
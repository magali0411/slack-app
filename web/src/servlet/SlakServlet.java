package servlet;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import imt.helpers.ResourceFileHelper;
import slak.DAO.ChannelDAO;
import slak.DAO.CompanyDAO;
import slak.DAO.InvitationDAO;
import slak.DAO.MessageDAO;
import slak.DAO.UserDAO;
import slak.entities.Channel;
import slak.entities.Invitation;
import slak.entities.Message;
import slak.entities.User;

@WebServlet({ "/helo", "/test7845", "/populate1254" })
public class SlakServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final boolean LOG = true;
	private static final Logger log = Logger.getLogger(SlakServlet.class.getName());

	// private static final boolean USE_WEBSOCKET_ = true; // FP191203

	private static DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	//@Inject
	private UserDAO userDAO;

	//@Inject
	private InvitationDAO invitationDAO;

	//@Inject
	private ChannelDAO channelDAO;

	//@Inject
	private CompanyDAO companyDAO;

	//@Inject
	private MessageDAO messageDAO;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
		// System.out.println(mesg);
	}

	private static void sclog(String m) {
		System.out.println(m);
	}

	@Override
	public void init() throws ServletException {
		super.init();
		ResourceFileHelper.getInstance().setUploadDir(this.getServletContext().getRealPath("/"));
		clog("init ok");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if ("/helo".equals(path)) {
			response.getWriter().println("hi");
			List<User> users = userDAO.getUsers();
			if (users.isEmpty())
				 populate();
		}
	    else
			response.getWriter().println("unknown");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
        if ("/populate1254".equals(path)) {
			String result = populate();
			response.getWriter().println("populate done : " + result);
		} else if ("/test7845".equals(path)) {
			String result = testMessage();
			response.getWriter().println("test done : " + result);
		} else
			response.getWriter().println("unknown");
	}
	

	private String populate() {
		clog("populating");
		messageDAO.deleteAll();
		invitationDAO.deleteAll();
		channelDAO.deleteAll();
		userDAO.deleteAll();
		User usermartin = createUser("Martin", "Peter", "johnny", "qwerty", "ji@jiji.com");
		User usersmith = createUser("Smith", "Jo", "jamesbond", "azerty", "jo@jojo.com");
		User usertom = createUser("Tom", "Tim", "ken", "foo", "ko@kojo.com");
		Channel channel_dev = createChannel("Development", "this channel is about dev", false);
		Channel channel_devops = createChannel("Devops", "this channel is about devops", false);
		Invitation invitation1 = sendInvitation(usermartin, usersmith, "Hello Jo Smith join Slak");
		Invitation invitation2 = sendInvitation(usersmith, usermartin, "Hello Peter Martin join Slak");
		Invitation invitation3 = sendInvitation(usersmith, usertom, "Hello Tim Tom join Slak");
		Message message_martin_dev1 = sendMessage(usermartin, channel_dev, "this is the message content 1 about java foo", "DEV_JAVA");
		Message message_martin_dev2 = sendMessage(usermartin, channel_dev, "this is the message content 2 about java bar", "DEV_JAVA");
		Message message_martin_devops3 = sendMessage(usermartin, channel_devops, "this is the message content 3 about java baz", "OPS_JAVA");
		Message message_martin_dev4 = sendMessage(usermartin, channel_dev, "this is the message content 4 about java djin", "DEV_JAVA");
		Message message_smith_devops1 = sendMessage(usersmith, channel_devops, "this is the message content 1 about node foo", "OPS_NODE");
		Message message_smith_dev2 = sendMessage(usersmith, channel_dev, "this is the message content 2 about node foo", "DEV_NODE");
		clog("populate ok");
		return "populate ok";
	}

	private String testMessage() {
		String json = "{\"testMessage\",\"error\"}";
		User u = userDAO.findByEmail("ji@jiji.com");
		Channel c = channelDAO.findByName("Development");
		Message message = sendMessage(u, c, "the test2 is ok", "DEV_JAVA");
		//try {
		//	json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message);
		//} catch (JsonProcessingException e) {
		//	e.printStackTrace();
		//}
		return json;
	}

//"Development","this channel is about dev",false,
	private Channel createChannel(String name, String description, boolean privat) {
		Channel channel = new Channel();
		channel.setName(name);
		channel.setDescription(description);
		channel.setDateCreation(new Date());
		channel.setPrivat(privat);
		channelDAO.persist(channel);
		return channel;
	}

	private User createUser(String name, String forename, String nickname, String password, String email) {
		User user = new User();
		user.setName(name);
		user.setForeName(forename);
		user.setNickName(nickname);
		user.setPassword(password);
		user.setEmail(email);
		userDAO.persist(user);
		return user;
	}


	private Invitation sendInvitation(User from, User to, String message) {
		Invitation invitation = new Invitation();
		invitation.setMessage(message);
		invitation.setInvitationDate(new Date());
		invitation.setEmitter(from);
		invitation.setReceiver(to);
		invitationDAO.persist(invitation);
		return invitation;
	}


	private Message sendMessage(User from, Channel channel, String content, String type) {
		Message message = new Message();
		message.setChannel(channel);
		message.setContent(content);
		message.setDate(new Date());
		message.setEmitter(from);
		message.setType(type);
		messageDAO.persist(message);
		return message;
	}

}


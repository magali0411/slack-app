package imt.rest;

import java.io.IOException;




import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import imt.helpers.ResourceFileHelper;

import org.jboss.logging.Logger;





@Path("/fragments")
public class FragmentsResource {

	public static final boolean LOG = true;
	private static final Logger log = Logger.getLogger( FragmentsResource.class.getName());

	
	
	//@Inject
//	private Logger log;

	public void clog(String mesg) {
		if (LOG)
			log.info(mesg);
			//System.out.println(mesg);
	}


	
	@GET
	@Path("/navbar/{page}")
	public String getFilebyPath(@PathParam("page") String page) {
		 ResourceFileHelper rfh = ResourceFileHelper.getInstance();
		 try {

				return rfh.readFragment("fragments/"+page);
		} catch (IOException e) {
			
			e.printStackTrace();
			return "error";
		} 
	}
	

	@GET
	@Path("/view/{id}")
	public String getView(@PathParam("id") String id) {
		 ResourceFileHelper rfh = ResourceFileHelper.getInstance();
		 try {
				return rfh.readView(id);
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		} 
	}
	
	@GET
	@Path("/script/{id}")
	public String getScript(@PathParam("id") String id) {
		 ResourceFileHelper rfh = ResourceFileHelper.getInstance();
		 try {
				return rfh.readScript(id);
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		} 
	}

}

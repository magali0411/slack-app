package imt.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;



public class ResourceFileHelper {


	private static final String SERVER_DIR = File.separator+"WEB-INF"+File.separator+"classes"+File.separator;
	private static final String FRAGMENT_DIR = SERVER_DIR + File.separator+"fragments"+File.separator;
	private static final String VIEW_DIR = SERVER_DIR +"views"+File.separator;
	private static final String SCRIPT_DIR = SERVER_DIR +"scripts"+File.separator;
	
	

	private String uploadDir;


	static ResourceFileHelper instance;
	
	
	public static ResourceFileHelper getInstance() {
		if (instance == null)
			instance = new ResourceFileHelper();
		return instance;
	}

	private String readFragmentFromResource(InputStream fis) throws IOException {
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line + "\n";
			}
		}
		return result;
	}

	private String readDataFromResource(InputStream fis) throws IOException {
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line + "\n";
			}
		}
		return result;
	}

	private List<String> readFromResource(InputStream iStream) throws IOException {
		List<String> result = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result.add(line + "\n");
			}
		}
		return result;
	}

	public String readFile(String path) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);
		InputStream iStream = resource.openStream();
		return readDataFromResource(iStream);
	}

	/**
	 * usage readFile("data/draw/draw.csv");
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String readFile__(String path) throws IOException {
		File f = getDataResource(path);
		FileInputStream iStream = new FileInputStream(f);
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String readFragment(String path) throws IOException {
		String ud = getUploadDir();
		ud = ud + FRAGMENT_DIR + "navbar.html";// fragments";
		File f = new File(ud);
		FileInputStream iStream = new FileInputStream(f);
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		}
		return result;

	}
	
	
	public String readView(String path) throws IOException {
		if (path.equals("view_runners"))
			path = "runners.html";
		String ud = getUploadDir();
		ud = ud + VIEW_DIR + path;
		File f = new File(ud);
		FileInputStream iStream = new FileInputStream(f);
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		}
		return result;
	}
	
	
	public String readScript(String path) throws IOException {
		if (path.equals("script_runners"))
			path = "runners_js";
		String ud = getUploadDir();
		ud = ud + SCRIPT_DIR + path;
		File f = new File(ud);
		FileInputStream iStream = new FileInputStream(f);
		String result = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		}
		return result;
	}
	

	public String read(String path) {
		try {
			return readFile(path);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public List<String> readFromResources(String path) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);
		InputStream iStream = resource.openStream();
		return readFromResource(iStream);
	}

	public File getFile_nu_(String path) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(path);
		InputStream iStream = resource.openStream();
		File result = getAsset(iStream, path);
		return result;
	}

	public List<String> readList(String path) {
		try {
			return readFromResources(path);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	public void save(String path, String filename) {
		// TODO Auto-generated method stub

	}

	protected void saveLog(String filename, List<String> testlog) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource(filename);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}


	private File getAsset(InputStream input, String targetFileName) throws IOException {
		String uploadPath = getUploadDir();// f.getAbsolutePath() + File.separator + UPLOAD_DIRECTORY;
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		if (targetFileName.contains("/"))
			targetFileName = targetFileName.replace("/", "_");
		String filePath = uploadPath + File.separator + targetFileName;
		File storeFile = new File(filePath);
		if (!storeFile.exists()) {
			OutputStream os = null;
			try {
				os = new FileOutputStream(storeFile);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = input.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
			} finally {
				input.close();
				os.close();
			}

		}
		return storeFile;

	}

	private String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public File getFile(String path) {
		String p = uploadDir + File.separator + path;
		return new File(p);
	}

	public String getUploadResource(String path) {
		String r = "";
		try {
			String p = uploadDir + File.separator + path;
			File x = new File(p);
			FileInputStream iStream;
			iStream = new FileInputStream(x);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(iStream, StandardCharsets.UTF_8));) {
				String line;
				while ((line = br.readLine()) != null) {
					r += line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return r;
	}

	public File getDataResource(String fileName) {
		String p = uploadDir + File.separator + "WEB-INF" + File.separator + "classes" + File.separator + "data"
				+ File.separator + fileName;
		return new File(p);
	}

/**
http://www.mastertheboss.com/jboss-frameworks/maven-tutorials/jboss-maven/building-a-maven-archetype-for-a-java-enterprise-project
https://blog.avenuecode.com/simple-jaxrs-application-with-cdi-javaee
 */

}

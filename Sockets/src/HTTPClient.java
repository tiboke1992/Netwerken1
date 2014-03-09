import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//TODO opkuisen en een extra commando da vraagt als ge put of post gebruikt

public class HTTPClient {

	private String[] args;
	private String command;
	private String version;
	private String uri;
	private String host;
	private String path;
	private int port;


	public HTTPClient(String[] args) {
		if (args.length != 4) {
			throw new IllegalArgumentException("Wrong number of arguments");
		} else {
			setArgs(args);
			initialize();
			start();
		}
	}

	/*
	 * GetPath receives an array of string which contain the complete url and
	 * uses that string to get the path e.g www.test.com/index.html will return
	 * /index.html
	 */
	public String getPath(String[] l) {
		String result = "";
		for (int i = 1; i < l.length; i++) {
			result += "/" + l[i];
		}
		return result;
	}

	public void initialize() {
		String[] arguments = this.getArguments();
		setCommand(arguments[0]);
		setUri(arguments[1]);
		String[] urls = uri.split("/");
		setHost(urls[0]);
		//this is needed for splitting the url 
		if (urls.length == 1) {
			setPath("/");
		} else {
			setPath(getPath(urls));
		}
		setPort(Integer.parseInt(arguments[2]));
		setVersion(arguments[3]);
	}

	public void start() {
		Socket socket = null;
		try {
			socket = new Socket(getHost(), getPort());
			OutputStream out = socket.getOutputStream();
			PrintWriter outwriter = new PrintWriter(out, false);
			outwriter.print(getCommand() + " " + getPath() + " HTTP/"
					+ getVersion() + "\n");

			if (getVersion().equals("1.1")) {
				outwriter.print("Host: " + getHost() + "\n");
				// If we use 1.1 it's important to make sure the connection
				// closes
				// Else you have to wait until the connection times out?
				outwriter.print("Connection: close" + "\n");
			}
			outwriter.print("Accept: text/plain, text/html, text/*\n");
			outwriter.print("User-Agent: Mozilla/5.0 \n");
			if(getCommand().toUpperCase().equals("POST")){
				System.out.println("Give parameters for POST:");
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
				String ag = "";
				try{
					ag = input.readLine();
				}catch(IOException io){
					System.out.println("Cant use those arguments");
				}
				outwriter.print("Content-Type: application/x-www-form-urlencoded" + "\n");
				outwriter.print("Content-Length: " + ag.length() + "\n");
				System.out.println("Content-Length: " + ag.length() + "\n");
				outwriter.print("\n");
				outwriter.print(ag);
				outwriter.print("\n");
			}
			outwriter.print("\n");
			outwriter.flush();

			InputStream in = socket.getInputStream();
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(inr);
			String line = "";
			while ((line = buffer.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("Finnished reading");
			saveTheImages(getHost() + getPath());

		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
		} catch (IOException e) {
			System.out.println("Cant acces this website");
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ioEx) {
				System.out.println("Problems closing the socket");
			}

		}
	}


	/*
	 * This method will check if a given URL contains any images. It uses the
	 * Jsoop framework to get the source from the image tags. It then saves all found images
	 * to the project folder
	 */
	public void saveTheImages(String url) {
		try {
			System.out.println("Checking if there are any images on this page");
			Document doc = Jsoup.connect("http://" + url).get();
			Elements img = doc.getElementsByTag("img");
			int counter = 1;
			for (Element el : img) {
				String src = el.absUrl("src");
				System.out.println(src);
				BufferedImage image = null;
				try {
					URL uri = new URL(src);
					image = ImageIO.read(uri);
					if (image != null) {
						File outputfile = new File("saved" + counter + ".png");
						ImageIO.write(image, "png", outputfile);
						counter++;
						System.out.println("Image saved");
					}
				} catch (IOException e) {
					System.out
							.println("something went wrong while trying to read this image");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Getters and setters
	 * 
	 */
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}

	public String[] getArguments() {
		return this.args;
	}
}

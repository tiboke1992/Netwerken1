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

	public HTTPClient(String[] args) {
		if (args.length != 4) {
			throw new IllegalArgumentException("Wrong number of arguments");
		} else {
			setArgs(args);
			start();
		}
	}

	/*
	 * GetPath receives an array of string which contain the complete url and
	 * uses that string to get the path e.g www.test.com/index.html will return
	 * /index.html
	 */
	public String GetPath(String[] l) {
		String result = "";
		for (int i = 1; i < l.length; i++) {
			result += "/" + l[i];
		}
		return result;
	}

	public void start() {
		String[] arguments = this.getArguments();
		String command = arguments[0];
		String uri = arguments[1];
		String[] urls = uri.split("/");
		String host = urls[0];
		String path = "";
		if (urls.length == 1) {
			path = "/";
		} else {
			path = GetPath(urls);
		}

		String port = arguments[2];
		int iPort = Integer.parseInt(port);
		String version = arguments[3];
		Socket socket = null;
		try {
			socket = new Socket(host, iPort);
			OutputStream out = socket.getOutputStream();
			PrintWriter outwriter = new PrintWriter(out, false);
			outwriter.print(command + " " + path + " HTTP/" + version + "\n");
			/*
			 * If the version is 1.1 we need to: 1. include the Host: header
			 * with each request 2. accept responses with chunked data 3.include
			 * the "Connection: close" header with each request 4.Handle the 100
			 * Continue response
			 */
			if (version.equals("1.1")) {
				outwriter.print("Host: " + host + "\n");
				// outwriter.print("Connection: close" + "\n");
			}
			outwriter.print("Accept: text/plain, text/html, text/*\n");
			outwriter.print("User-Agent: Mozilla/5.0 \n");
			outwriter.print("\n");
			outwriter.flush();

			InputStream in = socket.getInputStream();
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(inr);
			String line = "";
			while ((line = buffer.readLine()) != null) {
				System.out.println(line);
			}
			buffer.close();
			saveTheImages(host + path);

		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
		} catch (IOException e) {
			System.out.println("IO error");
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ioEx) {
			}

		}
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String[] getArguments() {
		return this.args;
	}

	public void saveTheImages(String url) {
		try {
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
							.println("something went wrong while trying to read this images");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

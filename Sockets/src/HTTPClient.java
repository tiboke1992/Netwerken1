import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

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
	 * GetPath receives an array of string which contain the complete url
	 * and uses that string to get the path e.g www.test.com/index.html
	 * will return /index.html
	 */
	public String GetPath(String[] l){
		String result = "";
		System.out.println(l.length);
		for(int i = 1 ; i < l.length ; i++){
			result += "/" + l[i];
		}
		System.out.println(result);
		return result;
	}
	
	public void start(){
		String[] arguments = this.getArguments();
		String command = arguments[0];
		String uri = arguments[1];
		String[] urls = uri.split("/");
		String host = urls[0];
		System.out.println(host);
		String path = GetPath(urls);
		String port = arguments[2];
		int iPort = Integer.parseInt(port);
		String version = arguments[3];
		Socket socket = null;
		try {
			socket = new Socket(host, iPort);
			System.out.println("Host : " + host  + " Port: " + iPort);
			System.out.println("Path : " + path);
			OutputStream out = socket.getOutputStream();
			PrintWriter outwriter = new PrintWriter(out, false);
			outwriter.print(command + " " + path + " HTTP/" + version
					+ "\r\n");
			outwriter.print("Accept: text/plain, text/html, text/*\r\n");
			outwriter.print("\r\n");
			outwriter.flush();

			InputStream in = socket.getInputStream();
			InputStreamReader inr = new InputStreamReader(in);
			BufferedReader buffer = new BufferedReader(inr);
			String line;
			while ((line = buffer.readLine()) != null) {
				System.out.println(line);
			}
			buffer.close();

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
	
	public void setArgs(String[] args){
		this.args = args;
	}
	
	public String[] getArguments(){
		return this.args;
	}
	
	
}

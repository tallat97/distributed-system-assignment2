import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
public class MyClient {

	static DataInputStream din;
	static PrintStream dout;
	static Socket s;
	
	static String response;
	static int nRec = 0, nLen = 0;
	
	static String largest=" ";
	
	public static void main(String[] args) throws Exception {
		s = new Socket("localhost", 50000);
		din = new DataInputStream(s.getInputStream());
		dout = new PrintStream(s.getOutputStream(), true);
		send("HELO");
		send("AUTH " + System.getProperty("user.name"));
		send("REDY");
		
		send("QUIT");
	}
	
	
	//	Extract information about Job into an integer array
	public static int[] getJobInfo() {
		String[] info = response.split(" ");
		int[] data = new int[info.length - 1];
		for (int i = 1; i < info.length; i++) {
			data[i - 1] = Integer.parseInt(info[i]);
		}
		return data;
	}
	//	Send message to server
	public static void send(String str) throws Exception {
		dout.print(str+"\n");
		dout.flush();
		System.out.println("Client: " + str);
		receive();
	}
	//	Read message from server
	public static void receive() throws Exception {
		int SIZE = Math.max(1000, nRec * nLen + 1);
		byte[] bytes = new byte[SIZE];
		din.read(bytes);
		String str = new String(bytes, StandardCharsets.UTF_8);
		System.out.println("Server: "+str);
		response = str.trim();
	}
}


import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
public class MyClient {

	static String response;
	static int nRec = 0, nLen = 0;

	static DataInputStream inputStream;
	static PrintStream outputStream;
	static Socket s;
	
	
	static String largest=" ";
	
	public static void main(String[] args) throws Exception {
		s = new Socket("localhost", 50000);
		outputStream = new PrintStream(s.getOutputStream(), true);
		inputStream = new DataInputStream(s.getInputStream());
		sendFun("HELO");
		sendFun("AUTH " + System.getProperty("user.name"));
		sendFun("REDY");
		while (!(response.equals("NONE"))) {
			if (!(response.startsWith("JOBN") || response.startsWith("JOBP"))) {
				if (response.startsWith("JCPL")) {
					sendFun("REDY");
				}
				continue;
			}
			int[] data = getJobInfo();
			if(largest.equals(" ")){
				largest=findLargestFun();
			}
			sendFun("SCHD "+data[1]+" " + largest);
			sendFun("REDY");
		}
		
		sendFun("QUIT");
		outputStream.close();
		inputStream.close();
		s.close();
	}
	
	public static String findLargestFun() throws Exception{
		sendFun("GETS All");
		String temp=response;
		String[] data=temp.split(" ");
		nRec = Integer.parseInt(data[1]);
		nLen = Integer.parseInt(data[2]);
		sendFun("OK");
		temp = response;
		sendFun("OK");
		String[] serv = temp.split("\n");
		String name="";
		int disk_size=0;
		for(String i : serv) {
			String[] j=i.split(" ");
			String n = j[0];
			int ds = Integer.parseInt(j[4].trim());
			if(ds>disk_size) {
				name = n;
				disk_size=ds;
			}
		}
		return name+" 0";
	}
	
	public static int[] getJobInfo() {
		String[] info = response.split(" ");
		int[] data = new int[info.length - 1];
		for (int i = 1; i < info.length; i++) {
			data[i - 1] = Integer.parseInt(info[i]);
		}
		return data;
	}
	
	public static void sendFun(String str) throws Exception {
		outputStream.print(str+"\n");
		outputStream.flush();
		receiveFun();
	}
	
	public static void receiveFun() throws Exception {
		int SIZE = Math.max(1000, nRec * nLen + 1);
		byte[] bytes = new byte[SIZE];
		inputStream.read(bytes);
		String str = new String(bytes, StandardCharsets.UTF_8);
		response = str.trim();
	}
}


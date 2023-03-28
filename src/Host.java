import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Host {
	
	private DatagramSocket sendReceiveSocket; //to from server
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket; //to client
	private DatagramSocket receiveSocket; //from client
	
	private InetAddress clientAddress;
	private int clientPort;
	
	/**
	 * constructor for Host
	 */
	public Host() {
		try {
	        sendReceiveSocket = new DatagramSocket();
	        receiveSocket = new DatagramSocket(23);
	        receiveSocket.setSoTimeout(10000);
	        sendReceiveSocket.setSoTimeout(10000);
		} catch (SocketException se) { 
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Send and receive messages to/from server
	 * @param messageData - byte[] to relay to server
	 */
	public void sendAndReceive(DatagramPacket packet) {
		
		System.out.println(packet.getLength());
		try {
	         sendPacket = new DatagramPacket(packet.getData(), packet.getLength(),
	                                         InetAddress.getLocalHost(), 69);   
		} catch (UnknownHostException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
 	  
	      try {
	    	  sendReceiveSocket.send(sendPacket);
		  } catch (IOException e) {
		      e.printStackTrace();
		      System.exit(1);
		  }
	      
	      System.out.println("Host Sending to server");
	      Utils.printPacketInfo(sendPacket);

	      byte data[] = new byte[100];
	      receivePacket = new DatagramPacket(data, data.length);

	      try {
	         // Block until a datagram is received via sendReceiveSocket.  
	         sendReceiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	      
	      System.out.println("Host received from server");
	      Utils.printPacketInfo(receivePacket);
	      
	      send(receivePacket);
	}
	
	/**
	 * send to client
	 * @param response - DatagramPacket of response to send to client
	 */
	public void send(DatagramPacket response) {
		
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("sending back to client...\n");

		DatagramPacket toClient = new DatagramPacket(response.getData(), response.getLength(), clientAddress, clientPort);
		 try {
	    	 sendSocket.send(toClient);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
		 System.out.println("sent to client");
		 Utils.printPacketInfo(toClient);
		 sendSocket.close();
	}
	
	/**
	 * receive messages from client
	 */
	public void receive() {
		byte data[] = new byte[100];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
 
		try {
			 receiveSocket.receive(receivePacket);
			 clientAddress = receivePacket.getAddress();
			 clientPort = receivePacket.getPort();
		} catch(IOException e) {
		     e.printStackTrace();
		     System.exit(1);
		}
		
		 
		Utils.printPacketInfo(receivePacket);
		sendAndReceive(receivePacket);   
	}
	
	public static void main(String[] args) {
		Host h = new Host();
		while(true) {
			h.receive();
		}
	}
	
}
